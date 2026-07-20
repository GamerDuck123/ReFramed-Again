package fr.adrien1106.reframed.block;

import fr.adrien1106.reframed.util.blocks.BlockHelper;
import fr.adrien1106.reframed.util.blocks.Corner;
import fr.adrien1106.reframed.util.blocks.Edge;
import fr.adrien1106.reframed.util.blocks.StairShape;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.BlockGetter;
import org.jetbrains.annotations.Nullable;

import static fr.adrien1106.reframed.block.ReFramedHalfStairBlock.getHalfStairShape;
import static fr.adrien1106.reframed.block.ReFramedStairBlock.getStairShape;
import static fr.adrien1106.reframed.block.ReFramedStepBlock.getStepShape;
import static fr.adrien1106.reframed.util.blocks.BlockProperties.*;

public class ReFramedHalfStairsStepStairBlock extends CornerDoubleReFramedBlock {

    public ReFramedHalfStairsStepStairBlock(Properties settings) {
        super(settings);
        registerDefaultState(defaultBlockState().setValue(CORNER_FEATURE, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(CORNER_FEATURE));
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext ctx) {
        BlockState state = super.getStateForPlacement(ctx);
        Corner corner = state.getValue(CORNER);
        int face_index = state.getValue(CORNER_FACE);
        Direction face = corner.getDirection(face_index);
        face = BlockHelper.getPlacementEdge(ctx).getOtherDirection(face);
        int feature_index = corner.getDirectionIndex(face);
        return state.setValue(CORNER_FEATURE, feature_index > face_index ? feature_index - 1 : feature_index);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        Corner corner = state.getValue(CORNER);
        int feature_index = state.getValue(CORNER_FEATURE), face_index = state.getValue(CORNER_FACE);
        Direction feature_face = corner.getDirection(feature_index >= face_index ? feature_index + 1 : feature_index);
        Direction face = corner.getDirection(face_index);
        Edge edge = Edge.getByDirections(feature_face, face);
        return getStairShape(
            edge,
            corner.getOtherDirection(edge).getAxisDirection() == Direction.AxisDirection.POSITIVE
            ? edge.getDirectionIndex(face) == 0
                ? StairShape.FIRST_OUTER_LEFT
                : StairShape.SECOND_OUTER_LEFT
            : edge.getDirectionIndex(face) == 0
                ? StairShape.FIRST_OUTER_RIGHT
                : StairShape.SECOND_OUTER_RIGHT
        );
    }

    @Override
    public VoxelShape getShape(BlockState state, int i) {
        Corner corner = state.getValue(CORNER);
        int feature_index = state.getValue(CORNER_FEATURE), face_index = state.getValue(CORNER_FACE);
        Direction feature_face = corner.getDirection(feature_index >= face_index ? feature_index + 1 : feature_index);
        Direction face = corner.getDirection(face_index);
        return i == 2
            ? getStepShape(Edge.getByDirections(face.getOpposite(), feature_face))
            : getHalfStairShape(corner, face_index);
    }

}
