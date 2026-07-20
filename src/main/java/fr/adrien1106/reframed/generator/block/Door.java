package fr.adrien1106.reframed.generator.block;

import fr.adrien1106.reframed.ReFramed;
import fr.adrien1106.reframed.generator.BlockStateProvider;
import fr.adrien1106.reframed.generator.GBlockstate;
import fr.adrien1106.reframed.generator.RecipeSetter;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.world.level.block.Block;
import net.minecraft.data.models.blockstates.BlockStateGenerator;
import net.minecraft.data.models.blockstates.MultiPartGenerator;
import net.minecraft.data.models.blockstates.Condition;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.level.ItemLike;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.Direction;

import static net.minecraft.world.level.block.state.properties.DoorHingeSide.*;
import static net.minecraft.data.models.blockstates.VariantProperties.Rotation.*;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.*;

public class Door implements RecipeSetter, BlockStateProvider {

    @Override
    public void setRecipe(RecipeOutput exporter, ItemLike convertible) {
        RecipeProvider.stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, convertible, ReFramed.CUBE, 1);
        ShapedRecipeBuilder
            .shaped(RecipeCategory.BUILDING_BLOCKS, convertible, 3)
            .pattern("II")
            .pattern("II")
            .pattern("II")
            .define('I', ReFramed.CUBE)
            .unlockedBy(FabricRecipeProvider.getHasName(ReFramed.CUBE), FabricRecipeProvider.has(ReFramed.CUBE))
            .unlockedBy(FabricRecipeProvider.getHasName(convertible), FabricRecipeProvider.has(convertible))
            .save(exporter);
    }

    @Override
    public BlockStateGenerator getMultipart(Block block) {
        ResourceLocation door = ReFramed.id("trapdoor_open_special");
        return MultiPartGenerator.multiPart(block)
            // SOUTH
            .with(Condition.or(
                    GBlockstate.when(OPEN, false, HORIZONTAL_FACING, Direction.SOUTH),
                    GBlockstate.when(OPEN, true, HORIZONTAL_FACING, Direction.EAST, DOOR_HINGE, LEFT),
                    GBlockstate.when(OPEN, true, HORIZONTAL_FACING, Direction.WEST, DOOR_HINGE, RIGHT)
                ),
                GBlockstate.variant(door, true, R0, R0))
            // WEST
            .with(Condition.or(
                    GBlockstate.when(OPEN, false, HORIZONTAL_FACING, Direction.WEST),
                    GBlockstate.when(OPEN, true, HORIZONTAL_FACING, Direction.SOUTH, DOOR_HINGE, LEFT),
                    GBlockstate.when(OPEN, true, HORIZONTAL_FACING, Direction.NORTH, DOOR_HINGE, RIGHT)
                ),
                GBlockstate.variant(door, true, R0, R90))
            // NORTH
            .with(Condition.or(
                    GBlockstate.when(OPEN, false, HORIZONTAL_FACING, Direction.NORTH),
                    GBlockstate.when(OPEN, true, HORIZONTAL_FACING, Direction.WEST, DOOR_HINGE, LEFT),
                    GBlockstate.when(OPEN, true, HORIZONTAL_FACING, Direction.EAST, DOOR_HINGE, RIGHT)
                ),
                GBlockstate.variant(door, true, R0, R180))
            // EAST
            .with(Condition.or(
                    GBlockstate.when(OPEN, false, HORIZONTAL_FACING, Direction.EAST),
                    GBlockstate.when(OPEN, true, HORIZONTAL_FACING, Direction.NORTH, DOOR_HINGE, LEFT),
                    GBlockstate.when(OPEN, true, HORIZONTAL_FACING, Direction.SOUTH, DOOR_HINGE, RIGHT)
                ),
                GBlockstate.variant(door, true, R0, R270));
    }
}
