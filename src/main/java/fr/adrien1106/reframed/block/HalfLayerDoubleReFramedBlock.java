package fr.adrien1106.reframed.block;

import fr.adrien1106.reframed.ReFramed;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.block.state.StateDefinition;

import java.util.List;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.LAYERS;

public abstract class HalfLayerDoubleReFramedBlock extends EdgeDoubleReFramedBlock {

    public HalfLayerDoubleReFramedBlock(Properties settings) {
        super(settings);
        registerDefaultState(defaultBlockState().setValue(LAYERS, 1));
    }

    @Override
    @SuppressWarnings("deprecation")
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        List<ItemStack> drops = super.getDrops(state, builder);
        if (state.getValue(LAYERS) > 1)
            drops.add(new ItemStack(ReFramed.HALF_LAYER, state.getValue(LAYERS)-1));
        return drops;
    }

    @Override
    protected void createBlockStateDefinition(net.minecraft.world.level.block.state.StateDefinition.Builder builder) {
        super.createBlockStateDefinition(builder.add(LAYERS));
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean canBeReplaced(BlockState state, BlockPlaceContext context) {
        if (context.getPlayer() == null
            || context.getPlayer().isShiftKeyDown()
            || !(context.getItemInHand().getItem() instanceof BlockItem block_item)
        ) return false;
        return block_item.getBlock() == ReFramed.HALF_LAYER && state.getValue(LAYERS) < 8;
    }
}
