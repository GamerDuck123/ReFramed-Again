package fr.adrien1106.reframed.client.model;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

public abstract class UnbakedRetexturedModel implements UnbakedModel {

    protected final ResourceLocation parent;

    protected int theme_index = 1;
    protected BlockState item_state;

    public UnbakedRetexturedModel(ResourceLocation parent) {
        this.parent = parent;
    }

    public UnbakedRetexturedModel setThemeIndex(int theme_index) {
        this.theme_index = theme_index;
        return this;
    }

    @Override
    public Collection<ResourceLocation> getDependencies() {
        return List.of(parent);
    }

    @Override
    public void resolveParents(Function<ResourceLocation, UnbakedModel> function) {
        function.apply(parent).resolveParents(function);
    }
}
