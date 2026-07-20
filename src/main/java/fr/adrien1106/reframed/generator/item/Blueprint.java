package fr.adrien1106.reframed.generator.item;

import fr.adrien1106.reframed.generator.RecipeSetter;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.item.Items;
import net.minecraft.data.recipes.RecipeCategory;

public class Blueprint implements RecipeSetter {

    @Override
    public void setRecipe(RecipeOutput exporter, ItemLike convertible) {
        ShapedRecipeBuilder
            .shaped(RecipeCategory.TOOLS, convertible, 3)
            .pattern("PI")
            .pattern("PP")
            .define('P', Items.PAPER)
            .define('I', Items.INK_SAC)
            .unlockedBy(FabricRecipeProvider.getHasName(Items.PAPER), FabricRecipeProvider.has(Items.PAPER))
            .unlockedBy(FabricRecipeProvider.getHasName(convertible), FabricRecipeProvider.has(convertible))
            .save(exporter);
    }
}
