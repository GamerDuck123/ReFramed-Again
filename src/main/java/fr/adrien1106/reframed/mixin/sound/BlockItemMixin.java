package fr.adrien1106.reframed.mixin.sound;

import com.llamalad7.mixinextras.sugar.Local;
import fr.adrien1106.reframed.block.ReFramedBlock;
import fr.adrien1106.reframed.util.blocks.ThemeableBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BlockItem.class)
public class BlockItemMixin {

    @Redirect(
        method = "place(Lnet/minecraft/world/item/context/BlockPlaceContext;)Lnet/minecraft/world/InteractionResult;",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/block/state/BlockState;getSoundType()Lnet/minecraft/world/level/block/SoundType;"
        )
    )
    public SoundType getCamoPlaceSound(BlockState state, @Local Level world, @Local BlockPos pos) {
        if (state.getBlock() instanceof ReFramedBlock frame_block
            && world.getBlockEntity(pos) instanceof ThemeableBlockEntity frame_entity
        ) {
            BlockState camo_state = frame_entity.getTheme(frame_block.getTopThemeIndex(state));
            state = camo_state.getBlock() != Blocks.AIR ? camo_state : state;
        }
        return state.getSoundType();
    }

    @Redirect(
        method = "place(Lnet/minecraft/world/item/context/BlockPlaceContext;)Lnet/minecraft/world/InteractionResult;",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/item/BlockItem;getPlaceSound(Lnet/minecraft/world/level/block/state/BlockState;)Lnet/minecraft/sounds/SoundEvent;"
        )
    )
    private SoundEvent getCamoSoundEvent(BlockItem item, BlockState state, @Local SoundType group) {
        // I don't know why it wasn't doing that by default
        return group.getPlaceSound();
    }
}
