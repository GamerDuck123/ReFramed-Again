package fr.adrien1106.reframed.mixin.compat;

import com.llamalad7.mixinextras.sugar.Local;
import fr.adrien1106.reframed.client.util.RenderHelper;
import fr.adrien1106.reframed.util.blocks.ThemeableBlockEntity;
import me.jellysquid.mods.sodium.client.render.chunk.compile.pipeline.BlockOcclusionCache;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockOcclusionCache.class)
public class SodiumBlockOcclusionCacheMixin {

    @Inject(
        method = "shouldDrawSide",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/core/BlockPos$MutableBlockPos;set(III)Lnet/minecraft/core/BlockPos$MutableBlockPos;",
            shift = At.Shift.AFTER
        ), cancellable = true)
    private void shouldDrawFrameNeighborSide(BlockState self_state, BlockGetter view, BlockPos self_pos, Direction face, CallbackInfoReturnable<Boolean> cir, @Local BlockPos.MutableBlockPos other_pos) {
        if (!(view.getBlockEntity(other_pos) instanceof ThemeableBlockEntity)) return;
        cir.setReturnValue(RenderHelper.shouldDrawSide(self_state, view, self_pos, face, other_pos, 0));
    }
}
