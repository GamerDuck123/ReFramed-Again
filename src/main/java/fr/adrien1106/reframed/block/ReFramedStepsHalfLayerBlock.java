package fr.adrien1106.reframed.block;

import fr.adrien1106.reframed.util.blocks.Edge;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.level.BlockGetter;

import static fr.adrien1106.reframed.block.ReFramedHalfLayerBlock.getHalfLayerShape;
import static fr.adrien1106.reframed.block.ReFramedStepBlock.getStepShape;
import static fr.adrien1106.reframed.util.blocks.BlockProperties.EDGE;
import static fr.adrien1106.reframed.util.blocks.BlockProperties.EDGE_FACE;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.LAYERS;

public class ReFramedStepsHalfLayerBlock extends HalfLayerDoubleReFramedBlock {

    private static final VoxelShape[] SLABS_HALF_LAYER_VOXELS = new VoxelShape[196];

    public ReFramedStepsHalfLayerBlock(Properties settings) {
        super(settings);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return getStepsHalfLayerShape(state.getValue(EDGE), state.getValue(EDGE_FACE), state.getValue(LAYERS));
    }

    public static VoxelShape getStepsHalfLayerShape(Edge edge, int face, int layers) {
        int i = edge.ordinal() * 16 + face * 8 + layers - 1;
        VoxelShape shape = SLABS_HALF_LAYER_VOXELS[i];
        if (shape == null) {
            shape = Shapes.join(
                getShape(edge, edge.getDirection(face), layers, 1),
                getShape(edge, edge.getDirection(face), layers, 2),
                BooleanOp.OR
            );
            SLABS_HALF_LAYER_VOXELS[i] = shape;
        }
        return shape;
    }

    @Override
    public VoxelShape getShape(BlockState state, int i) {
        Edge edge = state.getValue(EDGE);
        Direction face = edge.getDirection(state.getValue(EDGE_FACE));
        return getShape(edge, face, state.getValue(LAYERS), i);
    }

    private static VoxelShape getShape(Edge edge, Direction face, int layers, int i) {
        if (i == 2) {
            face = edge.getOtherDirection(face);
            edge = edge.getOpposite(face);
        }
        return i == 2
            ? getHalfLayerShape(edge, edge.getDirectionIndex(face), layers)
            : getStepShape(edge);
    }
}
