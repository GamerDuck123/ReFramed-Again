package fr.adrien1106.reframed.block;

import fr.adrien1106.reframed.util.blocks.Corner;
import fr.adrien1106.reframed.util.blocks.Edge;
import fr.adrien1106.reframed.util.blocks.StairShape;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.BlockGetter;

import static fr.adrien1106.reframed.block.ReFramedHalfStairBlock.getHalfStairShape;
import static fr.adrien1106.reframed.block.ReFramedSlabBlock.getSlabShape;
import static fr.adrien1106.reframed.block.ReFramedStairBlock.getStairShape;
import static fr.adrien1106.reframed.util.blocks.BlockProperties.CORNER;
import static fr.adrien1106.reframed.util.blocks.BlockProperties.CORNER_FACE;

public class ReFramedSlabsInnerStairBlock extends CornerDoubleReFramedBlock {

    public ReFramedSlabsInnerStairBlock(Properties settings) {
        super(settings);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        Corner corner = state.getValue(CORNER);
        Direction face = corner.getDirection(state.getValue(CORNER_FACE));
        Edge edge = corner.getEdge(face);
        return getStairShape(
            edge,
            face.getAxisDirection() == Direction.AxisDirection.POSITIVE
            ? StairShape.INNER_LEFT
            : StairShape.INNER_RIGHT
        );
    }

    @Override
    public VoxelShape getShape(BlockState state, int i) {
        Corner corner = state.getValue(CORNER);
        Direction face = corner.getDirection(state.getValue(CORNER_FACE));
        if (i == 2) corner = corner.change(face);
        return i == 2
            ? getHalfStairShape(corner, corner.getDirectionIndex(face.getOpposite()))
            : getSlabShape(face);
    }

}
