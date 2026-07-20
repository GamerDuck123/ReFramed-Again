package fr.adrien1106.reframed.generator.block;

import fr.adrien1106.reframed.ReFramed;
import fr.adrien1106.reframed.generator.BlockStateProvider;
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

import java.util.Map;

import static fr.adrien1106.reframed.generator.GBlockstate.variant;
import static fr.adrien1106.reframed.generator.GBlockstate.when;
import static fr.adrien1106.reframed.util.blocks.BlockProperties.EDGE;
import static fr.adrien1106.reframed.util.blocks.BlockProperties.EDGE_FACE;
import static fr.adrien1106.reframed.util.blocks.Edge.*;
import static net.minecraft.data.models.blockstates.VariantProperties.Rotation.*;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.LAYERS;

public class HalfLayer implements RecipeSetter, BlockStateProvider {

    @Override
    public void setRecipe(RecipeOutput exporter, ItemLike convertible) {
        RecipeProvider.stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, convertible, ReFramed.CUBE, 16);
        ShapelessRecipeBuilder
            .shapeless(RecipeCategory.BUILDING_BLOCKS, convertible, 2)
            .requires(ReFramed.LAYER)
            .unlockedBy(FabricRecipeProvider.getHasName(ReFramed.CUBE), FabricRecipeProvider.has(ReFramed.CUBE))
            .unlockedBy(FabricRecipeProvider.getHasName(convertible), FabricRecipeProvider.has(convertible))
            .save(exporter);
    }

    @Override
    public BlockStateGenerator getMultipart(Block block) {
        return getMultipart(block, "half_layer");
    }
        
    public static MultiPartGenerator getMultipart(Block block, String layer) {
        Map<Integer, ResourceLocation> layer_model = Map.of(
            1, ReFramed.id(layer + "_2_special"),
            2, ReFramed.id(layer + "_4_special"),
            3, ReFramed.id(layer + "_6_special"),
            4, ReFramed.id(layer + "_8_special"),
            5, ReFramed.id(layer + "_10_special"),
            6, ReFramed.id(layer + "_12_special"),
            7, ReFramed.id(layer + "_14_special"),
            8, ReFramed.id(layer + "_16_special")
        );
        Map<Integer, ResourceLocation> layer_side = Map.of(
            1, ReFramed.id(layer + "_side_2_special"),
            2, ReFramed.id(layer + "_side_4_special"),
            3, ReFramed.id(layer + "_side_6_special"),
            4, ReFramed.id(layer + "_side_8_special"),
            5, ReFramed.id(layer + "_side_10_special"),
            6, ReFramed.id(layer + "_side_12_special"),
            7, ReFramed.id(layer + "_side_14_special"),
            8, ReFramed.id(layer + "_side_16_special")
        );
        MultiPartGenerator supplier = MultiPartGenerator.multiPart(block);
        // DOWN
        layer_model.forEach((i, model) ->
            supplier.with(when(EDGE, DOWN_EAST, EDGE_FACE, 0, LAYERS, i),
                variant(model, true, R0, R0))
        );
        layer_model.forEach((i, model) ->
            supplier.with(when(EDGE, DOWN_SOUTH, EDGE_FACE, 0, LAYERS, i),
                variant(model, true, R0, R90))
        );
        layer_model.forEach((i, model) ->
            supplier.with(when(EDGE, WEST_DOWN, EDGE_FACE, 1, LAYERS, i),
                variant(model, true, R0, R180))
        );
        layer_model.forEach((i, model) ->
            supplier.with(when(EDGE, NORTH_DOWN, EDGE_FACE, 1, LAYERS, i),
                variant(model, true, R0, R270))
        );
        // UP
        layer_model.forEach((i, model) ->
            supplier.with(when(EDGE, EAST_UP, EDGE_FACE, 1, LAYERS, i),
                variant(model, true, R180, R0))
        );
        layer_model.forEach((i, model) ->
            supplier.with(when(EDGE, SOUTH_UP, EDGE_FACE, 1, LAYERS, i),
                variant(model, true, R180, R90))
        );
        layer_model.forEach((i, model) ->
            supplier.with(when(EDGE, UP_WEST, EDGE_FACE, 0, LAYERS, i),
                variant(model, true, R180, R180))
        );
        layer_model.forEach((i, model) ->
            supplier.with(when(EDGE, UP_NORTH, EDGE_FACE, 0, LAYERS, i),
                variant(model, true, R180, R270))
        );
        // EAST
        layer_side.forEach((i, model) ->
            supplier.with(when(EDGE, EAST_SOUTH, EDGE_FACE, 0, LAYERS, i),
                variant(model, true, R0, R0))
        );
        layer_side.forEach((i, model) ->
            supplier.with(when(EDGE, EAST_UP, EDGE_FACE, 0, LAYERS, i),
                variant(model, true, R90, R0))
        );
        layer_side.forEach((i, model) ->
            supplier.with(when(EDGE, NORTH_EAST, EDGE_FACE, 1, LAYERS, i),
                variant(model, true, R180, R0))
        );
        layer_side.forEach((i, model) ->
            supplier.with(when(EDGE, DOWN_EAST, EDGE_FACE, 1, LAYERS, i),
                variant(model, true, R270, R0))
        );
        // SOUTH
        layer_side.forEach((i, model) ->
            supplier.with(when(EDGE, SOUTH_WEST, EDGE_FACE, 0, LAYERS, i),
                variant(model, true, R0, R90))
        );
        layer_side.forEach((i, model) ->
            supplier.with(when(EDGE, SOUTH_UP, EDGE_FACE, 0, LAYERS, i),
                variant(model, true, R90, R90))
        );
        layer_side.forEach((i, model) ->
            supplier.with(when(EDGE, EAST_SOUTH, EDGE_FACE, 1, LAYERS, i),
                variant(model, true, R180, R90))
        );
        layer_side.forEach((i, model) ->
            supplier.with(when(EDGE, DOWN_SOUTH, EDGE_FACE, 1, LAYERS, i),
                variant(model, true, R270, R90))
        );
        // WEST
        layer_side.forEach((i, model) ->
            supplier.with(when(EDGE, WEST_NORTH, EDGE_FACE, 0, LAYERS, i),
                variant(model, true, R0, R180))
        );
        layer_side.forEach((i, model) ->
            supplier.with(when(EDGE, UP_WEST, EDGE_FACE, 1, LAYERS, i),
                variant(model, true, R90, R180))
        );
        layer_side.forEach((i, model) ->
            supplier.with(when(EDGE, SOUTH_WEST, EDGE_FACE, 1, LAYERS, i),
                variant(model, true, R180, R180))
        );
        layer_side.forEach((i, model) ->
            supplier.with(when(EDGE, WEST_DOWN, EDGE_FACE, 0, LAYERS, i),
                variant(model, true, R270, R180))
        );
        // NORTH
        layer_side.forEach((i, model) ->
            supplier.with(when(EDGE, NORTH_EAST, EDGE_FACE, 0, LAYERS, i),
                variant(model, true, R0, R270))
        );
        layer_side.forEach((i, model) ->
            supplier.with(when(EDGE, UP_NORTH, EDGE_FACE, 1, LAYERS, i),
                variant(model, true, R90, R270))
        );
        layer_side.forEach((i, model) ->
            supplier.with(when(EDGE, WEST_NORTH, EDGE_FACE, 1, LAYERS, i),
                variant(model, true, R180, R270))
        );
        layer_side.forEach((i, model) ->
            supplier.with(when(EDGE, NORTH_DOWN, EDGE_FACE, 0, LAYERS, i),
                variant(model, true, R270, R270))
        );
        return supplier;
    }
}
