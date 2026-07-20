package fr.adrien1106.reframed.block;

import fr.adrien1106.reframed.util.VoxelHelper;
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
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.level.BlockGetter;
import org.jetbrains.annotations.Nullable;

import static fr.adrien1106.reframed.block.ReFramedStepBlock.getStepShape;
import static fr.adrien1106.reframed.util.blocks.BlockProperties.EDGE;

public class ReFramedStepsCrossBlock extends WaterloggableReFramedDoubleBlock {

    public static VoxelShape[] STEP_CROSS_VOXELS;

    public ReFramedStepsCrossBlock(Properties settings) {
        super(settings);
        registerDefaultState(defaultBlockState().setValue(EDGE, Edge.NORTH_DOWN));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(EDGE));
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext ctx) {
        Edge edge = BlockHelper.getPlacementCorner(ctx).getEdge(ctx.getClickedFace().getOpposite());
        return super.getStateForPlacement(ctx).setValue(EDGE, edge);
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return STEP_CROSS_VOXELS[state.getValue(EDGE).ordinal()];
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
        return getStepShape(i == 1 ? state.getValue(EDGE): state.getValue(EDGE).opposite());
    }

    static {
        VoxelShape STEP_CROSS = Shapes.join(
            getStepShape(Edge.NORTH_DOWN),
            getStepShape(Edge.SOUTH_UP),
            BooleanOp.OR
        );
        STEP_CROSS_VOXELS = VoxelHelper.VoxelListBuilder.create(STEP_CROSS, 12)
            .add(VoxelHelper::rotateX)
            .add(VoxelHelper::rotateX)
            .add(VoxelHelper::rotateX)
            .add(0, VoxelHelper::rotateCY)
            .add(VoxelHelper::rotateZ)
            .add(VoxelHelper::rotateZ)
            .add(VoxelHelper::rotateZ)
            .add(0, VoxelHelper::rotateCZ)
            .add(VoxelHelper::rotateY)
            .add(VoxelHelper::rotateY)
            .add(VoxelHelper::rotateY)
            .build();
    }
}
