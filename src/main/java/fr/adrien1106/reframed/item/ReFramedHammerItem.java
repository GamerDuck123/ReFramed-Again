package fr.adrien1106.reframed.item;

import fr.adrien1106.reframed.ReFramed;
import fr.adrien1106.reframed.block.ReFramedDoubleBlock;
import fr.adrien1106.reframed.util.blocks.ThemeableBlockEntity;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class ReFramedHammerItem extends Item {
    public ReFramedHammerItem(Properties settings) {
        super(settings);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        if (!(world.getBlockEntity(pos) instanceof ThemeableBlockEntity frame_entity)) return InteractionResult.PASS;
        BlockState state = world.getBlockState(pos);
        Player player = context.getPlayer();
        int theme_index = state.getBlock() instanceof ReFramedDoubleBlock b
            ? b.getHitShape(
            state,
            context.getClickLocation(),
            context.getClickedPos(),
            context.getClickedFace()
        )
            : 1;

        if (frame_entity.getTheme(theme_index).getBlock() == Blocks.AIR) return InteractionResult.PASS;

        if (!player.isCreative()) {
            player.addItem(new ItemStack(frame_entity.getTheme(theme_index).getBlock()));
            world.playSound(player, pos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 1f, 1.1f);
        }
        frame_entity.setTheme(Blocks.AIR.defaultBlockState(), theme_index);
        if (world.isClientSide) ReFramed.chunkRerenderProxy.accept(world, pos);
        return InteractionResult.SUCCESS;
    }
}
