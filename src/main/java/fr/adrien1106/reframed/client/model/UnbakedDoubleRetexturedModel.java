package fr.adrien1106.reframed.client.model;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

public class UnbakedDoubleRetexturedModel implements UnbakedModel {

    protected final UnbakedModel model_1;
    protected final UnbakedModel model_2;

    public UnbakedDoubleRetexturedModel(UnbakedRetexturedModel model_1, UnbakedRetexturedModel model_2) {
        this.model_1 = model_1;
        this.model_2 = model_2;
        model_2.setThemeIndex(2);
    }


    @Override
    public Collection<ResourceLocation> getDependencies() {
        return List.of(((List<ResourceLocation>) model_1.getDependencies()).get(0), ((List<ResourceLocation>) model_2.getDependencies()).get(0));
    }

    @Override
    public void resolveParents(Function<ResourceLocation, UnbakedModel> function) {
        model_1.resolveParents(function);
        model_2.resolveParents(function);
    }

    @Nullable
    @Override
    public BakedModel bake(ModelBaker baker, Function<Material, TextureAtlasSprite> texture_getter, ModelState model_bake_settings, ResourceLocation identifier) {
        return new DoubleRetexturingBakedModel(
            (RetexturingBakedModel) model_1.bake(baker, texture_getter, model_bake_settings, identifier),
            (RetexturingBakedModel) model_2.bake(baker, texture_getter, model_bake_settings, identifier)
        );
    }
}
