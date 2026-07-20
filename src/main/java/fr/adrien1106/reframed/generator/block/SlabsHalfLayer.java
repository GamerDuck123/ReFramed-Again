package fr.adrien1106.reframed.generator.block;

import fr.adrien1106.reframed.ReFramed;
import fr.adrien1106.reframed.generator.BlockStateProvider;
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

import static fr.adrien1106.reframed.generator.GBlockstate.variant;
import static fr.adrien1106.reframed.generator.GBlockstate.when;
import static fr.adrien1106.reframed.util.blocks.BlockProperties.EDGE;
import static fr.adrien1106.reframed.util.blocks.BlockProperties.EDGE_FACE;
import static fr.adrien1106.reframed.util.blocks.Edge.*;
import static net.minecraft.data.models.blockstates.VariantProperties.Rotation.*;
import static net.minecraft.data.models.blockstates.Condition.or;

public class SlabsHalfLayer implements RecipeSetter, BlockStateProvider {

    @Override
    public void setRecipe(RecipeOutput exporter, ItemLike convertible) {
        RecipeProvider.stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, convertible, ReFramed.CUBE, 2);
        ShapelessRecipeBuilder
            .shapeless(RecipeCategory.BUILDING_BLOCKS, convertible)
            .requires(ReFramed.HALF_LAYER)
            .requires(ReFramed.SLAB)
            .unlockedBy(FabricRecipeProvider.getHasName(ReFramed.CUBE), FabricRecipeProvider.has(ReFramed.CUBE))
            .unlockedBy(FabricRecipeProvider.getHasName(convertible), FabricRecipeProvider.has(convertible))
            .save(exporter);
    }

    @Override
    public BlockStateGenerator getMultipart(Block block) {
        ResourceLocation slab_model = ReFramed.id("slab_special");
        return HalfLayer.getMultipart(block, "second_half_layer")
            .with(
                or(
                    when(EDGE, DOWN_EAST, EDGE_FACE, 0),
                    when(EDGE, DOWN_SOUTH, EDGE_FACE, 0),
                    when(EDGE, WEST_DOWN, EDGE_FACE, 1),
                    when(EDGE, NORTH_DOWN, EDGE_FACE, 1)
                ),
                variant(slab_model, true, R0, R0))
            .with(
                or(
                    when(EDGE, EAST_SOUTH, EDGE_FACE, 1),
                    when(EDGE, DOWN_SOUTH, EDGE_FACE, 1),
                    when(EDGE, SOUTH_WEST, EDGE_FACE, 0),
                    when(EDGE, SOUTH_UP, EDGE_FACE, 0)
                ),
                variant(slab_model, true, R90, R0))
            .with(
                or(
                    when(EDGE, UP_WEST, EDGE_FACE, 0),
                    when(EDGE, UP_NORTH, EDGE_FACE, 0),
                    when(EDGE, EAST_UP, EDGE_FACE, 1),
                    when(EDGE, SOUTH_UP, EDGE_FACE, 1)
                ),
                variant(slab_model, true, R180, R0))
            .with(
                or(
                    when(EDGE, WEST_NORTH, EDGE_FACE, 1),
                    when(EDGE, UP_NORTH, EDGE_FACE, 1),
                    when(EDGE, NORTH_EAST, EDGE_FACE, 0),
                    when(EDGE, NORTH_DOWN, EDGE_FACE, 0)
                ),
                variant(slab_model, true, R270, R0))
            .with(
                or(
                    when(EDGE, SOUTH_WEST, EDGE_FACE, 1),
                    when(EDGE, UP_WEST, EDGE_FACE, 1),
                    when(EDGE, WEST_NORTH, EDGE_FACE, 0),
                    when(EDGE, WEST_DOWN, EDGE_FACE, 0)
                ),
                variant(slab_model, true, R90, R90))
            .with(
                or(
                    when(EDGE, NORTH_EAST, EDGE_FACE, 1),
                    when(EDGE, DOWN_EAST, EDGE_FACE, 1),
                    when(EDGE, EAST_SOUTH, EDGE_FACE, 0),
                    when(EDGE, EAST_UP, EDGE_FACE, 0)
                ),
                variant(slab_model, true, R90, R270))
        ;
    }

}
