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
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.BlockGetter;
import org.jetbrains.annotations.Nullable;

import static fr.adrien1106.reframed.util.blocks.BlockProperties.EDGE;
import static fr.adrien1106.reframed.util.blocks.BlockProperties.EDGE_FACE;

public abstract class EdgeDoubleReFramedBlock extends WaterloggableReFramedDoubleBlock {

    public EdgeDoubleReFramedBlock(Properties settings) {
        super(settings);
        registerDefaultState(defaultBlockState().setValue(EDGE, Edge.NORTH_DOWN).setValue(EDGE_FACE, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(EDGE, EDGE_FACE));
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext ctx) {
        Edge edge = BlockHelper.getPlacementEdge(ctx);
        return super.getStateForPlacement(ctx)
            .setValue(EDGE, edge)
            .setValue(EDGE_FACE, edge.getDirectionIndex(ctx.getClickedFace().getOpposite()));
    }

    @Override
    @SuppressWarnings("deprecation")
    public abstract VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context);

    @Override
    @SuppressWarnings("deprecation")
    public BlockState rotate(BlockState state, Rotation rotation) {
        Edge edge = state.getValue(EDGE).rotate(rotation);
        Direction face = state.getValue(EDGE).getDirection(state.getValue(EDGE_FACE));
        return state.setValue(EDGE, edge).setValue(EDGE_FACE, edge.getDirectionIndex(rotation.rotate(face)));
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState mirror(BlockState state, Mirror mirror) {
        Edge edge = state.getValue(EDGE).mirror(mirror);
        Direction face = state.getValue(EDGE).getDirection(state.getValue(EDGE_FACE));
        return state.setValue(EDGE, edge).setValue(EDGE_FACE, edge.getDirectionIndex(mirror.mirror(face)));
    }

    @Override
    public abstract VoxelShape getShape(BlockState state, int i);

}
