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
import static net.minecraft.data.models.blockstates.VariantProperties.Rotation.R270;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.AXIS;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.FACING;

public class StepsSlab implements RecipeSetter, BlockStateProvider {

    @Override
    public void setRecipe(RecipeOutput exporter, ItemLike convertible) {
        RecipeProvider.stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, convertible, ReFramed.CUBE, 2);
        ShapedRecipeBuilder
            .shaped(RecipeCategory.BUILDING_BLOCKS, convertible)
            .pattern("II")
            .define('I', ReFramed.STEP)
            .unlockedBy(FabricRecipeProvider.getHasName(ReFramed.CUBE), FabricRecipeProvider.has(ReFramed.CUBE))
            .unlockedBy(FabricRecipeProvider.getHasName(convertible), FabricRecipeProvider.has(convertible))
            .save(exporter);
    }

    @Override
    public MultiPartGenerator getMultipart(Block block) {
        ResourceLocation step_id = ReFramed.id("steps_slab_special");
        ResourceLocation step_side_id = ReFramed.id("steps_slab_side_special");
        return MultiPartGenerator.multiPart(block)
            .with(GBlockstate.when(FACING, Direction.DOWN, AXIS, Direction.Axis.X),
                GBlockstate.variant(step_id, true, R0, R180))
            .with(GBlockstate.when(FACING, Direction.DOWN, AXIS, Direction.Axis.Z),
                GBlockstate.variant(step_id, true, R0, R270))
            .with(GBlockstate.when(FACING, Direction.UP, AXIS, Direction.Axis.X),
                GBlockstate.variant(step_id, true, R180, R180))
            .with(GBlockstate.when(FACING, Direction.UP, AXIS, Direction.Axis.Z),
                GBlockstate.variant(step_id, true, R180, R270))
            .with(GBlockstate.when(FACING, Direction.EAST, AXIS, Direction.Axis.Z),
                GBlockstate.variant(step_side_id, true, R0, R0))
            .with(GBlockstate.when(FACING, Direction.EAST, AXIS, Direction.Axis.Y),
                GBlockstate.variant(step_side_id, true, R90, R0))
            .with(GBlockstate.when(FACING, Direction.SOUTH, AXIS, Direction.Axis.X),
                GBlockstate.variant(step_side_id, true, R180, R90))
            .with(GBlockstate.when(FACING, Direction.SOUTH, AXIS, Direction.Axis.Y),
                GBlockstate.variant(step_side_id, true, R90, R90))
            .with(GBlockstate.when(FACING, Direction.WEST, AXIS, Direction.Axis.Z),
                GBlockstate.variant(step_side_id, true, R180, R180))
            .with(GBlockstate.when(FACING, Direction.WEST, AXIS, Direction.Axis.Y),
                GBlockstate.variant(step_side_id, true, R90, R180))
            .with(GBlockstate.when(FACING, Direction.NORTH, AXIS, Direction.Axis.X),
                GBlockstate.variant(step_side_id, true, R0, R270))
            .with(GBlockstate.when(FACING, Direction.NORTH, AXIS, Direction.Axis.Y),
                GBlockstate.variant(step_side_id, true, R90, R270));
    }
}
