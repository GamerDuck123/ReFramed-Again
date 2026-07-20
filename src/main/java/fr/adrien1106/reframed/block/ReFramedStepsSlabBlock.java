package fr.adrien1106.reframed.block;

import fr.adrien1106.reframed.util.blocks.BlockHelper;
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
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.BlockGetter;
import org.jetbrains.annotations.Nullable;

import static fr.adrien1106.reframed.block.ReFramedSlabBlock.getSlabShape;
import static fr.adrien1106.reframed.block.ReFramedStepBlock.getStepShape;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.AXIS;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.FACING;

public class ReFramedStepsSlabBlock extends WaterloggableReFramedDoubleBlock {

    public ReFramedStepsSlabBlock(Properties settings) {
        super(settings);
        registerDefaultState(defaultBlockState().setValue(FACING, Direction.DOWN).setValue(AXIS, Axis.X));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(FACING, AXIS));
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext ctx) {
        Vec3 pos = BlockHelper.getHitPos(ctx.getClickLocation(), ctx.getClickedPos());
        return super.getStateForPlacement(ctx)
            .setValue(FACING, ctx.getClickedFace().getOpposite())
            .setValue(AXIS, BlockHelper.getHitAxis(pos, ctx.getClickedFace()));
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return getSlabShape(state.getValue(FACING));
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state
            .setValue(AXIS, rotation.rotate(Direction.get(Direction.AxisDirection.POSITIVE, state.getValue(AXIS))).getAxis())
            .setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState mirror(BlockState state, Mirror mirror) {
        if (state.getValue(FACING).getAxis() != Axis.Y)
            return state.setValue(FACING, mirror.mirror(state.getValue(FACING)));

        return state;
    }

    @Override
    public VoxelShape getShape(BlockState state, int i) {
        Axis axis = state.getValue(AXIS);
        return getStepShape(Edge.getByDirections(
            state.getValue(FACING),
            switch (axis) {
                case X -> i == 1 ? Direction.WEST : Direction.EAST;
                case Y -> i == 1 ? Direction.DOWN : Direction.UP;
                case Z -> i == 1 ? Direction.NORTH : Direction.SOUTH;
            }
        ));
    }

    @Override
    public int getTopThemeIndex(BlockState state) {
        return 2;
    }
}
