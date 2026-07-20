package fr.adrien1106.reframed.block;

import fr.adrien1106.reframed.util.blocks.BlockHelper;
import fr.adrien1106.reframed.util.blocks.Corner;
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

import static fr.adrien1106.reframed.block.ReFramedHalfStairBlock.getHalfStairShape;
import static fr.adrien1106.reframed.block.ReFramedSlabBlock.getSlabShape;
import static fr.adrien1106.reframed.block.ReFramedSmallCubeBlock.getSmallCubeShape;
import static fr.adrien1106.reframed.util.blocks.BlockProperties.CORNER;
import static fr.adrien1106.reframed.util.blocks.BlockProperties.CORNER_FACE;
import static fr.adrien1106.reframed.util.blocks.Corner.NORTH_EAST_DOWN;

public class ReFramedHalfStairsSlabBlock extends WaterloggableReFramedDoubleBlock {

    public ReFramedHalfStairsSlabBlock(Properties settings) {
        super(settings);
        registerDefaultState(defaultBlockState().setValue(CORNER, NORTH_EAST_DOWN).setValue(CORNER_FACE, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(CORNER,CORNER_FACE));
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext ctx) {
        Corner corner = BlockHelper.getPlacementCorner(ctx);
        return super.getStateForPlacement(ctx)
            .setValue(CORNER, corner)
            .setValue(CORNER_FACE, corner.getDirectionIndex(ctx.getClickedFace().getOpposite()));
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return getSlabShape(state.getValue(CORNER).getDirection(state.getValue(CORNER_FACE)));
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState rotate(BlockState state, Rotation rotation) {
        Corner corner = state.getValue(CORNER).rotate(rotation);
        Direction face = state.getValue(CORNER).getDirection(state.getValue(CORNER_FACE));
        return state.setValue(CORNER, corner).setValue(CORNER_FACE, corner.getDirectionIndex(rotation.rotate(face)));
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState mirror(BlockState state, Mirror mirror) {
        Corner corner = state.getValue(CORNER).mirror(mirror);
        Direction face = state.getValue(CORNER).getDirection(state.getValue(CORNER_FACE));
        return state.setValue(CORNER, corner).setValue(CORNER_FACE, corner.getDirectionIndex(mirror.mirror(face)));
    }

    @Override
    public VoxelShape getShape(BlockState state, int i) {
        Corner corner = state.getValue(CORNER);
        int face = state.getValue(CORNER_FACE);
        return i == 2
            ? getSmallCubeShape(corner.getOpposite(face))
            : getHalfStairShape(corner, face);
    }
}
