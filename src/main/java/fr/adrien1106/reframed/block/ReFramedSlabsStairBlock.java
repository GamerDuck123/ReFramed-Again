package fr.adrien1106.reframed.block;

import fr.adrien1106.reframed.util.blocks.Edge;
import fr.adrien1106.reframed.util.blocks.StairShape;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.BlockGetter;

import static fr.adrien1106.reframed.block.ReFramedSlabBlock.getSlabShape;
import static fr.adrien1106.reframed.block.ReFramedStairBlock.getStairShape;
import static fr.adrien1106.reframed.block.ReFramedStepBlock.getStepShape;
import static fr.adrien1106.reframed.util.blocks.BlockProperties.EDGE;
import static fr.adrien1106.reframed.util.blocks.BlockProperties.EDGE_FACE;

public class ReFramedSlabsStairBlock extends EdgeDoubleReFramedBlock {

    public ReFramedSlabsStairBlock(Properties settings) {
        super(settings);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return getStairShape(state.getValue(EDGE), StairShape.STRAIGHT);
    }

    @Override
    public VoxelShape getShape(BlockState state, int i) {
        Edge edge = state.getValue(EDGE);
        Direction face = edge.getDirection(state.getValue(EDGE_FACE));
        return i == 2
            ? getStepShape(edge.getOpposite(edge.getOtherDirection(face)))
            : getSlabShape(face);
    }
}
