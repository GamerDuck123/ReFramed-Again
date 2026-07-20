package fr.adrien1106.reframed.generator.block;

import fr.adrien1106.reframed.ReFramed;
import fr.adrien1106.reframed.generator.BlockStateProvider;
import fr.adrien1106.reframed.generator.GBlockstate;
import fr.adrien1106.reframed.generator.RecipeSetter;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.world.level.block.Block;
import net.minecraft.data.models.blockstates.MultiPartGenerator;
import net.minecraft.data.models.blockstates.Condition;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.level.ItemLike;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.resources.ResourceLocation;

import static fr.adrien1106.reframed.util.blocks.BlockProperties.EDGE;
import static fr.adrien1106.reframed.util.blocks.BlockProperties.STAIR_SHAPE;
import static fr.adrien1106.reframed.util.blocks.Edge.*;
import static fr.adrien1106.reframed.util.blocks.StairShape.*;
import static net.minecraft.data.models.blockstates.VariantProperties.Rotation.*;

public class Stair implements RecipeSetter, BlockStateProvider {

    @Override
    public void setRecipe(RecipeOutput exporter, ItemLike convertible) {
        RecipeProvider.stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, convertible, ReFramed.CUBE);
        ShapedRecipeBuilder
            .shaped(RecipeCategory.BUILDING_BLOCKS, convertible, 4)
            .pattern("I  ")
            .pattern("II ")
            .pattern("III")
            .define('I', ReFramed.CUBE)
            .unlockedBy(FabricRecipeProvider.getHasName(ReFramed.CUBE), FabricRecipeProvider.has(ReFramed.CUBE))
            .unlockedBy(FabricRecipeProvider.getHasName(convertible), FabricRecipeProvider.has(convertible))
            .save(exporter);
    }

    @Override
    public MultiPartGenerator getMultipart(Block block) {
        return getMultipart(block, false);
    }

    public static MultiPartGenerator getMultipart(Block block, boolean is_double) {
        String infix = is_double ? "s_cube" : "";
        ResourceLocation straight_id = ReFramed.id("stair" + infix + "_special");
        ResourceLocation double_outer_id = ReFramed.id("outers_stair" + infix + "_special");
        ResourceLocation inner_id = ReFramed.id("inner_stair" + infix + "_special");
        ResourceLocation outer_id = ReFramed.id("outer_stair" + infix + "_special");
        ResourceLocation outer_side_id = ReFramed.id("outer_side_stair" + infix + "_special");
        return MultiPartGenerator.multiPart(block)
            /* STRAIGHT X AXIS */
            .with(GBlockstate.when(EDGE, DOWN_EAST, STAIR_SHAPE, STRAIGHT),
                GBlockstate.variant(straight_id, true, R0, R0))
            .with(GBlockstate.when(EDGE, EAST_UP, STAIR_SHAPE, STRAIGHT),
                GBlockstate.variant(straight_id, true, R180, R0))
            .with(GBlockstate.when(EDGE, UP_WEST, STAIR_SHAPE, STRAIGHT),
                GBlockstate.variant(straight_id, true, R180, R180))
            .with(GBlockstate.when(EDGE, WEST_DOWN, STAIR_SHAPE, STRAIGHT),
                GBlockstate.variant(straight_id, true, R0, R180))
            /* STRAIGHT Y AXIS */
            .with(GBlockstate.when(EDGE, EAST_SOUTH, STAIR_SHAPE, STRAIGHT),
                GBlockstate.variant(straight_id, true, R90, R0))
            .with(GBlockstate.when(EDGE, SOUTH_WEST, STAIR_SHAPE, STRAIGHT),
                GBlockstate.variant(straight_id, true, R90, R90))
            .with(GBlockstate.when(EDGE, WEST_NORTH, STAIR_SHAPE, STRAIGHT),
                GBlockstate.variant(straight_id, true, R90, R180))
            .with(GBlockstate.when(EDGE, NORTH_EAST, STAIR_SHAPE, STRAIGHT),
                GBlockstate.variant(straight_id, true, R90, R270))
            /* STRAIGHT Z AXIS */
            .with(GBlockstate.when(EDGE, DOWN_SOUTH, STAIR_SHAPE, STRAIGHT),
                GBlockstate.variant(straight_id, true, R0, R90))
            .with(GBlockstate.when(EDGE, NORTH_DOWN, STAIR_SHAPE, STRAIGHT),
                GBlockstate.variant(straight_id, true, R0, R270))
            .with(GBlockstate.when(EDGE, UP_NORTH, STAIR_SHAPE, STRAIGHT),
                GBlockstate.variant(straight_id, true, R180, R270))
            .with(GBlockstate.when(EDGE, SOUTH_UP, STAIR_SHAPE, STRAIGHT),
                GBlockstate.variant(straight_id, true, R180, R90))
            /* INNER BOTTOM */
            .with(Condition.or(
                    GBlockstate.when(EDGE, NORTH_DOWN, STAIR_SHAPE, INNER_RIGHT),
                    GBlockstate.when(EDGE, WEST_NORTH, STAIR_SHAPE, INNER_RIGHT),
                    GBlockstate.when(EDGE, WEST_DOWN, STAIR_SHAPE, INNER_RIGHT)),
                GBlockstate.variant(inner_id, true, R0, R180))
            .with(Condition.or(
                    GBlockstate.when(EDGE, NORTH_DOWN, STAIR_SHAPE, INNER_LEFT),
                    GBlockstate.when(EDGE, NORTH_EAST, STAIR_SHAPE, INNER_RIGHT),
                    GBlockstate.when(EDGE, DOWN_EAST, STAIR_SHAPE, INNER_RIGHT)),
                GBlockstate.variant(inner_id, true, R0, R270))
            .with(Condition.or(
                    GBlockstate.when(EDGE, DOWN_SOUTH, STAIR_SHAPE, INNER_LEFT),
                    GBlockstate.when(EDGE, EAST_SOUTH, STAIR_SHAPE, INNER_RIGHT),
                    GBlockstate.when(EDGE, DOWN_EAST, STAIR_SHAPE, INNER_LEFT)),
                GBlockstate.variant(inner_id, true, R0, R0))
            .with(Condition.or(
                    GBlockstate.when(EDGE, DOWN_SOUTH, STAIR_SHAPE, INNER_RIGHT),
                    GBlockstate.when(EDGE, SOUTH_WEST, STAIR_SHAPE, INNER_RIGHT),
                    GBlockstate.when(EDGE, WEST_DOWN, STAIR_SHAPE, INNER_LEFT)),
                GBlockstate.variant(inner_id, true, R0, R90))
            /* INNER TOP */
            .with(Condition.or(
                    GBlockstate.when(EDGE, EAST_UP, STAIR_SHAPE, INNER_RIGHT),
                    GBlockstate.when(EDGE, NORTH_EAST, STAIR_SHAPE, INNER_LEFT),
                    GBlockstate.when(EDGE, UP_NORTH, STAIR_SHAPE, INNER_LEFT)),
                GBlockstate.variant(inner_id, true, R180, R0))
            .with(Condition.or(
                    GBlockstate.when(EDGE, EAST_UP, STAIR_SHAPE, INNER_LEFT),
                    GBlockstate.when(EDGE, EAST_SOUTH, STAIR_SHAPE, INNER_LEFT),
                    GBlockstate.when(EDGE, SOUTH_UP, STAIR_SHAPE, INNER_LEFT)),
                GBlockstate.variant(inner_id, true, R180, R90))
            .with(Condition.or(
                    GBlockstate.when(EDGE, SOUTH_UP, STAIR_SHAPE, INNER_RIGHT),
                    GBlockstate.when(EDGE, SOUTH_WEST, STAIR_SHAPE, INNER_LEFT),
                    GBlockstate.when(EDGE, UP_WEST, STAIR_SHAPE, INNER_LEFT)),
                GBlockstate.variant(inner_id, true, R180, R180))
            .with(Condition.or(
                    GBlockstate.when(EDGE, UP_NORTH, STAIR_SHAPE, INNER_RIGHT),
                    GBlockstate.when(EDGE, WEST_NORTH, STAIR_SHAPE, INNER_LEFT),
                    GBlockstate.when(EDGE, UP_WEST, STAIR_SHAPE, INNER_RIGHT)),
                GBlockstate.variant(inner_id, true, R180, R270))
            /* OUTER BOTTOM */
            .with(Condition.or(
                    GBlockstate.when(EDGE, DOWN_SOUTH, STAIR_SHAPE, SECOND_OUTER_LEFT),
                    GBlockstate.when(EDGE, DOWN_EAST, STAIR_SHAPE, SECOND_OUTER_LEFT)),
                GBlockstate.variant(outer_id, true, R0, R0))
            .with(Condition.or(
                    GBlockstate.when(EDGE, DOWN_SOUTH, STAIR_SHAPE, SECOND_OUTER_RIGHT),
                    GBlockstate.when(EDGE, WEST_DOWN, STAIR_SHAPE, FIRST_OUTER_LEFT)),
                GBlockstate.variant(outer_id, true, R0, R90))
            .with(Condition.or(
                    GBlockstate.when(EDGE, NORTH_DOWN, STAIR_SHAPE, FIRST_OUTER_RIGHT),
                    GBlockstate.when(EDGE, WEST_DOWN, STAIR_SHAPE, FIRST_OUTER_RIGHT)),
                GBlockstate.variant(outer_id, true, R0, R180))
            .with(Condition.or(
                    GBlockstate.when(EDGE, NORTH_DOWN, STAIR_SHAPE, FIRST_OUTER_LEFT),
                    GBlockstate.when(EDGE, DOWN_EAST, STAIR_SHAPE, SECOND_OUTER_RIGHT)),
                GBlockstate.variant(outer_id, true, R0, R270))
            /* OUTER TOP */
            .with(Condition.or(
                    GBlockstate.when(EDGE, UP_NORTH, STAIR_SHAPE, SECOND_OUTER_LEFT),
                    GBlockstate.when(EDGE, EAST_UP, STAIR_SHAPE, FIRST_OUTER_RIGHT)),
                GBlockstate.variant(outer_id, true, R180, R0))
            .with(Condition.or(
                    GBlockstate.when(EDGE, SOUTH_UP, STAIR_SHAPE, FIRST_OUTER_LEFT),
                    GBlockstate.when(EDGE, EAST_UP, STAIR_SHAPE, FIRST_OUTER_LEFT)),
                GBlockstate.variant(outer_id, true, R180, R90))
            .with(Condition.or(
                    GBlockstate.when(EDGE, SOUTH_UP, STAIR_SHAPE, FIRST_OUTER_RIGHT),
                    GBlockstate.when(EDGE, UP_WEST, STAIR_SHAPE, SECOND_OUTER_LEFT)),
                GBlockstate.variant(outer_id, true, R180, R180))
            .with(Condition.or(
                    GBlockstate.when(EDGE, UP_NORTH, STAIR_SHAPE, SECOND_OUTER_RIGHT),
                    GBlockstate.when(EDGE, UP_WEST, STAIR_SHAPE, SECOND_OUTER_RIGHT)),
                GBlockstate.variant(outer_id, true, R180, R270))
            /* OUTER EAST */
            .with(Condition.or(
                    GBlockstate.when(EDGE, EAST_SOUTH, STAIR_SHAPE, SECOND_OUTER_RIGHT),
                    GBlockstate.when(EDGE, DOWN_EAST, STAIR_SHAPE, FIRST_OUTER_LEFT)),
                GBlockstate.variant(outer_side_id, true, R0, R0))
            .with(Condition.or(
                    GBlockstate.when(EDGE, EAST_SOUTH, STAIR_SHAPE, SECOND_OUTER_LEFT),
                    GBlockstate.when(EDGE, EAST_UP, STAIR_SHAPE, SECOND_OUTER_LEFT)),
                GBlockstate.variant(outer_side_id, true, R90, R0))
            .with(Condition.or(
                    GBlockstate.when(EDGE, NORTH_EAST, STAIR_SHAPE, FIRST_OUTER_LEFT),
                    GBlockstate.when(EDGE, EAST_UP, STAIR_SHAPE, SECOND_OUTER_RIGHT)),
                GBlockstate.variant(outer_side_id, true, R180, R0))
            .with(Condition.or(
                    GBlockstate.when(EDGE, NORTH_EAST, STAIR_SHAPE, FIRST_OUTER_RIGHT),
                    GBlockstate.when(EDGE, DOWN_EAST, STAIR_SHAPE, FIRST_OUTER_RIGHT)),
                GBlockstate.variant(outer_side_id, true, R270, R0))
            /* OUTER SOUTH */
            .with(Condition.or(
                    GBlockstate.when(EDGE, SOUTH_WEST, STAIR_SHAPE, SECOND_OUTER_RIGHT),
                    GBlockstate.when(EDGE, DOWN_SOUTH, STAIR_SHAPE, FIRST_OUTER_RIGHT)),
                GBlockstate.variant(outer_side_id, true, R0, R90))
            .with(Condition.or(
                    GBlockstate.when(EDGE, SOUTH_WEST, STAIR_SHAPE, SECOND_OUTER_LEFT),
                    GBlockstate.when(EDGE, SOUTH_UP, STAIR_SHAPE, SECOND_OUTER_RIGHT)),
                GBlockstate.variant(outer_side_id, true, R90, R90))
            .with(Condition.or(
                    GBlockstate.when(EDGE, EAST_SOUTH, STAIR_SHAPE, FIRST_OUTER_LEFT),
                    GBlockstate.when(EDGE, SOUTH_UP, STAIR_SHAPE, SECOND_OUTER_LEFT)),
                GBlockstate.variant(outer_side_id, true, R180, R90))
            .with(Condition.or(
                    GBlockstate.when(EDGE, EAST_SOUTH, STAIR_SHAPE, FIRST_OUTER_RIGHT),
                    GBlockstate.when(EDGE, DOWN_SOUTH, STAIR_SHAPE, FIRST_OUTER_LEFT)),
                GBlockstate.variant(outer_side_id, true, R270, R90))
            /* OUTER WEST */
            .with(Condition.or(
                    GBlockstate.when(EDGE, WEST_NORTH, STAIR_SHAPE, SECOND_OUTER_RIGHT),
                    GBlockstate.when(EDGE, WEST_DOWN, STAIR_SHAPE, SECOND_OUTER_RIGHT)),
                GBlockstate.variant(outer_side_id, true, R0, R180))
            .with(Condition.or(
                    GBlockstate.when(EDGE, WEST_NORTH, STAIR_SHAPE, SECOND_OUTER_LEFT),
                    GBlockstate.when(EDGE, UP_WEST, STAIR_SHAPE, FIRST_OUTER_RIGHT)),
                GBlockstate.variant(outer_side_id, true, R90, R180))
            .with(Condition.or(
                    GBlockstate.when(EDGE, SOUTH_WEST, STAIR_SHAPE, FIRST_OUTER_LEFT),
                    GBlockstate.when(EDGE, UP_WEST, STAIR_SHAPE, FIRST_OUTER_LEFT)),
                GBlockstate.variant(outer_side_id, true, R180, R180))
            .with(Condition.or(
                    GBlockstate.when(EDGE, SOUTH_WEST, STAIR_SHAPE, FIRST_OUTER_RIGHT),
                    GBlockstate.when(EDGE, WEST_DOWN, STAIR_SHAPE, SECOND_OUTER_LEFT)),
                GBlockstate.variant(outer_side_id, true, R270, R180))
            /* OUTER NORTH */
            .with(Condition.or(
                    GBlockstate.when(EDGE, NORTH_EAST, STAIR_SHAPE, SECOND_OUTER_RIGHT),
                    GBlockstate.when(EDGE, NORTH_DOWN, STAIR_SHAPE, SECOND_OUTER_LEFT)),
                GBlockstate.variant(outer_side_id, true, R0, R270))
            .with(Condition.or(
                    GBlockstate.when(EDGE, NORTH_EAST, STAIR_SHAPE, SECOND_OUTER_LEFT),
                    GBlockstate.when(EDGE, UP_NORTH, STAIR_SHAPE, FIRST_OUTER_LEFT)),
                GBlockstate.variant(outer_side_id, true, R90, R270))
            .with(Condition.or(
                    GBlockstate.when(EDGE, WEST_NORTH, STAIR_SHAPE, FIRST_OUTER_LEFT),
                    GBlockstate.when(EDGE, UP_NORTH, STAIR_SHAPE, FIRST_OUTER_RIGHT)),
                GBlockstate.variant(outer_side_id, true, R180, R270))
            .with(Condition.or(
                    GBlockstate.when(EDGE, WEST_NORTH, STAIR_SHAPE, FIRST_OUTER_RIGHT),
                    GBlockstate.when(EDGE, NORTH_DOWN, STAIR_SHAPE, SECOND_OUTER_RIGHT)),
                GBlockstate.variant(outer_side_id, true, R270, R270))
            /* OUTER BOTTOM */
            .with(Condition.or(
                    GBlockstate.when(EDGE, DOWN_SOUTH, STAIR_SHAPE, OUTER_LEFT),
                    GBlockstate.when(EDGE, DOWN_EAST, STAIR_SHAPE, OUTER_LEFT),
                    GBlockstate.when(EDGE, EAST_SOUTH, STAIR_SHAPE, OUTER_RIGHT)),
                GBlockstate.variant(double_outer_id, true, R0, R0))
            .with(Condition.or(
                    GBlockstate.when(EDGE, DOWN_SOUTH, STAIR_SHAPE, OUTER_RIGHT),
                    GBlockstate.when(EDGE, WEST_DOWN, STAIR_SHAPE, OUTER_LEFT),
                    GBlockstate.when(EDGE, SOUTH_WEST, STAIR_SHAPE, OUTER_RIGHT)),
                GBlockstate.variant(double_outer_id, true, R0, R90))
            .with(Condition.or(
                    GBlockstate.when(EDGE, NORTH_DOWN, STAIR_SHAPE, OUTER_RIGHT),
                    GBlockstate.when(EDGE, WEST_DOWN, STAIR_SHAPE, OUTER_RIGHT),
                    GBlockstate.when(EDGE, WEST_NORTH, STAIR_SHAPE, OUTER_RIGHT)),
                GBlockstate.variant(double_outer_id, true, R0, R180))
            .with(Condition.or(
                    GBlockstate.when(EDGE, NORTH_DOWN, STAIR_SHAPE, OUTER_LEFT),
                    GBlockstate.when(EDGE, DOWN_EAST, STAIR_SHAPE, OUTER_RIGHT),
                    GBlockstate.when(EDGE, NORTH_EAST, STAIR_SHAPE, OUTER_RIGHT)),
                GBlockstate.variant(double_outer_id, true, R0, R270))
            /* OUTER TOP */
            .with(Condition.or(
                    GBlockstate.when(EDGE, UP_NORTH, STAIR_SHAPE, OUTER_LEFT),
                    GBlockstate.when(EDGE, EAST_UP, STAIR_SHAPE, OUTER_RIGHT),
                    GBlockstate.when(EDGE, NORTH_EAST, STAIR_SHAPE, OUTER_LEFT)),
                GBlockstate.variant(double_outer_id, true, R180, R0))
            .with(Condition.or(
                    GBlockstate.when(EDGE, SOUTH_UP, STAIR_SHAPE, OUTER_LEFT),
                    GBlockstate.when(EDGE, EAST_UP, STAIR_SHAPE, OUTER_LEFT),
                    GBlockstate.when(EDGE, EAST_SOUTH, STAIR_SHAPE, OUTER_LEFT)),
                GBlockstate.variant(double_outer_id, true, R180, R90))
            .with(Condition.or(
                    GBlockstate.when(EDGE, SOUTH_UP, STAIR_SHAPE, OUTER_RIGHT),
                    GBlockstate.when(EDGE, UP_WEST, STAIR_SHAPE, OUTER_LEFT),
                    GBlockstate.when(EDGE, SOUTH_WEST, STAIR_SHAPE, OUTER_LEFT)),
                GBlockstate.variant(double_outer_id, true, R180, R180))
            .with(Condition.or(
                    GBlockstate.when(EDGE, UP_NORTH, STAIR_SHAPE, OUTER_RIGHT),
                    GBlockstate.when(EDGE, UP_WEST, STAIR_SHAPE, OUTER_RIGHT),
                    GBlockstate.when(EDGE, WEST_NORTH, STAIR_SHAPE, OUTER_LEFT)),
                GBlockstate.variant(double_outer_id, true, R180, R270));
    }
}
