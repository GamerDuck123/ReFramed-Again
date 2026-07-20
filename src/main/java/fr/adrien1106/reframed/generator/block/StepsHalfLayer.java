package fr.adrien1106.reframed.generator.block;

import fr.adrien1106.reframed.ReFramed;
import fr.adrien1106.reframed.generator.BlockStateProvider;
import fr.adrien1106.reframed.generator.GBlockstate;
import fr.adrien1106.reframed.generator.RecipeSetter;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.world.level.block.Block;
import net.minecraft.data.models.blockstates.BlockStateGenerator;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.world.level.ItemLike;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.resources.ResourceLocation;

import static fr.adrien1106.reframed.util.blocks.BlockProperties.EDGE;
import static fr.adrien1106.reframed.util.blocks.Edge.*;
import static net.minecraft.data.models.blockstates.VariantProperties.Rotation.*;

public class StepsHalfLayer implements RecipeSetter, BlockStateProvider {

    @Override
    public void setRecipe(RecipeOutput exporter, ItemLike convertible) {
        RecipeProvider.stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, convertible, ReFramed.CUBE, 4);
        ShapelessRecipeBuilder
            .shapeless(RecipeCategory.BUILDING_BLOCKS, convertible)
            .requires(ReFramed.HALF_LAYER)
            .requires(ReFramed.STEP)
            .unlockedBy(FabricRecipeProvider.getHasName(ReFramed.CUBE), FabricRecipeProvider.has(ReFramed.CUBE))
            .unlockedBy(FabricRecipeProvider.getHasName(convertible), FabricRecipeProvider.has(convertible))
            .save(exporter);
    }

    @Override
    public BlockStateGenerator getMultipart(Block block) {
        ResourceLocation step_model = ReFramed.id("step_special");
        return HalfLayer.getMultipart(block, "second_half_layer")
            /* X AXIS */
            .with(GBlockstate.when(EDGE, DOWN_EAST),
                GBlockstate.variant(step_model, true, R0, R0))
            .with(GBlockstate.when(EDGE, EAST_UP),
                GBlockstate.variant(step_model, true, R180, R0))
            .with(GBlockstate.when(EDGE, UP_WEST),
                GBlockstate.variant(step_model, true, R180, R180))
            .with(GBlockstate.when(EDGE, WEST_DOWN),
                GBlockstate.variant(step_model, true, R0, R180))
            /* Y AXIS */
            .with(GBlockstate.when(EDGE, EAST_SOUTH),
                GBlockstate.variant(step_model, true, R90, R0))
            .with(GBlockstate.when(EDGE, SOUTH_WEST),
                GBlockstate.variant(step_model, true, R90, R90))
            .with(GBlockstate.when(EDGE, WEST_NORTH),
                GBlockstate.variant(step_model, true, R90, R180))
            .with(GBlockstate.when(EDGE, NORTH_EAST),
                GBlockstate.variant(step_model, true, R90, R270))
            /* Z AXIS */
            .with(GBlockstate.when(EDGE, DOWN_SOUTH),
                GBlockstate.variant(step_model, true, R0, R90))
            .with(GBlockstate.when(EDGE, NORTH_DOWN),
                GBlockstate.variant(step_model, true, R0, R270))
            .with(GBlockstate.when(EDGE, UP_NORTH),
                GBlockstate.variant(step_model, true, R180, R270))
            .with(GBlockstate.when(EDGE, SOUTH_UP),
                GBlockstate.variant(step_model, true, R180, R90))
        ;
    }

}
