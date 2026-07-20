package fr.adrien1106.reframed.block;

import fr.adrien1106.reframed.ReFramed;
import fr.adrien1106.reframed.util.VoxelHelper;
import fr.adrien1106.reframed.util.blocks.BlockHelper;
import fr.adrien1106.reframed.util.blocks.Corner;
import fr.adrien1106.reframed.util.blocks.Edge;
import fr.adrien1106.reframed.util.blocks.StairShape;
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
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.level.BlockGetter;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

// TODO(Ravel): ambiguous static import, members with name VoxelListBuilder have different new names
//
// TODO(Ravel): ambiguous static import, members with name VoxelListBuilder have different new names
//
// TODO(Ravel): ambiguous static import, members with name VoxelListBuilder have different new names
//
import static fr.adrien1106.reframed.util.VoxelHelper.VoxelListBuilder;
import static fr.adrien1106.reframed.util.blocks.BlockProperties.*;
import static fr.adrien1106.reframed.util.blocks.Corner.*;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.FACING;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.WATERLOGGED;

public class ReFramedSmallCubeBlock extends WaterloggableReFramedBlock {

    public static final VoxelShape[] SMALL_CUBE_VOXELS;

    public ReFramedSmallCubeBlock(Properties settings) {
        super(settings);
        registerDefaultState(defaultBlockState().setValue(CORNER, NORTH_EAST_DOWN));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(CORNER));
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean canBeReplaced(BlockState state, BlockPlaceContext context) {
        if (context.getPlayer() == null
            || context.getPlayer().isShiftKeyDown()
            || !(context.getItemInHand().getItem() instanceof BlockItem block_item)
        ) return false;

        Block block = block_item.getBlock();
        Corner corner = state.getValue(CORNER);
        if (block == this)
            return matchesShape(
                context.getClickLocation(),
                context.getClickedPos(),
                defaultBlockState().setValue(CORNER, corner.change(corner.getFirstDirection()))
            ) || matchesShape(
                context.getClickLocation(),
                context.getClickedPos(),
                defaultBlockState().setValue(CORNER, corner.change(corner.getSecondDirection()))
            ) || matchesShape(
                context.getClickLocation(),
                context.getClickedPos(),
                defaultBlockState().setValue(CORNER, corner.change(corner.getThirdDirection()))
            );

        if (block == ReFramed.HALF_STAIR || block == ReFramed.SLAB) {
            corner = corner.getOpposite();
            Direction face = corner.getFirstDirection();
            Edge edge = corner.getEdge(face);
            if (ReFramed.STAIR.matchesShape(
                context.getClickLocation(),
                context.getClickedPos(),
                ReFramed.STAIR.defaultBlockState()
                    .setValue(EDGE, edge)
                    .setValue(
                        STAIR_SHAPE,
                        face.getAxisDirection() == Direction.AxisDirection.POSITIVE
                            ? StairShape.INNER_LEFT
                            : StairShape.INNER_RIGHT
                    )
            )) return block == ReFramed.SLAB || !matchesShape(
                context.getClickLocation(),
                context.getClickedPos(),
                defaultBlockState().setValue(CORNER, corner)
            );
        }

        return false;
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext ctx) {
        BlockPos pos = ctx.getClickedPos();
        BlockState current_state = ctx.getLevel().getBlockState(pos);

        if (current_state.is(ReFramed.HALF_STAIR)) {
            BlockState new_state;
            Direction face = current_state.getValue(CORNER).getDirection(current_state.getValue(CORNER_FACE));
            if (matchesShape(
                ctx.getClickLocation(), pos,
                defaultBlockState().setValue(CORNER, current_state.getValue(CORNER).change(face))
            )) new_state = ReFramed.HALF_STAIRS_CUBE_STAIR.defaultBlockState();
            else new_state = ReFramed.HALF_STAIRS_SLAB.defaultBlockState();
            return new_state
                .setValue(CORNER, current_state.getValue(CORNER))
                .setValue(CORNER_FACE, current_state.getValue(CORNER_FACE))
                .setValue(WATERLOGGED, current_state.getValue(WATERLOGGED));
        }

        if (current_state.is(this)) {
            Vec3 hit = ctx.getClickLocation();
            Corner corner = current_state.getValue(CORNER);
            ReFramedSmallCubesStepBlock block = ((ReFramedSmallCubesStepBlock) ReFramed.SMALL_CUBES_STEP);
            BlockState state = block.defaultBlockState()
                .setValue(EDGE, corner.getEdge(corner.getFirstDirection()))
                .setValue(WATERLOGGED, current_state.getValue(WATERLOGGED));
            if (block.matchesShape(
                hit, pos, state,
                corner.getFirstDirection().getAxisDirection() == Direction.AxisDirection.POSITIVE ? 1 : 2
            )) return state;
            state = state.setValue(EDGE, corner.getEdge(corner.getSecondDirection()));
            if (block.matchesShape(
                hit, pos, state,
                corner.getSecondDirection().getAxisDirection() == Direction.AxisDirection.POSITIVE ? 1 : 2
            )) return state;
            return state.setValue(EDGE, corner.getEdge(corner.getThirdDirection()));
        }

        if (current_state.is(ReFramed.SLAB)) {
            Corner corner = BlockHelper.getPlacementCorner(ctx);
            Direction face = current_state.getValue(FACING);
            if (!corner.hasDirection(face)) corner = corner.change(face.getOpposite());
            return ReFramed.SLABS_OUTER_STAIR.defaultBlockState()
                .setValue(CORNER, corner)
                .setValue(CORNER_FACE, corner.getDirectionIndex(face))
                .setValue(WATERLOGGED, current_state.getValue(WATERLOGGED));
        }

        return super.getStateForPlacement(ctx).setValue(CORNER, BlockHelper.getPlacementCorner(ctx));
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return getSmallCubeShape(state.getValue(CORNER));
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(CORNER, state.getValue(CORNER).rotate(rotation));
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.setValue(CORNER, state.getValue(CORNER).mirror(mirror));
    }

    @Override
    public Map<Integer, Integer> getThemeMap(BlockState state, BlockState new_state) {
        if (new_state.is(ReFramed.HALF_STAIRS_SLAB)
            || new_state.is(ReFramed.SLABS_OUTER_STAIR)
        ) return Map.of(1, 2);
        if (new_state.is(ReFramed.SMALL_CUBES_STEP))
            return Map.of(
                1,
                state.getValue(CORNER)
                    .getOtherDirection(new_state.getValue(EDGE))
                    .getAxisDirection() == Direction.AxisDirection.POSITIVE ? 2 : 1
            );
        return super.getThemeMap(state, new_state);
    }

    public static VoxelShape getSmallCubeShape(Corner corner) {
        return SMALL_CUBE_VOXELS[corner.getID()];
    }

    static {
        final VoxelShape SMALL_CUBE = Shapes.box(.5f, 0f, 0f, 1f, .5f, .5f);

        SMALL_CUBE_VOXELS = VoxelListBuilder.create(SMALL_CUBE, 8)
            .add(VoxelHelper::rotateY)
            .add(VoxelHelper::rotateY)
            .add(VoxelHelper::rotateY)

            .add(SMALL_CUBE, VoxelHelper::mirrorY)
            .add(VoxelHelper::rotateY)
            .add(VoxelHelper::rotateY)
            .add(VoxelHelper::rotateY)
            .build();
    }
}
