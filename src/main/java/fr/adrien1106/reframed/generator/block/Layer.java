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
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.level.ItemLike;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.Direction;

import static net.minecraft.data.models.blockstates.VariantProperties.Rotation.*;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.FACING;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.LAYERS;

public class Layer implements RecipeSetter, BlockStateProvider {

    @Override
    public void setRecipe(RecipeOutput exporter, ItemLike convertible) {
        RecipeProvider.stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, convertible, ReFramed.CUBE, 8);
        ShapedRecipeBuilder
            .shaped(RecipeCategory.BUILDING_BLOCKS, convertible, 16)
            .pattern("II")
            .define('I', ReFramed.CUBE)
            .unlockedBy(FabricRecipeProvider.getHasName(ReFramed.CUBE), FabricRecipeProvider.has(ReFramed.CUBE))
            .unlockedBy(FabricRecipeProvider.getHasName(convertible), FabricRecipeProvider.has(convertible))
            .save(exporter);
    }

    @Override
    public MultiPartGenerator getMultipart(Block block) {
        String model_pattern = "layer_x_special";
        MultiPartGenerator supplier = MultiPartGenerator.multiPart(block);
        for (int i = 1; i <= 8; i++) {
            ResourceLocation model = ReFramed.id(model_pattern.replace("x", i + ""));
            supplier
                .with(GBlockstate.when(FACING, Direction.DOWN, LAYERS, i),
                    GBlockstate.variant(model, true, R0, R0))
                .with(GBlockstate.when(FACING, Direction.SOUTH, LAYERS, i),
                    GBlockstate.variant(model, true, R90, R0))
                .with(GBlockstate.when(FACING, Direction.UP, LAYERS, i),
                    GBlockstate.variant(model, true, R180, R0))
                .with(GBlockstate.when(FACING, Direction.NORTH, LAYERS, i),
                    GBlockstate.variant(model, true, R270, R0))
                .with(GBlockstate.when(FACING, Direction.WEST, LAYERS, i),
                    GBlockstate.variant(model, true, R90, R90))
                .with(GBlockstate.when(FACING, Direction.EAST, LAYERS, i),
                    GBlockstate.variant(model, true, R90, R270));
        }
        return supplier;
    }
}
