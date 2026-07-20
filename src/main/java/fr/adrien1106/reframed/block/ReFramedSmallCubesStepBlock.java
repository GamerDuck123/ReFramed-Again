package fr.adrien1106.reframed.block;

import fr.adrien1106.reframed.util.blocks.BlockHelper;
import fr.adrien1106.reframed.util.blocks.Corner;
import fr.adrien1106.reframed.util.blocks.Edge;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.BlockGetter;
import org.jetbrains.annotations.Nullable;

import static fr.adrien1106.reframed.block.ReFramedSmallCubeBlock.SMALL_CUBE_VOXELS;
import static fr.adrien1106.reframed.block.ReFramedStepBlock.getStepShape;
import static fr.adrien1106.reframed.util.blocks.BlockProperties.EDGE;

public class ReFramedSmallCubesStepBlock extends WaterloggableReFramedDoubleBlock {

    public ReFramedSmallCubesStepBlock(Properties settings) {
        super(settings);
        registerDefaultState(defaultBlockState().setValue(EDGE, Edge.NORTH_DOWN));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(EDGE));
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
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

    @Override
    public VoxelShape getShape(BlockState state, int i) {
        Edge edge = state.getValue(EDGE);
        return SMALL_CUBE_VOXELS[Corner.getByDirections(
            edge.getFirstDirection(),
            edge.getSecondDirection(),
            i == 1
                ? edge.getRightDirection()
                : edge.getLeftDirection()
        ).getID()];
    }

    @Override
    public int getTopThemeIndex(BlockState state) {
        return 2;
    }
}
