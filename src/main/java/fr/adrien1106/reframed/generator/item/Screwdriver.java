package fr.adrien1106.reframed.generator.item;

import fr.adrien1106.reframed.ReFramed;
import fr.adrien1106.reframed.generator.RecipeSetter;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.item.Items;
import net.minecraft.data.recipes.RecipeCategory;

public class Screwdriver implements RecipeSetter {

    @Override
    public void setRecipe(RecipeOutput exporter, ItemLike convertible) {
        ShapedRecipeBuilder
            .shaped(RecipeCategory.TOOLS, convertible)
            .pattern("  I")
            .pattern(" I ")
            .pattern("C  ")
            .define('I', Items.IRON_INGOT)
            .define('C', ReFramed.CUBE)
            .unlockedBy(FabricRecipeProvider.getHasName(ReFramed.CUBE), FabricRecipeProvider.has(ReFramed.CUBE))
            .unlockedBy(FabricRecipeProvider.getHasName(convertible), FabricRecipeProvider.has(convertible))
            .save(exporter);
    }
}
