package fr.adrien1106.reframed.generator;

import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.level.ItemLike;

public interface RecipeSetter {

    void setRecipe(RecipeOutput exporter, ItemLike convertible);
}
