package fr.adrien1106.reframed.mixin.render;

import com.llamalad7.mixinextras.sugar.Local;
import fr.adrien1106.reframed.client.model.RetexturingBakedModel;
import fr.adrien1106.reframed.client.util.RenderHelper;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ModelBlockRenderer.class)
public class ModelBlockRendererMixin {

    @Redirect(
        method = "tesselateWithAO",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/block/Block;shouldRenderFace(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;Lnet/minecraft/core/BlockPos;)Z"
        )
    )
    private boolean shouldDrawFrameSideS(BlockState state, BlockGetter world, BlockPos pos, Direction side, BlockPos other_pos, @Local(argsOnly = true) BakedModel model) {
        return RenderHelper.shouldDrawSide(state, world, pos, side, other_pos, model instanceof RetexturingBakedModel rm ? rm.getThemeIndex() : 0);
    }

    @Redirect(
        method = "tesselateWithoutAO",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/block/Block;shouldRenderFace(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;Lnet/minecraft/core/BlockPos;)Z"
        )
    )
    private boolean shouldDrawFrameSideF(BlockState state, BlockGetter world, BlockPos pos, Direction side, BlockPos other_pos, @Local(argsOnly = true) BakedModel model) {
        return RenderHelper.shouldDrawSide(state, world, pos, side, other_pos, model instanceof RetexturingBakedModel rm ? rm.getThemeIndex() : 0);
    }
}
