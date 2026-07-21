package fr.adrien1106.reframed.block;

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
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.BlockGetter;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.LAYERS;

public abstract class LayeredReFramedBlock extends WaterloggableReFramedBlock {

    public LayeredReFramedBlock(Properties settings) {
        super(settings);
        registerDefaultState(defaultBlockState().setValue(LAYERS, 1));
    }

    @Override
    @SuppressWarnings("deprecation")
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        List<ItemStack> drops = super.getDrops(state, builder);
        drops.forEach((stack) -> {
            if (stack.getItem() instanceof BlockItem bi && bi.getBlock() instanceof LayeredReFramedBlock)
                stack.setCount(state.getValue(LAYERS));
        });
        return drops;
    }

    @Override
    protected void createBlockStateDefinition(net.minecraft.world.level.block.state.StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(LAYERS));
    }

    @Override
    @SuppressWarnings("deprecation")
    public abstract VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context);

    @Override
    @SuppressWarnings("deprecation")
    public boolean canBeReplaced(BlockState state, BlockPlaceContext context) {
        if (context.getPlayer() == null) return false;
        return !(
            context.getPlayer().isShiftKeyDown()
                || !(context.getItemInHand().getItem() instanceof BlockItem block_item)
                || !(block_item.getBlock() == this && state.getValue(LAYERS) < 8)
        );
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext ctx) {
        BlockState previous = ctx.getLevel().getBlockState(ctx.getClickedPos());
        if (!previous.is(this)) return super.getStateForPlacement(ctx);
        return previous.setValue(LAYERS, previous.getValue(LAYERS) + 1);
    }
}
