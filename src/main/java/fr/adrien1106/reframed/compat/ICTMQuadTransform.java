package fr.adrien1106.reframed.compat;

import me.pepperbell.continuity.client.model.QuadProcessors;
import me.pepperbell.continuity.impl.client.ProcessingContextImpl;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;

import java.util.function.Function;
import java.util.function.Supplier;

public interface ICTMQuadTransform extends RenderContext.QuadTransform {

    void invokePrepare(BlockAndTintGetter view, BlockState appearance, BlockState state, BlockPos pos, Supplier<RandomSource> random, RenderContext ctx, boolean manual_culling, Function<TextureAtlasSprite, QuadProcessors.Slice> slice);

    ProcessingContextImpl getProcessingContext();

    void invokeReset();
}
