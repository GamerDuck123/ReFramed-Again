package fr.adrien1106.reframed.mixin.compat;

import com.llamalad7.mixinextras.sugar.Local;
import fr.adrien1106.reframed.util.blocks.ThemeableBlockEntity;
import me.pepperbell.continuity.client.processor.ConnectionPredicate;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ConnectionPredicate.class)
public interface ContinuityConnectionPredicateMixin {

    @Redirect(
        method = "shouldConnect(Lnet/minecraft/world/level/BlockAndTintGetter;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;Lnet/minecraft/client/renderer/texture/TextureAtlasSprite;)Z",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/BlockAndTintGetter;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;"
        )
    ) // TODO better connected textures
    private BlockState getBlockState(BlockAndTintGetter view, BlockPos pos, @Local(argsOnly = true, ordinal = 1) BlockState state) {
        if (!(view.getBlockEntity(pos) instanceof ThemeableBlockEntity frame_entity)) return view.getBlockState(pos);
        return frame_entity.getThemes()
            .stream()
            .filter(theme -> theme.getBlock() == state.getBlock())
            .findFirst()
            .orElse(frame_entity.getTheme(0));
    }

}
