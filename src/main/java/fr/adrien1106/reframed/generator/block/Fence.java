package fr.adrien1106.reframed.generator.block;

import fr.adrien1106.reframed.ReFramed;
import fr.adrien1106.reframed.generator.BlockStateProvider;
import fr.adrien1106.reframed.generator.GBlockstate;
import fr.adrien1106.reframed.generator.RecipeSetter;
import fr.adrien1106.reframed.generator.TagGetter;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
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

public class Fence implements RecipeSetter, TagGetter, BlockStateProvider {

    @Override
    public void setRecipe(RecipeOutput exporter, ItemLike convertible) {
        RecipeProvider.stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, convertible, ReFramed.CUBE, 4);
        ShapedRecipeBuilder
            .shaped(RecipeCategory.BUILDING_BLOCKS, convertible, 4)
            .pattern("I-I")
            .pattern("I-I")
            .define('I', ReFramed.CUBE)
            .define('-', Blocks.BAMBOO)
            .unlockedBy(FabricRecipeProvider.getHasName(ReFramed.CUBE), FabricRecipeProvider.has(ReFramed.CUBE))
            .unlockedBy(FabricRecipeProvider.getHasName(convertible), FabricRecipeProvider.has(convertible))
            .save(exporter);
    }

    @Override
    public List<TagKey<Block>> getTags() {
        return List.of(BlockTags.FENCES, BlockTags.WOODEN_FENCES);
    }

    @Override
    public BlockStateGenerator getMultipart(Block block) {
        ResourceLocation side_on = ReFramed.id("fence_side_on_special");
        ResourceLocation side_off = ReFramed.id("fence_side_off_special");
        return MultiPartGenerator.multiPart(block)
            .with(GBlockstate.variant(ReFramed.id("fence_core_special"), true, R0, R0))
            // SIDE ON
            .with(GBlockstate.when(NORTH, true),
                GBlockstate.variant(side_on, true, R0, R0))
            .with(GBlockstate.when(EAST, true),
                GBlockstate.variant(side_on, true, R0, R90))
            .with(GBlockstate.when(SOUTH, true),
                GBlockstate.variant(side_on, true, R0, R180))
            .with(GBlockstate.when(WEST, true),
                GBlockstate.variant(side_on, true, R0, R270))
            // SIDE OFF
            .with(GBlockstate.when(NORTH, false),
                GBlockstate.variant(side_off, true, R0, R0))
            .with(GBlockstate.when(EAST, false),
                GBlockstate.variant(side_off, true, R0, R90))
            .with(GBlockstate.when(SOUTH, false),
                GBlockstate.variant(side_off, true, R0, R180))
            .with(GBlockstate.when(WEST, false),
                GBlockstate.variant(side_off, true, R0, R270));
    }
}
