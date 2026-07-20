package fr.adrien1106.reframed.block;

import fr.adrien1106.reframed.ReFramed;
import fr.adrien1106.reframed.util.VoxelHelper;
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

// TODO(Ravel): ambiguous static import, members with name VoxelListBuilder have different new names
//
// TODO(Ravel): ambiguous static import, members with name VoxelListBuilder have different new names
//
// TODO(Ravel): ambiguous static import, members with name VoxelListBuilder have different new names
//
import static fr.adrien1106.reframed.util.VoxelHelper.VoxelListBuilder;
import static fr.adrien1106.reframed.util.blocks.BlockProperties.HALF_LAYERS;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.*;

public class ReFramedLayerBlock extends LayeredReFramedBlock {

    public static final VoxelShape[] LAYER_VOXELS;

    public ReFramedLayerBlock(Properties settings) {
        super(settings);
        registerDefaultState(defaultBlockState().setValue(FACING, Direction.DOWN));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(FACING));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return getLayerShape(state.getValue(FACING), state.getValue(LAYERS));
    }

    public static VoxelShape getLayerShape(Direction facing, int layers) {
        return LAYER_VOXELS[facing.get3DDataValue() * 8 + layers - 1];
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext ctx) {
        BlockState previous = ctx.getLevel().getBlockState(ctx.getClickedPos());
        BlockState state = super.getStateForPlacement(ctx);
        if (previous.is(this)) return state;

        if (previous.is(ReFramed.SLAB))
            return ReFramed.SLABS_LAYER.defaultBlockState()
                .setValue(FACING, previous.getValue(FACING))
                .setValue(WATERLOGGED, previous.getValue(WATERLOGGED));

        if (previous.is(ReFramed.SLABS_LAYER))
            return previous.setValue(HALF_LAYERS, previous.getValue(HALF_LAYERS) + 1);

        return state.setValue(FACING, ctx.getClickedFace().getOpposite());
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.setValue(FACING, mirror.mirror(state.getValue(FACING)));
    }

    static {
        VoxelListBuilder builder = VoxelListBuilder.create(box(0, 0, 0, 16, 2, 16), 48)
            .add(box(0, 0, 0, 16, 4, 16))
            .add(box(0, 0, 0, 16, 6, 16))
            .add(box(0, 0, 0, 16, 8, 16))
            .add(box(0, 0, 0, 16, 10, 16))
            .add(box(0, 0, 0, 16, 12, 16))
            .add(box(0, 0, 0, 16, 14, 16))
            .add(box(0, 0, 0, 16, 16, 16));

        for (int i = 0; i < 8; i++) {
            builder.add(i, VoxelHelper::mirrorY);
        }
        for (int i = 0; i < 8; i++) {
            builder.add(i, VoxelHelper::rotateX);
        }
        for (int i = 0; i < 8; i++) {
            builder.add(i, VoxelHelper::rotateCX);
        }
        for (int i = 0; i < 8; i++) {
            builder.add(i, VoxelHelper::rotateCZ);
        }
        for (int i = 0; i < 8; i++) {
            builder.add(i, VoxelHelper::rotateZ);
        }

        LAYER_VOXELS = builder.build();
    }
}
