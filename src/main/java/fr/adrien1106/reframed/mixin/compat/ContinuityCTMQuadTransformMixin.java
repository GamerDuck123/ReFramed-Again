package fr.adrien1106.reframed.mixin.compat;

import fr.adrien1106.reframed.compat.ICTMQuadTransform;
import me.pepperbell.continuity.client.model.QuadProcessors;
import me.pepperbell.continuity.impl.client.ProcessingContextImpl;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.block.BlockState;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockRenderView;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.Function;
import java.util.function.Supplier;

@Mixin(targets = "me.pepperbell.continuity.client.model.CtmBakedModel$CtmQuadTransform")
public abstract class ContinuityCTMQuadTransformMixin implements ICTMQuadTransform {
    @Shadow(remap = false) @Final protected ProcessingContextImpl processingContext;

    @Shadow(remap = false) public abstract void reset();

    @Shadow
    public abstract void prepare(BlockRenderView blockView, BlockState appearanceState, BlockState state, BlockPos pos, Supplier<Random> randomSupplier, RenderContext renderContext, boolean useManualCulling, Function<Sprite, QuadProcessors.Slice> sliceFunc);

    @Redirect(
        method = "transform",
        at = @At(
            value = "INVOKE",
            target = "Lnet/fabricmc/fabric/api/renderer/v1/render/RenderContext;isFaceCulled(Lnet/minecraft/util/math/Direction;)Z"
        )
    )
    private boolean camo_replacement(RenderContext ctx, Direction face) {
        if (ctx == null) return false;
        return ctx.isFaceCulled(face);
    }

    // uses this because invoker did not want to work for some reason
    public void invokePrepare(BlockRenderView view, BlockState appearance, BlockState state, BlockPos pos, Supplier<Random> random, RenderContext ctx, boolean manual_culling, Function<Sprite, QuadProcessors.Slice> slice) {
        prepare(view, appearance, state, pos, random, ctx, manual_culling, slice);
    }

    @Override
    public ProcessingContextImpl getProcessingContext() {
        return processingContext;
    }

    @Override
    public void invokeReset() {
        reset();
    }
}
