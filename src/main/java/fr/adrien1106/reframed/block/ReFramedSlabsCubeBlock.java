package fr.adrien1106.reframed.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import static fr.adrien1106.reframed.block.ReFramedSlabBlock.*;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.AXIS;

public class ReFramedSlabsCubeBlock extends ReFramedDoubleBlock {

    public ReFramedSlabsCubeBlock(Properties settings) {
        super(settings);
        registerDefaultState(defaultBlockState().setValue(AXIS, Direction.Axis.Y));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(AXIS));
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return super.getStateForPlacement(ctx).setValue(AXIS, ctx.getClickedFace().getAxis());
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(AXIS, rotation.rotate(Direction.get(Direction.AxisDirection.POSITIVE, state.getValue(AXIS))).getAxis());
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.setValue(AXIS, mirror.mirror(Direction.get(Direction.AxisDirection.POSITIVE, state.getValue(AXIS))).getAxis());
    }

    @Override
    public VoxelShape getShape(BlockState state, int i) {
        return switch (state.getValue(AXIS)) {
            case Y -> i == 2 ? UP    : DOWN;
            case Z -> i == 2 ? SOUTH : NORTH;
            case X -> i == 2 ? EAST  : WEST;
        };
    }

    @Override
    public int getTopThemeIndex(BlockState state) {
        return 2;
    }
}
