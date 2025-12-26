package fr.adrien1106.reframed.mixin.render;

import fr.adrien1106.reframed.block.ReFramedBlock;
import fr.adrien1106.reframed.client.util.RenderHelper;
import fr.adrien1106.reframed.util.blocks.ThemeableBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Block.class)
public abstract class BlockMixin {

    @Inject(
        method = "shouldDrawSide",
        at = @At("HEAD"),
        cancellable = true
    ) // serves as a safety sometimes mods implements culling cache and hence will break some injections...
    private static void shouldDrawSide(BlockState state, BlockView world, BlockPos pos, Direction side, BlockPos other_pos, CallbackInfoReturnable<Boolean> cir) {
        if (!(world.getBlockEntity(other_pos) instanceof ThemeableBlockEntity && state.getBlock() instanceof ReFramedBlock)) return;
        cir.setReturnValue(RenderHelper.shouldDrawSide(state, world, pos, side, other_pos, 0));
    }
}
