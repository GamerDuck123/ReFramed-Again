package fr.adrien1106.reframed.generator.block;

import fr.adrien1106.reframed.generator.RecipeSetter;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.item.Items;
import net.minecraft.data.recipes.RecipeCategory;

public class Cube implements RecipeSetter {

    @Override
    public void setRecipe(RecipeOutput exporter, ItemLike convertible) {
        ShapedRecipeBuilder
            .shaped(RecipeCategory.BUILDING_BLOCKS, convertible)
            .pattern("III")
            .pattern("I~I")
            .pattern("III")
            .define('I', Items.BAMBOO)
            .define('~', Items.STRING)
            .unlockedBy(FabricRecipeProvider.getHasName(Items.BAMBOO), FabricRecipeProvider.has(Items.BAMBOO))
            .unlockedBy(FabricRecipeProvider.getHasName(convertible), FabricRecipeProvider.has(convertible))
            .save(exporter);
    }
}
