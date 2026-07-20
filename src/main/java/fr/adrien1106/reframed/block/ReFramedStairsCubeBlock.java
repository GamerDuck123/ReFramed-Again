package fr.adrien1106.reframed.block;

import fr.adrien1106.reframed.util.blocks.BlockHelper;
import fr.adrien1106.reframed.util.blocks.Edge;
import fr.adrien1106.reframed.util.blocks.StairShape;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.LevelAccessor;
import org.jetbrains.annotations.Nullable;

import static fr.adrien1106.reframed.block.ReFramedStairBlock.*;
// TODO(Ravel): ambiguous static import, members with name VoxelListBuilder have different new names
//
// TODO(Ravel): ambiguous static import, members with name VoxelListBuilder have different new names
//
// TODO(Ravel): ambiguous static import, members with name VoxelListBuilder have different new names
//
import static fr.adrien1106.reframed.util.VoxelHelper.VoxelListBuilder;
import static fr.adrien1106.reframed.util.blocks.BlockProperties.EDGE;
import static fr.adrien1106.reframed.util.blocks.BlockProperties.STAIR_SHAPE;

public class ReFramedStairsCubeBlock extends ReFramedDoubleBlock {

    private static final VoxelShape[] STAIRS_CUBE_VOXELS = VoxelListBuilder.buildFrom(STAIR_VOXELS);

    public ReFramedStairsCubeBlock(Properties settings) {
        super(settings);
        registerDefaultState(defaultBlockState().setValue(EDGE, Edge.NORTH_DOWN).setValue(STAIR_SHAPE, StairShape.STRAIGHT));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(EDGE, STAIR_SHAPE));
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighbor_state, LevelAccessor world, BlockPos pos, BlockPos moved) {
        return super.updateShape(state, direction, neighbor_state, world, pos, moved)
            .setValue(STAIR_SHAPE, BlockHelper.getStairsShape(state.getValue(EDGE), world, pos));
    }


    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        Edge face = BlockHelper.getPlacementEdge(ctx);
        StairShape shape = BlockHelper.getStairsShape(face, ctx.getLevel(), ctx.getClickedPos());
        return super.getStateForPlacement(ctx).setValue(EDGE, face).setValue(STAIR_SHAPE, shape);
    }


    @Override
    @SuppressWarnings("deprecation")
    public BlockState rotate(BlockState state, Rotation rotation) {
        Edge prev_edge = state.getValue(EDGE);
        Edge edge = prev_edge.rotate(rotation);
        if (prev_edge.getAxis() == Direction.Axis.Y) return state.setValue(EDGE, edge);

        if (prev_edge.getFace().getAxisDirection() == edge.getFace().getAxisDirection()) // 90° rotations
            state = state.setValue(STAIR_SHAPE, state.getValue(STAIR_SHAPE).mirror());
        else state = state.setValue(STAIR_SHAPE, state.getValue(STAIR_SHAPE).flip());

        if (prev_edge.getAxis() == edge.getAxis()) // 180° rotation
            state = state.setValue(STAIR_SHAPE, state.getValue(STAIR_SHAPE).mirror());

        return state.setValue(EDGE, edge);
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState mirror(BlockState state, Mirror mirror) {
        Edge prev_edge = state.getValue(EDGE);
        Edge edge = prev_edge.mirror(mirror);
        return state
            .setValue(STAIR_SHAPE, prev_edge == edge ? state.getValue(STAIR_SHAPE).mirror() : state.getValue(STAIR_SHAPE).flip())
            .setValue(EDGE, edge);
    }

    @Override
    public VoxelShape getShape(BlockState state, int i) {
        Edge edge = state.getValue(EDGE);
        StairShape shape = state.getValue(STAIR_SHAPE);
        return i == 2 ? STAIRS_CUBE_VOXELS[edge.getID() * 9 + shape.getID()] : getStairShape(edge, shape);
    }
}
