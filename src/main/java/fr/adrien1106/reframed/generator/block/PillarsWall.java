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
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.world.level.ItemLike;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

import static net.minecraft.world.level.block.state.properties.WallSide.*;
import static net.minecraft.data.models.blockstates.VariantProperties.Rotation.*;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.*;

public class PillarsWall implements RecipeSetter, TagGetter, BlockStateProvider {

    @Override
    public void setRecipe(RecipeOutput exporter, ItemLike convertible) {
        RecipeProvider.stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, convertible, ReFramed.CUBE, 1);
        ShapelessRecipeBuilder
            .shapeless(RecipeCategory.BUILDING_BLOCKS, convertible, 2)
            .requires(ReFramed.WALL, 2)
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
            low = ReFramed.id("pillars_wall_low_special"),
            tall = ReFramed.id("pillars_wall_tall_special"),
            none = ReFramed.id("wall_pillar_none_special");
        return MultiPartGenerator.multiPart(block)
            // PILLAR CORE
            .with(GBlockstate.variant(ReFramed.id("wall_core_special"), true, R0, R0))
            // LOW
            .with(GBlockstate.when(NORTH_WALL, LOW),
                GBlockstate.variant(low, true, R0, R0))
            .with(GBlockstate.when(EAST_WALL, LOW),
                GBlockstate.variant(low, true, R0, R90))
            .with(GBlockstate.when(SOUTH_WALL, LOW),
                GBlockstate.variant(low, true, R0, R180))
            .with(GBlockstate.when(WEST_WALL, LOW),
                GBlockstate.variant(low, true, R0, R270))
            // TALL
            .with(GBlockstate.when(NORTH_WALL, TALL),
                GBlockstate.variant(tall, true, R0, R0))
            .with(GBlockstate.when(EAST_WALL, TALL),
                GBlockstate.variant(tall, true, R0, R90))
            .with(GBlockstate.when(SOUTH_WALL, TALL),
                GBlockstate.variant(tall, true, R0, R180))
            .with(GBlockstate.when(WEST_WALL, TALL),
                GBlockstate.variant(tall, true, R0, R270))
            // PILLAR NONE
            .with(GBlockstate.when(NORTH_WALL, NONE),
                GBlockstate.variant(none, true, R0, R0))
            .with(GBlockstate.when(EAST_WALL, NONE),
                GBlockstate.variant(none, true, R0, R90))
            .with(GBlockstate.when(SOUTH_WALL, NONE),
                GBlockstate.variant(none, true, R0, R180))
            .with(GBlockstate.when(WEST_WALL, NONE),
                GBlockstate.variant(none, true, R0, R270));
    }
}
