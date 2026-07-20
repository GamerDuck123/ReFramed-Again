package fr.adrien1106.reframed.client.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.renderer.v1.model.ForwardingBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public class DoubleRetexturingBakedModel extends ForwardingBakedModel implements MultiRetexturableModel {

    private final RetexturingBakedModel model_1, model_2;
    public DoubleRetexturingBakedModel(RetexturingBakedModel model_1, RetexturingBakedModel model_2) {
        this.wrapped = model_1.getWrappedModel();
        this.model_1 = model_1;
        this.model_2 = model_2;
    }

    @Override
    public boolean isVanillaAdapter() {
        return false;
    }

    @Override
    public TextureAtlasSprite getParticleIcon() {
        return model_1.getWrappedModel().getParticleIcon();
    }

    @Override
    public List<BakedQuad> getQuads(BlockState blockState, Direction face, RandomSource rand) {
        List<BakedQuad> quads = new ArrayList<>(model_1.getQuads(blockState, face, rand));
        quads.addAll(model_2.getQuads(blockState, face, rand));
        return quads;
    }

    @Override
    public void emitBlockQuads(BlockAndTintGetter world, BlockState state, BlockPos pos, Supplier<RandomSource> randomSupplier, RenderContext context) {
        model_1.emitBlockQuads(world, state, pos, randomSupplier, context);
        model_2.emitBlockQuads(world, state, pos, randomSupplier, context);
    }

    @Override // models are emitted here because no checks are done on items
    public void emitItemQuads(ItemStack stack, Supplier<RandomSource> randomSupplier, RenderContext context) {
        model_1.emitItemQuads(stack, randomSupplier, context);
        model_2.emitItemQuads(stack, randomSupplier, context);
    }

    @Override
    public List<RetexturingBakedModel> models() {
        return List.of(model_1, model_2);
    }
}
