package fr.adrien1106.reframed.generator.block;

import fr.adrien1106.reframed.ReFramed;
import fr.adrien1106.reframed.generator.BlockStateProvider;
import fr.adrien1106.reframed.generator.GBlockstate;
import fr.adrien1106.reframed.generator.RecipeSetter;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.data.models.blockstates.BlockStateGenerator;
import net.minecraft.data.models.blockstates.MultiPartGenerator;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.level.ItemLike;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.Direction;

import static net.minecraft.data.models.blockstates.VariantProperties.Rotation.*;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.*;

public class Trapdoor implements RecipeSetter, BlockStateProvider {

    @Override
    public void setRecipe(RecipeOutput exporter, ItemLike convertible) {
        RecipeProvider.stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, convertible, ReFramed.CUBE, 2);
        ShapedRecipeBuilder
            .shaped(RecipeCategory.BUILDING_BLOCKS, convertible, 2)
            .pattern("  I")
            .pattern("III")
            .pattern("II ")
            .define('I', ReFramed.CUBE)
            .unlockedBy(FabricRecipeProvider.getHasName(ReFramed.CUBE), FabricRecipeProvider.has(ReFramed.CUBE))
            .unlockedBy(FabricRecipeProvider.getHasName(convertible), FabricRecipeProvider.has(convertible))
            .save(exporter);
    }

    @Override
    public BlockStateGenerator getMultipart(Block block) {
        ResourceLocation open = ReFramed.id("trapdoor_open_special");
        return MultiPartGenerator.multiPart(block)
            .with(GBlockstate.when(OPEN, false, HALF, Half.BOTTOM),
                GBlockstate.variant(ReFramed.id("trapdoor_bottom_special"), true, R0, R0))
            .with(GBlockstate.when(OPEN, false, HALF, Half.TOP),
                GBlockstate.variant(ReFramed.id("trapdoor_top_special"), true, R0, R0))
            .with(GBlockstate.when(OPEN, true, HORIZONTAL_FACING, Direction.NORTH),
                GBlockstate.variant(open, true, R0, R0))
            .with(GBlockstate.when(OPEN, true, HORIZONTAL_FACING, Direction.EAST),
                GBlockstate.variant(open, true, R0, R90))
            .with(GBlockstate.when(OPEN, true, HORIZONTAL_FACING, Direction.SOUTH),
                GBlockstate.variant(open, true, R0, R180))
            .with(GBlockstate.when(OPEN, true, HORIZONTAL_FACING, Direction.WEST),
                GBlockstate.variant(open, true, R0, R270));
    }
}
