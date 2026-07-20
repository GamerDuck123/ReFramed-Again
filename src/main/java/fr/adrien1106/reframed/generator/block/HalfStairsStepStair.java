package fr.adrien1106.reframed.generator.block;

import fr.adrien1106.reframed.ReFramed;
import fr.adrien1106.reframed.generator.BlockStateProvider;
import fr.adrien1106.reframed.generator.GBlockstate;
import fr.adrien1106.reframed.generator.RecipeSetter;
import fr.adrien1106.reframed.util.blocks.Corner;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.world.level.block.Block;
import net.minecraft.data.models.blockstates.MultiPartGenerator;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.world.level.ItemLike;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.resources.ResourceLocation;

import static fr.adrien1106.reframed.util.blocks.BlockProperties.*;
import static net.minecraft.data.models.blockstates.VariantProperties.Rotation.*;
import static net.minecraft.data.models.blockstates.VariantProperties.Rotation.R270;

public class HalfStairsStepStair implements RecipeSetter, BlockStateProvider {

    @Override
    public void setRecipe(RecipeOutput exporter, ItemLike convertible) {
        RecipeProvider.stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, convertible, ReFramed.CUBE);
        ShapelessRecipeBuilder
            .shapeless(RecipeCategory.BUILDING_BLOCKS, convertible)
            .requires(ReFramed.SMALL_CUBE)
            .requires(ReFramed.HALF_STAIR)
            .unlockedBy(FabricRecipeProvider.getHasName(ReFramed.CUBE), FabricRecipeProvider.has(ReFramed.CUBE))
            .unlockedBy(FabricRecipeProvider.getHasName(convertible), FabricRecipeProvider.has(convertible))
            .save(exporter);
    }

    @Override
    public MultiPartGenerator getMultipart(Block block) {
        ResourceLocation model_1_id = ReFramed.id("half_stairs_step_stair_1_special");
        ResourceLocation side_1_id = ReFramed.id("half_stairs_step_stair_side_1_special");
        ResourceLocation model_2_id = ReFramed.id("half_stairs_step_stair_2_special");
        ResourceLocation side_2_id = ReFramed.id("half_stairs_step_stair_side_2_special");
        return MultiPartGenerator.multiPart(block)
            // BOTTOM
            // --- 1 ---
            .with(GBlockstate.when(CORNER, Corner.EAST_SOUTH_DOWN, CORNER_FACE, 2, CORNER_FEATURE, 0),
                GBlockstate.variant(model_1_id, true, R0, R0))
            .with(GBlockstate.when(CORNER, Corner.SOUTH_WEST_DOWN, CORNER_FACE, 2, CORNER_FEATURE, 0),
                GBlockstate.variant(model_1_id, true, R0, R90))
            .with(GBlockstate.when(CORNER, Corner.WEST_NORTH_DOWN, CORNER_FACE, 2, CORNER_FEATURE, 0),
                GBlockstate.variant(model_1_id, true, R0, R180))
            .with(GBlockstate.when(CORNER, Corner.NORTH_EAST_DOWN, CORNER_FACE, 2, CORNER_FEATURE, 0),
                GBlockstate.variant(model_1_id, true, R0, R270))
            // --- 2 ---
            .with(GBlockstate.when(CORNER, Corner.EAST_SOUTH_DOWN, CORNER_FACE, 2, CORNER_FEATURE, 1),
                GBlockstate.variant(model_2_id, true, R0, R0))
            .with(GBlockstate.when(CORNER, Corner.SOUTH_WEST_DOWN, CORNER_FACE, 2, CORNER_FEATURE, 1),
                GBlockstate.variant(model_2_id, true, R0, R90))
            .with(GBlockstate.when(CORNER, Corner.WEST_NORTH_DOWN, CORNER_FACE, 2, CORNER_FEATURE, 1),
                GBlockstate.variant(model_2_id, true, R0, R180))
            .with(GBlockstate.when(CORNER, Corner.NORTH_EAST_DOWN, CORNER_FACE, 2, CORNER_FEATURE, 1),
                GBlockstate.variant(model_2_id, true, R0, R270))
            // TOP
            // --- 1 ---
            .with(GBlockstate.when(CORNER, Corner.NORTH_EAST_UP, CORNER_FACE, 2, CORNER_FEATURE, 1),
                GBlockstate.variant(model_1_id, true, R180, R0))
            .with(GBlockstate.when(CORNER, Corner.EAST_SOUTH_UP, CORNER_FACE, 2, CORNER_FEATURE, 1),
                GBlockstate.variant(model_1_id, true, R180, R90))
            .with(GBlockstate.when(CORNER, Corner.SOUTH_WEST_UP, CORNER_FACE, 2, CORNER_FEATURE, 1),
                GBlockstate.variant(model_1_id, true, R180, R180))
            .with(GBlockstate.when(CORNER, Corner.WEST_NORTH_UP, CORNER_FACE, 2, CORNER_FEATURE, 1),
                GBlockstate.variant(model_1_id, true, R180, R270))
            // --- 2 ---
            .with(GBlockstate.when(CORNER, Corner.NORTH_EAST_UP, CORNER_FACE, 2, CORNER_FEATURE, 0),
                GBlockstate.variant(model_2_id, true, R180, R0))
            .with(GBlockstate.when(CORNER, Corner.EAST_SOUTH_UP, CORNER_FACE, 2, CORNER_FEATURE, 0),
                GBlockstate.variant(model_2_id, true, R180, R90))
            .with(GBlockstate.when(CORNER, Corner.SOUTH_WEST_UP, CORNER_FACE, 2, CORNER_FEATURE, 0),
                GBlockstate.variant(model_2_id, true, R180, R180))
            .with(GBlockstate.when(CORNER, Corner.WEST_NORTH_UP, CORNER_FACE, 2, CORNER_FEATURE, 0),
                GBlockstate.variant(model_2_id, true, R180, R270))
            // EAST
            // --- 1 ---
            .with(GBlockstate.when(CORNER, Corner.EAST_SOUTH_DOWN, CORNER_FACE, 0, CORNER_FEATURE, 0),
                GBlockstate.variant(side_1_id, true, R0, R0))
            .with(GBlockstate.when(CORNER, Corner.EAST_SOUTH_UP, CORNER_FACE, 0, CORNER_FEATURE, 1),
                GBlockstate.variant(side_1_id, true, R90, R0))
            .with(GBlockstate.when(CORNER, Corner.NORTH_EAST_UP, CORNER_FACE, 1, CORNER_FEATURE, 0),
                GBlockstate.variant(side_1_id, true, R180, R0))
            .with(GBlockstate.when(CORNER, Corner.NORTH_EAST_DOWN, CORNER_FACE, 1, CORNER_FEATURE, 1),
                GBlockstate.variant(side_1_id, true, R270, R0))
            // --- 2 ---
            .with(GBlockstate.when(CORNER, Corner.EAST_SOUTH_DOWN, CORNER_FACE, 0, CORNER_FEATURE, 1),
                GBlockstate.variant(side_2_id, true, R0, R0))
            .with(GBlockstate.when(CORNER, Corner.EAST_SOUTH_UP, CORNER_FACE, 0, CORNER_FEATURE, 0),
                GBlockstate.variant(side_2_id, true, R90, R0))
            .with(GBlockstate.when(CORNER, Corner.NORTH_EAST_UP, CORNER_FACE, 1, CORNER_FEATURE, 1),
                GBlockstate.variant(side_2_id, true, R180, R0))
            .with(GBlockstate.when(CORNER, Corner.NORTH_EAST_DOWN, CORNER_FACE, 1, CORNER_FEATURE, 0),
                GBlockstate.variant(side_2_id, true, R270, R0))
            // SOUTH
            // --- 1 ---
            .with(GBlockstate.when(CORNER, Corner.SOUTH_WEST_DOWN, CORNER_FACE, 0, CORNER_FEATURE, 0),
                GBlockstate.variant(side_1_id, true, R0, R90))
            .with(GBlockstate.when(CORNER, Corner.SOUTH_WEST_UP, CORNER_FACE, 0, CORNER_FEATURE, 1),
                GBlockstate.variant(side_1_id, true, R90, R90))
            .with(GBlockstate.when(CORNER, Corner.EAST_SOUTH_UP, CORNER_FACE, 1, CORNER_FEATURE, 0),
                GBlockstate.variant(side_1_id, true, R180, R90))
            .with(GBlockstate.when(CORNER, Corner.EAST_SOUTH_DOWN, CORNER_FACE, 1, CORNER_FEATURE, 1),
                GBlockstate.variant(side_1_id, true, R270, R90))
            // --- 2 ---
            .with(GBlockstate.when(CORNER, Corner.SOUTH_WEST_DOWN, CORNER_FACE, 0, CORNER_FEATURE, 1),
                GBlockstate.variant(side_2_id, true, R0, R90))
            .with(GBlockstate.when(CORNER, Corner.SOUTH_WEST_UP, CORNER_FACE, 0, CORNER_FEATURE, 0),
                GBlockstate.variant(side_2_id, true, R90, R90))
            .with(GBlockstate.when(CORNER, Corner.EAST_SOUTH_UP, CORNER_FACE, 1, CORNER_FEATURE, 1),
                GBlockstate.variant(side_2_id, true, R180, R90))
            .with(GBlockstate.when(CORNER, Corner.EAST_SOUTH_DOWN, CORNER_FACE, 1, CORNER_FEATURE, 0),
                GBlockstate.variant(side_2_id, true, R270, R90))
            // WEST
            // --- 1 ---
            .with(GBlockstate.when(CORNER, Corner.WEST_NORTH_DOWN, CORNER_FACE, 0, CORNER_FEATURE, 0),
                GBlockstate.variant(side_1_id, true, R0, R180))
            .with(GBlockstate.when(CORNER, Corner.WEST_NORTH_UP, CORNER_FACE, 0, CORNER_FEATURE, 1),
                GBlockstate.variant(side_1_id, true, R90, R180))
            .with(GBlockstate.when(CORNER, Corner.SOUTH_WEST_UP, CORNER_FACE, 1, CORNER_FEATURE, 0),
                GBlockstate.variant(side_1_id, true, R180, R180))
            .with(GBlockstate.when(CORNER, Corner.SOUTH_WEST_DOWN, CORNER_FACE, 1, CORNER_FEATURE, 1),
                GBlockstate.variant(side_1_id, true, R270, R180))
            // --- 2 ---
            .with(GBlockstate.when(CORNER, Corner.WEST_NORTH_DOWN, CORNER_FACE, 0, CORNER_FEATURE, 1),
                GBlockstate.variant(side_2_id, true, R0, R180))
            .with(GBlockstate.when(CORNER, Corner.WEST_NORTH_UP, CORNER_FACE, 0, CORNER_FEATURE, 0),
                GBlockstate.variant(side_2_id, true, R90, R180))
            .with(GBlockstate.when(CORNER, Corner.SOUTH_WEST_UP, CORNER_FACE, 1, CORNER_FEATURE, 1),
                GBlockstate.variant(side_2_id, true, R180, R180))
            .with(GBlockstate.when(CORNER, Corner.SOUTH_WEST_DOWN, CORNER_FACE, 1, CORNER_FEATURE, 0),
                GBlockstate.variant(side_2_id, true, R270, R180))
            // NORTH
            // --- 1 ---
            .with(GBlockstate.when(CORNER, Corner.NORTH_EAST_DOWN, CORNER_FACE, 0, CORNER_FEATURE, 0),
                GBlockstate.variant(side_1_id, true, R0, R270))
            .with(GBlockstate.when(CORNER, Corner.NORTH_EAST_UP, CORNER_FACE, 0, CORNER_FEATURE, 1),
                GBlockstate.variant(side_1_id, true, R90, R270))
            .with(GBlockstate.when(CORNER, Corner.WEST_NORTH_UP, CORNER_FACE, 1, CORNER_FEATURE, 0),
                GBlockstate.variant(side_1_id, true, R180, R270))
            .with(GBlockstate.when(CORNER, Corner.WEST_NORTH_DOWN, CORNER_FACE, 1, CORNER_FEATURE, 1),
                GBlockstate.variant(side_1_id, true, R270, R270))
            // --- 2 ---
            .with(GBlockstate.when(CORNER, Corner.NORTH_EAST_DOWN, CORNER_FACE, 0, CORNER_FEATURE, 1),
                GBlockstate.variant(side_2_id, true, R0, R270))
            .with(GBlockstate.when(CORNER, Corner.NORTH_EAST_UP, CORNER_FACE, 0, CORNER_FEATURE, 0),
                GBlockstate.variant(side_2_id, true, R90, R270))
            .with(GBlockstate.when(CORNER, Corner.WEST_NORTH_UP, CORNER_FACE, 1, CORNER_FEATURE, 1),
                GBlockstate.variant(side_2_id, true, R180, R270))
            .with(GBlockstate.when(CORNER, Corner.WEST_NORTH_DOWN, CORNER_FACE, 1, CORNER_FEATURE, 0),
                GBlockstate.variant(side_2_id, true, R270, R270))
            ;
    }
}
