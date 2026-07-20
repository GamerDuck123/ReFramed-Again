package fr.adrien1106.reframed.item;

import fr.adrien1106.reframed.ReFramed;
import fr.adrien1106.reframed.block.ReFramedEntity;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class ReFramedBlueprintItem extends Item {
    public ReFramedBlueprintItem(Properties settings) {
        super(settings);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        BlockPos pos = context.getClickedPos();
        Level world = context.getLevel();
        if (!(world.getBlockEntity(pos) instanceof ReFramedEntity frame_entity)
            || frame_entity.getThemes().stream().noneMatch(state -> state.getBlock() != Blocks.AIR)
        ) return InteractionResult.PASS;

        context.getItemInHand().shrink(1);
        ItemStack stack = ReFramed.BLUEPRINT_WRITTEN.getDefaultInstance();
        frame_entity.saveToItem(stack);
        context.getPlayer().addItem(stack);
        world.playSound(context.getPlayer(), context.getPlayer().blockPosition(), SoundEvents.BOOK_PUT, SoundSource.PLAYERS);

        return InteractionResult.SUCCESS;
    }
}
