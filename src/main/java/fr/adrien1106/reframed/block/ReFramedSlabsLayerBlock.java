package fr.adrien1106.reframed.block;

import fr.adrien1106.reframed.ReFramed;
import fr.adrien1106.reframed.util.VoxelHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.BlockGetter;

import java.util.List;

import static fr.adrien1106.reframed.block.ReFramedLayerBlock.getLayerShape;
import static fr.adrien1106.reframed.block.ReFramedSlabBlock.getSlabShape;
import static fr.adrien1106.reframed.util.blocks.BlockProperties.HALF_LAYERS;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.FACING;

public class ReFramedSlabsLayerBlock extends FacingDoubleReFramedBlock {

    public static VoxelShape[] HALF_LAYER_SHAPES;

    public ReFramedSlabsLayerBlock(Properties settings) {
        super(settings);
        registerDefaultState(defaultBlockState().setValue(HALF_LAYERS, 1));
    }

    @Override
    @SuppressWarnings("deprecation")
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        List<ItemStack> drops = super.getDrops(state, builder);
        if (state.getValue(HALF_LAYERS) > 1)
            drops.add(new ItemStack(ReFramed.LAYER, state.getValue(HALF_LAYERS)-1));
        return drops;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean canBeReplaced(BlockState state, BlockPlaceContext context) {
        if (context.getPlayer() == null
            || context.getPlayer().isShiftKeyDown()
            || !(context.getItemInHand().getItem() instanceof BlockItem block_item)
        ) return false;

        return block_item.getBlock() == ReFramed.LAYER && state.getValue(HALF_LAYERS) < 4;
    }

    @Override
    protected void createBlockStateDefinition(net.minecraft.world.level.block.state.StateDefinition.Builder builder) {
        super.createBlockStateDefinition(builder.add(HALF_LAYERS));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return getLayerShape(state.getValue(FACING), state.getValue(HALF_LAYERS) + 4);
    }

    @Override
    public VoxelShape getShape(BlockState state, int i) {
        Direction face = state.getValue(FACING);
        return i == 2
            ? HALF_LAYER_SHAPES[face.get3DDataValue() * 4 + state.getValue(HALF_LAYERS)-1]
            : getSlabShape(face);
    }

    static {
        VoxelHelper.VoxelListBuilder builder = VoxelHelper.VoxelListBuilder.create(box(0, 8, 0, 16, 10, 16), 24)
            .add(box(0, 8, 0, 16, 12, 16))
            .add(box(0, 8, 0, 16, 14, 16))
            .add(box(0, 8, 0, 16, 16, 16));

        for (int i = 0; i < 4; i++) {
            builder.add(i, VoxelHelper::mirrorY);
        }
        for (int i = 0; i < 4; i++) {
            builder.add(i, VoxelHelper::rotateX);
        }
        for (int i = 0; i < 4; i++) {
            builder.add(i, VoxelHelper::rotateCX);
        }
        for (int i = 0; i < 4; i++) {
            builder.add(i, VoxelHelper::rotateCZ);
        }
        for (int i = 0; i < 4; i++) {
            builder.add(i, VoxelHelper::rotateZ);
        }

        HALF_LAYER_SHAPES = builder.build();
    }
}
