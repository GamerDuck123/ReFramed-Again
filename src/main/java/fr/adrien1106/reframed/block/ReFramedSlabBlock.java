package fr.adrien1106.reframed.block;

import fr.adrien1106.reframed.ReFramed;
import fr.adrien1106.reframed.util.blocks.BlockHelper;
import fr.adrien1106.reframed.util.blocks.Corner;
import fr.adrien1106.reframed.util.blocks.Edge;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.level.BlockGetter;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

import static fr.adrien1106.reframed.util.blocks.BlockProperties.*;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.*;

public class ReFramedSlabBlock extends WaterloggableReFramedBlock {

	protected static final VoxelShape DOWN = Shapes.box(0f, 0f, 0f, 1f, 0.5f, 1f);
	protected static final VoxelShape UP = Shapes.box(0f, 0.5f, 0f, 1f, 1f, 1f);
	protected static final VoxelShape NORTH = Shapes.box(0f, 0f, 0f, 1f, 1f, 0.5f);
	protected static final VoxelShape SOUTH = Shapes.box(0f, 0f, 0.5f, 1f, 1f, 1f);
	protected static final VoxelShape EAST = Shapes.box(0.5f, 0f, 0f, 1f, 1f, 1f);
	protected static final VoxelShape WEST = Shapes.box(0f, 0f, 0f, 0.5f, 1f, 1f);

	public ReFramedSlabBlock(Properties settings) {
		super(settings);
		registerDefaultState(defaultBlockState().setValue(FACING, Direction.DOWN));
	}
	
	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder.add(FACING));
	}

	@Override
    @SuppressWarnings("deprecation")
	public boolean canBeReplaced(BlockState state, BlockPlaceContext context) {
        if (context.getPlayer() == null
            || context.getPlayer().isShiftKeyDown()
            || !(context.getItemInHand().getItem() instanceof BlockItem block_item)
        ) return false;

        // allow replacing with slab, step, small cube and half stair
        Block block = block_item.getBlock();
        if (block != this
            && block != ReFramed.STEP
            && block != ReFramed.SMALL_CUBE
            && block != ReFramed.HALF_STAIR
            && block != ReFramed.HALF_LAYER
            && block != ReFramed.LAYER
        ) return false;

        // check if the player is clicking on the inner part of the block
		return matchesShape(
            context.getClickLocation(),
            context.getClickedPos(),
            state.setValue(FACING, state.getValue(FACING).getOpposite())
        );
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext ctx) {
		BlockState current_state = ctx.getLevel().getBlockState(ctx.getClickedPos());

		if (current_state.is(this))
			return ReFramed.SLABS_CUBE.defaultBlockState()
				.setValue(AXIS, current_state.getValue(FACING).getAxis());

        if (current_state.is(ReFramed.HALF_LAYER)) {
            Edge edge = current_state.getValue(EDGE);
            Direction face = edge.getDirection(current_state.getValue(EDGE_FACE));
            edge = edge.getOpposite(face);
            return ReFramed.SLABS_HALF_LAYER.defaultBlockState()
                .setValue(EDGE, edge)
                .setValue(EDGE_FACE, edge.getDirectionIndex(edge.getOtherDirection(face)))
                .setValue(LAYERS, current_state.getValue(LAYERS))
                .setValue(WATERLOGGED, current_state.getValue(WATERLOGGED));
        }

        if (current_state.is(ReFramed.HALF_STAIR)) {
            Corner corner = current_state.getValue(CORNER);
            Direction face = corner.getDirection(current_state.getValue(CORNER_FACE));
            corner = corner.change(face);
            return ReFramed.SLABS_INNER_STAIR.defaultBlockState()
                .setValue(CORNER, corner)
                .setValue(CORNER_FACE, corner.getDirectionIndex(face.getOpposite()))
                .setValue(WATERLOGGED, current_state.getValue(WATERLOGGED));
        }

        if (current_state.is(ReFramed.STEP)) {
            Edge edge = current_state.getValue(EDGE),
                placed = BlockHelper.getPlacementEdge(ctx),
                new_edge;
            Direction face = edge.getFirstDirection(),
                other = edge.getSecondDirection().getOpposite();

            if (!ReFramed.STEP.matchesShape(
                    ctx.getClickLocation(),
                    ctx.getClickedPos(),
                    current_state.setValue(EDGE, new_edge = edge.getOpposite(face))
                )
                && ctx.getClickedFace() != other.getOpposite()
                && (placed.getAxis() == edge.getAxis()
                || ctx.getClickedFace() == other
                || !placed.hasDirection(other))
            ) {
                new_edge = edge.getOpposite(edge.getSecondDirection());
                other = edge.getFirstDirection().getOpposite();
            }

            return ReFramed.SLABS_STAIR.defaultBlockState()
                .setValue(EDGE, new_edge)
                .setValue(EDGE_FACE, new_edge.getDirectionIndex(other))
                .setValue(WATERLOGGED, current_state.getValue(WATERLOGGED));
        }

        if (current_state.is(ReFramed.SMALL_CUBE)) {
            Corner corner = current_state.getValue(CORNER);
            Edge placed = BlockHelper.getPlacementEdge(ctx);
            Direction face;
            Corner new_corner;

            if (!corner.hasDirection(face = ctx.getClickedFace())) {
                int i = 0;
                do {
                    face = corner.getDirection(i);
                    new_corner = corner.change(face);
                } while (!ReFramed.SMALL_CUBE.matchesShape(
                    ctx.getClickLocation(),
                    ctx.getClickedPos(),
                    current_state.setValue(CORNER, new_corner)
                ) && ++i < 3);

                if (i == 3) {
                    face = placed.getOtherDirection(corner.getMatchingDirection(placed)).getOpposite();
                    new_corner = corner.change(face);
                }
            } else new_corner = corner.change(face);

            return ReFramed.SLABS_OUTER_STAIR.defaultBlockState()
                .setValue(CORNER, new_corner)
                .setValue(CORNER_FACE, new_corner.getDirectionIndex(face.getOpposite()))
                .setValue(WATERLOGGED, current_state.getValue(WATERLOGGED));
        }

		return super.getStateForPlacement(ctx).setValue(FACING, ctx.getClickedFace().getOpposite());
	}

	@Override
    @SuppressWarnings("deprecation")
	public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
		return getSlabShape(state.getValue(FACING));
	}

	@Override
    @SuppressWarnings("deprecation")
	public BlockState rotate(BlockState state, Rotation rotation) {
		return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
	}

	@Override
    @SuppressWarnings("deprecation")
	public BlockState mirror(BlockState state, Mirror mirror) {
		return state.setValue(FACING, mirror.mirror(state.getValue(FACING)));
	}

	public static VoxelShape getSlabShape(Direction side) {
		return switch (side) {
			case DOWN -> DOWN;
			case UP -> UP;
			case NORTH -> NORTH;
			case SOUTH -> SOUTH;
			case EAST -> EAST;
			case WEST -> WEST;
		};
	}

	@Override
	public Map<Integer, Integer> getThemeMap(BlockState state, BlockState new_state) {
        if (new_state.is(ReFramed.SLABS_STAIR)
            || new_state.is(ReFramed.SLABS_OUTER_STAIR)
            || new_state.is(ReFramed.SLABS_INNER_STAIR)
            || new_state.is(ReFramed.SLABS_HALF_LAYER)
        ) return Map.of(1, 1);
		if (new_state.is(ReFramed.SLABS_CUBE)) return Map.of(1, state.getValue(FACING).getAxisDirection() == Direction.AxisDirection.POSITIVE ? 2 : 1);
		return super.getThemeMap(state, new_state);
	}
}
