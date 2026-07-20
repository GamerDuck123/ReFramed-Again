package fr.adrien1106.reframed.block;

import fr.adrien1106.reframed.ReFramed;
import fr.adrien1106.reframed.util.VoxelHelper;
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
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
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
import static fr.adrien1106.reframed.util.blocks.BlockProperties.STAIR_SHAPE;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.*;

public class ReFramedStepBlock extends WaterloggableReFramedBlock {

    public static final VoxelShape[] STEP_VOXELS;

    public ReFramedStepBlock(Properties settings) {
        super(settings);
        registerDefaultState(defaultBlockState().setValue(EDGE, Edge.NORTH_DOWN));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(EDGE));
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean canBeReplaced(BlockState state, BlockPlaceContext context) {
        if (context.getPlayer() == null
            || context.getPlayer().isShiftKeyDown()
            || !(context.getItemInHand().getItem() instanceof BlockItem block_item)
        ) return false;

        Block block = block_item.getBlock();
        Edge edge = state.getValue(EDGE);
        if (block == ReFramed.HALF_LAYER)
            return matchesShape(
                context.getClickLocation(),
                context.getClickedPos(),
                defaultBlockState().setValue(EDGE, edge.getOpposite(edge.getFirstDirection()))
            ) || matchesShape(
                context.getClickLocation(),
                context.getClickedPos(),
                defaultBlockState().setValue(EDGE, edge.getOpposite(edge.getSecondDirection()))
            );

        // allow replacing with stair
        if (block != this
            && block != ReFramed.STAIR
            && block != ReFramed.SLAB
        ) return false;

        return ReFramed.STAIR
            .matchesShape(
                context.getClickLocation(),
                context.getClickedPos(),
                ReFramed.STAIRS_CUBE.defaultBlockState().setValue(EDGE, edge.opposite())
            );
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        BlockPos pos = ctx.getClickedPos();
        BlockState current_state = ctx.getLevel().getBlockState(pos);

        if (current_state.is(ReFramed.STAIR))
            return ReFramed.STAIRS_CUBE.defaultBlockState()
                .setValue(EDGE, current_state.getValue(EDGE))
                .setValue(STAIR_SHAPE, current_state.getValue(STAIR_SHAPE));


        if (current_state.is(this)) {
            Vec3 hit = ctx.getClickLocation();
            Edge edge = current_state.getValue(EDGE);

            // Steps Slab
            if (matchesShape(hit, pos,
                current_state.setValue(EDGE, edge.getOpposite(edge.getFirstDirection()))
            )) return ReFramed.STEPS_SLAB.defaultBlockState()
                .setValue(FACING, edge.getFirstDirection())
                .setValue(AXIS, edge.getSecondDirection().getAxis())
                .setValue(WATERLOGGED, current_state.getValue(WATERLOGGED));

            else if (matchesShape(hit, pos,
                current_state.setValue(EDGE, edge.getOpposite(edge.getSecondDirection()))
            )) return ReFramed.STEPS_SLAB.defaultBlockState()
                .setValue(FACING, edge.getSecondDirection())
                .setValue(AXIS, edge.getFirstDirection().getAxis())
                .setValue(WATERLOGGED, current_state.getValue(WATERLOGGED));

            // Steps Cross
            return ReFramed.STEPS_CROSS.defaultBlockState()
                .setValue(EDGE, edge)
                .setValue(WATERLOGGED, current_state.getValue(WATERLOGGED));
        }

        if (current_state.is(ReFramed.SLAB)) {
            Direction facing = current_state.getValue(FACING);
            Edge edge;

            // Slabs Stair
            if (ctx.getClickedFace() == facing || ctx.getClickedFace() == facing.getOpposite())
                edge = BlockHelper.getPlacementEdge(ctx);
            else
                edge = Edge.getByDirections(facing, ctx.getClickedFace().getOpposite());
            return ReFramed.SLABS_STAIR.defaultBlockState()
                .setValue(EDGE, edge)
                .setValue(EDGE_FACE, edge.getDirectionIndex(facing));

        }

        if (current_state.is(ReFramed.HALF_STAIR)) {
            Corner corner = current_state.getValue(CORNER);
            int face_index = current_state.getValue(CORNER_FACE), feature_index;
            Direction face = corner.getDirection(current_state.getValue(CORNER_FACE));
            Direction side = ctx.getClickedFace().getOpposite();

            if (side.getAxis() == face.getAxis())
                side = BlockHelper.getPlacementEdge(ctx).getOtherDirection(face == side ? face : face.getOpposite());

            if (side.getAxis() != face.getAxis() && !corner.hasDirection(side))
                side = corner.getOtherDirection(Edge.getByDirections(face, side.getOpposite()));

            feature_index = corner.getDirectionIndex(side);
            return ReFramed.HALF_STAIRS_STEP_STAIR.defaultBlockState()
                .setValue(CORNER, corner)
                .setValue(CORNER_FACE, face_index)
                .setValue(CORNER_FEATURE, feature_index > face_index ? feature_index - 1 : feature_index)
                .setValue(WATERLOGGED, current_state.getValue(WATERLOGGED));
        }

        if (current_state.is(ReFramed.HALF_LAYER)) {
            Edge edge = current_state.getValue(EDGE);
            Direction face = edge.getDirection(current_state.getValue(EDGE_FACE));
            edge = edge.getOpposite(face);
            return ReFramed.STEPS_HALF_LAYER.defaultBlockState()
                .setValue(EDGE, edge)
                .setValue(EDGE_FACE, edge.getDirectionIndex(edge.getOtherDirection(face)))
                .setValue(LAYERS, current_state.getValue(LAYERS))
                .setValue(WATERLOGGED, current_state.getValue(WATERLOGGED));
        }

        return super.getStateForPlacement(ctx).setValue(EDGE, BlockHelper.getPlacementEdge(ctx));
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return getStepShape(state.getValue(EDGE));
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(EDGE, state.getValue(EDGE).rotate(rotation));
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.setValue(EDGE, state.getValue(EDGE).mirror(mirror));
    }

    public static VoxelShape getStepShape(Edge edge) {
        return STEP_VOXELS[edge.getID()];
    }

    @Override
    public Map<Integer, Integer> getThemeMap(BlockState state, BlockState new_state) {
        if (new_state.is(ReFramed.STEPS_CROSS)
            || new_state.is(ReFramed.STEPS_HALF_LAYER)
        ) return Map.of(1, 1);
        if (new_state.is(ReFramed.STAIRS_CUBE)
            || new_state.is(ReFramed.SLABS_STAIR)) return Map.of(1, 2);
        if (new_state.is(ReFramed.STEPS_SLAB))
            return Map.of(
                1,
                state.getValue(EDGE)
                    .getOtherDirection(new_state.getValue(FACING))
                    .getAxisDirection() == Direction.AxisDirection.POSITIVE ? 2 : 1
            );
        return super.getThemeMap(state, new_state);
    }

    static {
        final VoxelShape STEP = box(0, 0, 0, 16, 8, 8);

        STEP_VOXELS = VoxelListBuilder.create(STEP, 12)
            .add(VoxelHelper::rotateCX)
            .add(VoxelHelper::rotateCX)
            .add(VoxelHelper::rotateCX)

            .add(STEP, VoxelHelper::rotateCY)
            .add(VoxelHelper::rotateZ)
            .add(VoxelHelper::rotateZ)
            .add(VoxelHelper::rotateZ)

            .add(STEP, VoxelHelper::rotateCZ)
            .add(VoxelHelper::rotateY)
            .add(VoxelHelper::rotateY)
            .add(VoxelHelper::rotateY)
            .build();
    }
}
