package fr.adrien1106.reframed.generator.block;

import fr.adrien1106.reframed.ReFramed;
import fr.adrien1106.reframed.generator.BlockStateProvider;
import fr.adrien1106.reframed.generator.GBlockstate;
import fr.adrien1106.reframed.generator.RecipeSetter;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.world.level.block.Block;
import net.minecraft.data.models.blockstates.MultiPartGenerator;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.world.level.ItemLike;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.Direction;

import static fr.adrien1106.reframed.util.blocks.BlockProperties.HALF_LAYERS;
import static net.minecraft.data.models.blockstates.VariantProperties.Rotation.*;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.FACING;

public class SlabsLayer implements RecipeSetter, BlockStateProvider {

    @Override
    public void setRecipe(RecipeOutput exporter, ItemLike convertible) {
        RecipeProvider.stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, convertible, ReFramed.CUBE, 2);
        ShapelessRecipeBuilder
            .shapeless(RecipeCategory.BUILDING_BLOCKS, convertible, 1)
            .requires(ReFramed.SLAB)
            .requires(ReFramed.LAYER)
            .unlockedBy(FabricRecipeProvider.getHasName(ReFramed.CUBE), FabricRecipeProvider.has(ReFramed.CUBE))
            .unlockedBy(FabricRecipeProvider.getHasName(convertible), FabricRecipeProvider.has(convertible))
            .save(exporter);
    }

    @Override
    public MultiPartGenerator getMultipart(Block block) {
        String model_pattern = "slabs_layer_x_special";
        MultiPartGenerator supplier = MultiPartGenerator.multiPart(block);
        for (int i = 1; i <= 4; i++) {
            ResourceLocation model = ReFramed.id(model_pattern.replace("x", i * 2 + ""));
            supplier
                .with(GBlockstate.when(FACING, Direction.DOWN, HALF_LAYERS, i),
                    GBlockstate.variant(model, true, R0, R0))
                .with(GBlockstate.when(FACING, Direction.SOUTH, HALF_LAYERS, i),
                    GBlockstate.variant(model, true, R90, R0))
                .with(GBlockstate.when(FACING, Direction.UP, HALF_LAYERS, i),
                    GBlockstate.variant(model, true, R180, R0))
                .with(GBlockstate.when(FACING, Direction.NORTH, HALF_LAYERS, i),
                    GBlockstate.variant(model, true, R270, R0))
                .with(GBlockstate.when(FACING, Direction.WEST, HALF_LAYERS, i),
                    GBlockstate.variant(model, true, R90, R90))
                .with(GBlockstate.when(FACING, Direction.EAST, HALF_LAYERS, i),
                    GBlockstate.variant(model, true, R90, R270));
        }
        return supplier;
    }
}
