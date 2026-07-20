package fr.adrien1106.reframed.generator.block;

import fr.adrien1106.reframed.ReFramed;
import fr.adrien1106.reframed.generator.BlockStateProvider;
import fr.adrien1106.reframed.generator.GBlockstate;
import fr.adrien1106.reframed.generator.RecipeSetter;
import fr.adrien1106.reframed.generator.TagGetter;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.world.level.block.Block;
import net.minecraft.data.models.blockstates.BlockStateGenerator;
import net.minecraft.data.models.blockstates.MultiPartGenerator;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.level.ItemLike;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

import static net.minecraft.data.models.blockstates.VariantProperties.Rotation.*;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.*;

public class Pane implements RecipeSetter, TagGetter, BlockStateProvider {

    @Override
    public void setRecipe(RecipeOutput exporter, ItemLike convertible) {
        RecipeProvider.stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, convertible, ReFramed.CUBE, 4);
        ShapedRecipeBuilder
            .shaped(RecipeCategory.BUILDING_BLOCKS, convertible, 32)
            .pattern("III")
            .pattern("I I")
            .pattern("III")
            .define('I', ReFramed.CUBE)
            .unlockedBy(FabricRecipeProvider.getHasName(ReFramed.CUBE), FabricRecipeProvider.has(ReFramed.CUBE))
            .unlockedBy(FabricRecipeProvider.getHasName(convertible), FabricRecipeProvider.has(convertible))
            .save(exporter);
    }

    @Override
    public List<TagKey<Block>> getTags() {
        return List.of(BlockTags.WALLS);
    }

    @Override
    public BlockStateGenerator getMultipart(Block block) {
        ResourceLocation
            pane_side = ReFramed.id("pane_side_special"),
            pane_side_alt = ReFramed.id("pane_side_alt_special"),
            pane_noside = ReFramed.id("pane_noside_special"),
            pane_noside_alt = ReFramed.id("pane_noside_alt_special");
        return MultiPartGenerator.multiPart(block)
            // PILLAR CORE
            .with(GBlockstate.variant(ReFramed.id("pane_post_special"), true, R0, R0))
            // SIDE
            .with(GBlockstate.when(NORTH, true),
                GBlockstate.variant(pane_side, true, R0, R0))
            .with(GBlockstate.when(EAST, true),
                GBlockstate.variant(pane_side, true, R0, R90))
            .with(GBlockstate.when(SOUTH, true),
                GBlockstate.variant(pane_side_alt, true, R0, R0))
            .with(GBlockstate.when(WEST, true),
                GBlockstate.variant(pane_side_alt, true, R0, R90))
            // NOSIDE
            .with(GBlockstate.when(NORTH, false),
                GBlockstate.variant(pane_noside, true, R0, R0))
            .with(GBlockstate.when(EAST, false),
                GBlockstate.variant(pane_noside_alt, true, R0, R0))
            .with(GBlockstate.when(SOUTH, false),
                GBlockstate.variant(pane_noside_alt, true, R0, R90))
            .with(GBlockstate.when(WEST, false),
                GBlockstate.variant(pane_noside, true, R0, R270));
    }
}
