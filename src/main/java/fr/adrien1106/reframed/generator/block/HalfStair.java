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

import static fr.adrien1106.reframed.util.blocks.BlockProperties.CORNER;
import static fr.adrien1106.reframed.util.blocks.BlockProperties.CORNER_FACE;
import static fr.adrien1106.reframed.util.blocks.Corner.*;
import static fr.adrien1106.reframed.util.blocks.Corner.NORTH_EAST_UP;
import static net.minecraft.data.models.blockstates.VariantProperties.Rotation.*;

public class HalfStair implements RecipeSetter, BlockStateProvider {

    @Override
    public void setRecipe(RecipeOutput exporter, ItemLike convertible) {
        RecipeProvider.stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, convertible, ReFramed.CUBE, 2);
        ShapedRecipeBuilder
            .shaped(RecipeCategory.BUILDING_BLOCKS, convertible, 4)
            .pattern("I ")
            .pattern("II")
            .define('I', ReFramed.CUBE)
            .unlockedBy(FabricRecipeProvider.getHasName(ReFramed.CUBE), FabricRecipeProvider.has(ReFramed.CUBE))
            .unlockedBy(FabricRecipeProvider.getHasName(convertible), FabricRecipeProvider.has(convertible))
            .save(exporter);
    }

    @Override
    public BlockStateGenerator getMultipart(Block block) {
        return getMultipart(
            block,
            ReFramed.id("half_stair_down_special"),
            ReFramed.id("half_stair_side_special")
        );
    }

    public static BlockStateGenerator getMultipart(Block block, ResourceLocation model_down, ResourceLocation model_side) {
        return MultiPartGenerator.multiPart(block)
            .with(GBlockstate.when(CORNER, NORTH_EAST_DOWN, CORNER_FACE, 1),
                GBlockstate.variant(model_side, true, R0, R0))
            .with(GBlockstate.when(CORNER, NORTH_EAST_DOWN, CORNER_FACE, 0),
                GBlockstate.variant(model_side, true, R90, R270))
            .with(GBlockstate.when(CORNER, NORTH_EAST_DOWN, CORNER_FACE, 2),
                GBlockstate.variant(model_down, true, R0, R0))

            .with(GBlockstate.when(CORNER, EAST_SOUTH_DOWN, CORNER_FACE, 1),
                GBlockstate.variant(model_side, true, R0, R90))
            .with(GBlockstate.when(CORNER, EAST_SOUTH_DOWN, CORNER_FACE, 0),
                GBlockstate.variant(model_side, true, R90, R0))
            .with(GBlockstate.when(CORNER, EAST_SOUTH_DOWN, CORNER_FACE, 2),
                GBlockstate.variant(model_down, true, R0, R90))

            .with(GBlockstate.when(CORNER, SOUTH_WEST_DOWN, CORNER_FACE, 1),
                GBlockstate.variant(model_side, true, R0, R180))
            .with(GBlockstate.when(CORNER, SOUTH_WEST_DOWN, CORNER_FACE, 0),
                GBlockstate.variant(model_side, true, R90, R90))
            .with(GBlockstate.when(CORNER, SOUTH_WEST_DOWN, CORNER_FACE, 2),
                GBlockstate.variant(model_down, true, R0, R180))

            .with(GBlockstate.when(CORNER, WEST_NORTH_DOWN, CORNER_FACE, 1),
                GBlockstate.variant(model_side, true, R0, R270))
            .with(GBlockstate.when(CORNER, WEST_NORTH_DOWN, CORNER_FACE, 0),
                GBlockstate.variant(model_side, true, R90, R180))
            .with(GBlockstate.when(CORNER, WEST_NORTH_DOWN, CORNER_FACE, 2),
                GBlockstate.variant(model_down, true, R0, R270))

            .with(GBlockstate.when(CORNER, EAST_SOUTH_UP, CORNER_FACE, 0),
                GBlockstate.variant(model_side, true, R180, R0))
            .with(GBlockstate.when(CORNER, EAST_SOUTH_UP, CORNER_FACE, 1),
                GBlockstate.variant(model_side, true, R270, R90))
            .with(GBlockstate.when(CORNER, EAST_SOUTH_UP, CORNER_FACE, 2),
                GBlockstate.variant(model_down, true, R180, R0))

            .with(GBlockstate.when(CORNER, SOUTH_WEST_UP, CORNER_FACE, 0),
                GBlockstate.variant(model_side, true, R180, R90))
            .with(GBlockstate.when(CORNER, SOUTH_WEST_UP, CORNER_FACE, 1),
                GBlockstate.variant(model_side, true, R270, R180))
            .with(GBlockstate.when(CORNER, SOUTH_WEST_UP, CORNER_FACE, 2),
                GBlockstate.variant(model_down, true, R180, R90))

            .with(GBlockstate.when(CORNER, WEST_NORTH_UP, CORNER_FACE, 0),
                GBlockstate.variant(model_side, true, R180, R180))
            .with(GBlockstate.when(CORNER, WEST_NORTH_UP, CORNER_FACE, 1),
                GBlockstate.variant(model_side, true, R270, R270))
            .with(GBlockstate.when(CORNER, WEST_NORTH_UP, CORNER_FACE, 2),
                GBlockstate.variant(model_down, true, R180, R180))

            .with(GBlockstate.when(CORNER, NORTH_EAST_UP, CORNER_FACE, 0),
                GBlockstate.variant(model_side, true, R180, R270))
            .with(GBlockstate.when(CORNER, NORTH_EAST_UP, CORNER_FACE, 1),
                GBlockstate.variant(model_side, true, R270, R0))
            .with(GBlockstate.when(CORNER, NORTH_EAST_UP, CORNER_FACE, 2),
                GBlockstate.variant(model_down, true, R180, R270));
    }
}
