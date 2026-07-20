package fr.adrien1106.reframed.generator.block;

import fr.adrien1106.reframed.ReFramed;
import fr.adrien1106.reframed.generator.BlockStateProvider;
import fr.adrien1106.reframed.generator.GBlockstate;
import fr.adrien1106.reframed.generator.RecipeSetter;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.world.level.block.Block;
import net.minecraft.data.models.blockstates.BlockStateGenerator;
import net.minecraft.data.models.blockstates.MultiPartGenerator;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.level.ItemLike;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.resources.ResourceLocation;

import static fr.adrien1106.reframed.util.blocks.BlockProperties.EDGE;
import static fr.adrien1106.reframed.util.blocks.Edge.*;
import static fr.adrien1106.reframed.util.blocks.Edge.SOUTH_UP;
import static net.minecraft.data.models.blockstates.VariantProperties.Rotation.*;

public class Step implements RecipeSetter, BlockStateProvider {

    @Override
    public void setRecipe(RecipeOutput exporter, ItemLike convertible) {
        RecipeProvider.stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, convertible, ReFramed.CUBE, 4);
        ShapedRecipeBuilder
            .shaped(RecipeCategory.BUILDING_BLOCKS, convertible, 8)
            .pattern("II")
            .define('I', ReFramed.CUBE)
            .unlockedBy(FabricRecipeProvider.getHasName(ReFramed.CUBE), FabricRecipeProvider.has(ReFramed.CUBE))
            .unlockedBy(FabricRecipeProvider.getHasName(convertible), FabricRecipeProvider.has(convertible))
            .save(exporter);
    }

    @Override
    public BlockStateGenerator getMultipart(Block block) {
        return getMultipart(block, "step");
    }

    public static BlockStateGenerator getMultipart(Block block, String model_name) {
        return getMultipart(block, model_name, model_name);
    }

    public static BlockStateGenerator getMultipart(Block block, String model_name, String reverse_model_name) {
        ResourceLocation model_id = ReFramed.id(model_name + "_special");
        ResourceLocation reverse_model_id = ReFramed.id(reverse_model_name + "_special");
        return MultiPartGenerator.multiPart(block)
            /* X AXIS */
            .with(GBlockstate.when(EDGE, DOWN_EAST),
                GBlockstate.variant(model_id, true, R0, R0))
            .with(GBlockstate.when(EDGE, EAST_UP),
                GBlockstate.variant(reverse_model_id, true, R180, R0))
            .with(GBlockstate.when(EDGE, UP_WEST),
                GBlockstate.variant(model_id, true, R180, R180))
            .with(GBlockstate.when(EDGE, WEST_DOWN),
                GBlockstate.variant(reverse_model_id, true, R0, R180))
            /* Y AXIS */
            .with(GBlockstate.when(EDGE, EAST_SOUTH),
                GBlockstate.variant(model_id, true, R90, R0))
            .with(GBlockstate.when(EDGE, SOUTH_WEST),
                GBlockstate.variant(model_id, true, R90, R90))
            .with(GBlockstate.when(EDGE, WEST_NORTH),
                GBlockstate.variant(model_id, true, R90, R180))
            .with(GBlockstate.when(EDGE, NORTH_EAST),
                GBlockstate.variant(model_id, true, R90, R270))
            /* Z AXIS */
            .with(GBlockstate.when(EDGE, DOWN_SOUTH),
                GBlockstate.variant(reverse_model_id, true, R0, R90))
            .with(GBlockstate.when(EDGE, NORTH_DOWN),
                GBlockstate.variant(model_id, true, R0, R270))
            .with(GBlockstate.when(EDGE, UP_NORTH),
                GBlockstate.variant(reverse_model_id, true, R180, R270))
            .with(GBlockstate.when(EDGE, SOUTH_UP),
                GBlockstate.variant(model_id, true, R180, R90));
    }
}
