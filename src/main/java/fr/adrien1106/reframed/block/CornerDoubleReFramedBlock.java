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

import static fr.adrien1106.reframed.util.blocks.BlockProperties.CORNER;
import static fr.adrien1106.reframed.util.blocks.BlockProperties.CORNER_FACE;

public abstract class CornerDoubleReFramedBlock extends WaterloggableReFramedDoubleBlock {

    public CornerDoubleReFramedBlock(Properties settings) {
        super(settings);
        registerDefaultState(defaultBlockState().setValue(CORNER, Corner.NORTH_EAST_DOWN).setValue(CORNER_FACE, 0));
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
    public abstract VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context);


    @Override
    @SuppressWarnings("deprecation")
    public BlockState rotate(BlockState state, Rotation rotation) {
        Corner corner = state.getValue(CORNER);
        Direction face = corner.getDirection(state.getValue(CORNER_FACE));
        corner = corner.rotate(rotation);
        return state
            .setValue(CORNER, corner)
            .setValue(CORNER_FACE, corner.getDirectionIndex(rotation.rotate(face)));
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState mirror(BlockState state, Mirror mirror) {
        Corner corner = state.getValue(CORNER);
        Direction face = corner.getDirection(state.getValue(CORNER_FACE));
        corner = corner.mirror(mirror);
        return state
            .setValue(CORNER, corner)
            .setValue(CORNER_FACE, corner.getDirectionIndex(mirror.mirror(face)));
    }

    @Override
    public abstract VoxelShape getShape(BlockState state, int i);
}
