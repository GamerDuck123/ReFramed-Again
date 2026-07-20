package fr.adrien1106.reframed.generator.block;

import fr.adrien1106.reframed.ReFramed;
import fr.adrien1106.reframed.generator.BlockStateProvider;
import fr.adrien1106.reframed.generator.GBlockstate;
import fr.adrien1106.reframed.generator.RecipeSetter;
import fr.adrien1106.reframed.util.blocks.Edge;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.world.level.block.Block;
import net.minecraft.data.models.blockstates.MultiPartGenerator;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.world.level.ItemLike;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.resources.ResourceLocation;

import static fr.adrien1106.reframed.util.blocks.BlockProperties.EDGE;
import static fr.adrien1106.reframed.util.blocks.BlockProperties.EDGE_FACE;
import static net.minecraft.data.models.blockstates.VariantProperties.Rotation.*;

public class SlabsStair implements RecipeSetter, BlockStateProvider {

    @Override
    public void setRecipe(RecipeOutput exporter, ItemLike convertible) {
        RecipeProvider.stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, convertible, ReFramed.CUBE);
        ShapelessRecipeBuilder
            .shapeless(RecipeCategory.BUILDING_BLOCKS, convertible)
            .requires(ReFramed.STEP)
            .requires(ReFramed.SLAB)
            .unlockedBy(FabricRecipeProvider.getHasName(ReFramed.CUBE), FabricRecipeProvider.has(ReFramed.CUBE))
            .unlockedBy(FabricRecipeProvider.getHasName(convertible), FabricRecipeProvider.has(convertible))
            .save(exporter);
    }

    @Override
    public MultiPartGenerator getMultipart(Block block) {
        ResourceLocation model_id = ReFramed.id("slabs_stair_special");
        ResourceLocation side_id = ReFramed.id("slabs_stair_side_special");
        return MultiPartGenerator.multiPart(block)
            // BOTTOM
            .with(GBlockstate.when(EDGE, Edge.DOWN_EAST, EDGE_FACE, 0),
                GBlockstate.variant(model_id, true, R0, R0))
            .with(GBlockstate.when(EDGE, Edge.DOWN_SOUTH, EDGE_FACE, 0),
                GBlockstate.variant(model_id, true, R0, R90))
            .with(GBlockstate.when(EDGE, Edge.WEST_DOWN, EDGE_FACE, 1),
                GBlockstate.variant(model_id, true, R0, R180))
            .with(GBlockstate.when(EDGE, Edge.NORTH_DOWN, EDGE_FACE, 1),
                GBlockstate.variant(model_id, true, R0, R270))
            // TOP
            .with(GBlockstate.when(EDGE, Edge.EAST_UP, EDGE_FACE, 1),
                GBlockstate.variant(model_id, true, R180, R0))
            .with(GBlockstate.when(EDGE, Edge.SOUTH_UP, EDGE_FACE, 1),
                GBlockstate.variant(model_id, true, R180, R90))
            .with(GBlockstate.when(EDGE, Edge.UP_WEST, EDGE_FACE, 0),
                GBlockstate.variant(model_id, true, R180, R180))
            .with(GBlockstate.when(EDGE, Edge.UP_NORTH, EDGE_FACE, 0),
                GBlockstate.variant(model_id, true, R180, R270))
            // EAST
            .with(GBlockstate.when(EDGE, Edge.EAST_SOUTH, EDGE_FACE, 0),
                GBlockstate.variant(side_id, true, R0, R0))
            .with(GBlockstate.when(EDGE, Edge.EAST_UP, EDGE_FACE, 0),
                GBlockstate.variant(side_id, true, R90, R0))
            .with(GBlockstate.when(EDGE, Edge.NORTH_EAST, EDGE_FACE, 1),
                GBlockstate.variant(side_id, true, R180, R0))
            .with(GBlockstate.when(EDGE, Edge.DOWN_EAST, EDGE_FACE, 1),
                GBlockstate.variant(side_id, true, R270, R0))
            // SOUTH
            .with(GBlockstate.when(EDGE, Edge.SOUTH_WEST, EDGE_FACE, 0),
                GBlockstate.variant(side_id, true, R0, R90))
            .with(GBlockstate.when(EDGE, Edge.SOUTH_UP, EDGE_FACE, 0),
                GBlockstate.variant(side_id, true, R90, R90))
            .with(GBlockstate.when(EDGE, Edge.EAST_SOUTH, EDGE_FACE, 1),
                GBlockstate.variant(side_id, true, R180, R90))
            .with(GBlockstate.when(EDGE, Edge.DOWN_SOUTH, EDGE_FACE, 1),
                GBlockstate.variant(side_id, true, R270, R90))
            // WEST
            .with(GBlockstate.when(EDGE, Edge.WEST_NORTH, EDGE_FACE, 0),
                GBlockstate.variant(side_id, true, R0, R180))
            .with(GBlockstate.when(EDGE, Edge.UP_WEST, EDGE_FACE, 1),
                GBlockstate.variant(side_id, true, R90, R180))
            .with(GBlockstate.when(EDGE, Edge.SOUTH_WEST, EDGE_FACE, 1),
                GBlockstate.variant(side_id, true, R180, R180))
            .with(GBlockstate.when(EDGE, Edge.WEST_DOWN, EDGE_FACE, 0),
                GBlockstate.variant(side_id, true, R270, R180))
            // NORTH
            .with(GBlockstate.when(EDGE, Edge.NORTH_EAST, EDGE_FACE, 0),
                GBlockstate.variant(side_id, true, R0, R270))
            .with(GBlockstate.when(EDGE, Edge.UP_NORTH, EDGE_FACE, 1),
                GBlockstate.variant(side_id, true, R90, R270))
            .with(GBlockstate.when(EDGE, Edge.WEST_NORTH, EDGE_FACE, 1),
                GBlockstate.variant(side_id, true, R180, R270))
            .with(GBlockstate.when(EDGE, Edge.NORTH_DOWN, EDGE_FACE, 0),
                GBlockstate.variant(side_id, true, R270, R270))
            ;
    }
}
