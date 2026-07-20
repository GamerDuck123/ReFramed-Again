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

public class Slab implements RecipeSetter, BlockStateProvider {

    @Override
    public void setRecipe(RecipeOutput exporter, ItemLike convertible) {
        RecipeProvider.stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, convertible, ReFramed.CUBE, 2);
        ShapedRecipeBuilder
            .shaped(RecipeCategory.BUILDING_BLOCKS, convertible, 6)
            .pattern("III")
            .define('I', ReFramed.CUBE)
            .unlockedBy(FabricRecipeProvider.getHasName(ReFramed.CUBE), FabricRecipeProvider.has(ReFramed.CUBE))
            .unlockedBy(FabricRecipeProvider.getHasName(convertible), FabricRecipeProvider.has(convertible))
            .save(exporter);
    }

    @Override
    public MultiPartGenerator getMultipart(Block block) {
        return getMultipart(block, "slab");
    }

    public static MultiPartGenerator getMultipart(Block block, String model_name) {
        ResourceLocation model_id = ReFramed.id(model_name + "_special");
        return MultiPartGenerator.multiPart(block)
            .with(GBlockstate.when(FACING, Direction.DOWN),
                GBlockstate.variant(model_id, true, R0, R0))
            .with(GBlockstate.when(FACING, Direction.SOUTH),
                GBlockstate.variant(model_id, true, R90, R0))
            .with(GBlockstate.when(FACING, Direction.UP),
                GBlockstate.variant(model_id, true, R180, R0))
            .with(GBlockstate.when(FACING, Direction.NORTH),
                GBlockstate.variant(model_id, true, R270, R0))
            .with(GBlockstate.when(FACING, Direction.WEST),
                GBlockstate.variant(model_id, true, R90, R90))
            .with(GBlockstate.when(FACING, Direction.EAST),
                GBlockstate.variant(model_id, true, R90, R270));
    }


}
