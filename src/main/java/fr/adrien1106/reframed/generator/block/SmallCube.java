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
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.world.level.ItemLike;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.resources.ResourceLocation;

import static fr.adrien1106.reframed.util.blocks.BlockProperties.CORNER;
import static fr.adrien1106.reframed.util.blocks.Corner.*;
import static fr.adrien1106.reframed.util.blocks.Corner.NORTH_EAST_UP;
import static net.minecraft.data.models.blockstates.VariantProperties.Rotation.*;

public class SmallCube implements RecipeSetter, BlockStateProvider {

    @Override
    public void setRecipe(RecipeOutput exporter, ItemLike convertible) {
        RecipeProvider.stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, convertible, ReFramed.CUBE, 8);
        ShapelessRecipeBuilder
            .shapeless(RecipeCategory.BUILDING_BLOCKS, convertible, 8)
            .requires(ReFramed.CUBE)
            .unlockedBy(FabricRecipeProvider.getHasName(ReFramed.CUBE), FabricRecipeProvider.has(ReFramed.CUBE))
            .unlockedBy(FabricRecipeProvider.getHasName(convertible), FabricRecipeProvider.has(convertible))
            .save(exporter);
    }

    @Override
    public BlockStateGenerator getMultipart(Block block) {
        ResourceLocation small_cube_id = ReFramed.id("small_cube_special");
        return MultiPartGenerator.multiPart(block)
            .with(GBlockstate.when(CORNER, NORTH_EAST_DOWN),
                GBlockstate.variant(small_cube_id, true, R0, R0))
            .with(GBlockstate.when(CORNER, EAST_SOUTH_DOWN),
                GBlockstate.variant(small_cube_id, true, R0, R90))
            .with(GBlockstate.when(CORNER, SOUTH_WEST_DOWN),
                GBlockstate.variant(small_cube_id, true, R0, R180))
            .with(GBlockstate.when(CORNER, WEST_NORTH_DOWN),
                GBlockstate.variant(small_cube_id, true, R0, R270))
            .with(GBlockstate.when(CORNER, EAST_SOUTH_UP),
                GBlockstate.variant(small_cube_id, true, R180, R0))
            .with(GBlockstate.when(CORNER, SOUTH_WEST_UP),
                GBlockstate.variant(small_cube_id, true, R180, R90))
            .with(GBlockstate.when(CORNER, WEST_NORTH_UP),
                GBlockstate.variant(small_cube_id, true, R180, R180))
            .with(GBlockstate.when(CORNER, NORTH_EAST_UP),
                GBlockstate.variant(small_cube_id, true, R180, R270));
    }
}
