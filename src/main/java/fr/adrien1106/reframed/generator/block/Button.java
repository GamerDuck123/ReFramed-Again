package fr.adrien1106.reframed.generator.block;

import fr.adrien1106.reframed.ReFramed;
import fr.adrien1106.reframed.generator.BlockStateProvider;
import fr.adrien1106.reframed.generator.GBlockstate;
import fr.adrien1106.reframed.generator.RecipeSetter;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.data.models.blockstates.BlockStateGenerator;
import net.minecraft.data.models.blockstates.MultiPartGenerator;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.world.level.ItemLike;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.Direction;

import static net.minecraft.data.models.blockstates.VariantProperties.Rotation.*;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.*;

public class Button implements RecipeSetter, BlockStateProvider {

    @Override
    public void setRecipe(RecipeOutput exporter, ItemLike convertible) {
        RecipeProvider.stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, convertible, ReFramed.CUBE, 8);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, convertible, 1)
            .requires(ReFramed.CUBE, 1)
            .unlockedBy(FabricRecipeProvider.getHasName(ReFramed.CUBE), FabricRecipeProvider.has(ReFramed.CUBE))
            .unlockedBy(FabricRecipeProvider.getHasName(convertible), FabricRecipeProvider.has(convertible))
            .save(exporter);
    }

    @Override
    public BlockStateGenerator getMultipart(Block block) {
        ResourceLocation button = ReFramed.id("button_special");
        ResourceLocation button_pressed = ReFramed.id("button_pressed_special");
        return MultiPartGenerator.multiPart(block)
            // FLOOR OFF
            .with(GBlockstate.when(POWERED, false, HORIZONTAL_FACING, Direction.NORTH, ATTACH_FACE, AttachFace.FLOOR),
                GBlockstate.variant(button, true, R0, R0))
            .with(GBlockstate.when(POWERED, false, HORIZONTAL_FACING, Direction.EAST, ATTACH_FACE, AttachFace.FLOOR),
                GBlockstate.variant(button, true, R0, R90))
            .with(GBlockstate.when(POWERED, false, HORIZONTAL_FACING, Direction.SOUTH, ATTACH_FACE, AttachFace.FLOOR),
                GBlockstate.variant(button, true, R0, R180))
            .with(GBlockstate.when(POWERED, false, HORIZONTAL_FACING, Direction.WEST, ATTACH_FACE, AttachFace.FLOOR),
                GBlockstate.variant(button, true, R0, R270))
            // CEILING OFF
            .with(GBlockstate.when(POWERED, false, HORIZONTAL_FACING, Direction.SOUTH, ATTACH_FACE, AttachFace.CEILING),
                GBlockstate.variant(button, true, R180, R0))
            .with(GBlockstate.when(POWERED, false, HORIZONTAL_FACING, Direction.WEST, ATTACH_FACE, AttachFace.CEILING),
                GBlockstate.variant(button, true, R180, R90))
            .with(GBlockstate.when(POWERED, false, HORIZONTAL_FACING, Direction.NORTH, ATTACH_FACE, AttachFace.CEILING),
                GBlockstate.variant(button, true, R180, R180))
            .with(GBlockstate.when(POWERED, false, HORIZONTAL_FACING, Direction.EAST, ATTACH_FACE, AttachFace.CEILING),
                GBlockstate.variant(button, true, R180, R270))
            // WALL OFF
            .with(GBlockstate.when(POWERED, false, HORIZONTAL_FACING, Direction.NORTH, ATTACH_FACE, AttachFace.WALL),
                GBlockstate.variant(button, true, R90, R0))
            .with(GBlockstate.when(POWERED, false, HORIZONTAL_FACING, Direction.EAST, ATTACH_FACE, AttachFace.WALL),
                GBlockstate.variant(button, true, R90, R90))
            .with(GBlockstate.when(POWERED, false, HORIZONTAL_FACING, Direction.SOUTH, ATTACH_FACE, AttachFace.WALL),
                GBlockstate.variant(button, true, R90, R180))
            .with(GBlockstate.when(POWERED, false, HORIZONTAL_FACING, Direction.WEST, ATTACH_FACE, AttachFace.WALL),
                GBlockstate.variant(button, true, R90, R270))
            // FLOOR ON
            .with(GBlockstate.when(POWERED, true, HORIZONTAL_FACING, Direction.NORTH, ATTACH_FACE, AttachFace.FLOOR),
                GBlockstate.variant(button_pressed, true, R0, R0))
            .with(GBlockstate.when(POWERED, true, HORIZONTAL_FACING, Direction.EAST, ATTACH_FACE, AttachFace.FLOOR),
                GBlockstate.variant(button_pressed, true, R0, R90))
            .with(GBlockstate.when(POWERED, true, HORIZONTAL_FACING, Direction.SOUTH, ATTACH_FACE, AttachFace.FLOOR),
                GBlockstate.variant(button_pressed, true, R0, R180))
            .with(GBlockstate.when(POWERED, true, HORIZONTAL_FACING, Direction.WEST, ATTACH_FACE, AttachFace.FLOOR),
                GBlockstate.variant(button_pressed, true, R0, R270))
            // CEILING ON
            .with(GBlockstate.when(POWERED, true, HORIZONTAL_FACING, Direction.SOUTH, ATTACH_FACE, AttachFace.CEILING),
                GBlockstate.variant(button_pressed, true, R180, R0))
            .with(GBlockstate.when(POWERED, true, HORIZONTAL_FACING, Direction.WEST, ATTACH_FACE, AttachFace.CEILING),
                GBlockstate.variant(button_pressed, true, R180, R90))
            .with(GBlockstate.when(POWERED, true, HORIZONTAL_FACING, Direction.NORTH, ATTACH_FACE, AttachFace.CEILING),
                GBlockstate.variant(button_pressed, true, R180, R180))
            .with(GBlockstate.when(POWERED, true, HORIZONTAL_FACING, Direction.EAST, ATTACH_FACE, AttachFace.CEILING),
                GBlockstate.variant(button_pressed, true, R180, R270))
            // WALL ON
            .with(GBlockstate.when(POWERED, true, HORIZONTAL_FACING, Direction.NORTH, ATTACH_FACE, AttachFace.WALL),
                GBlockstate.variant(button_pressed, true, R90, R0))
            .with(GBlockstate.when(POWERED, true, HORIZONTAL_FACING, Direction.EAST, ATTACH_FACE, AttachFace.WALL),
                GBlockstate.variant(button_pressed, true, R90, R90))
            .with(GBlockstate.when(POWERED, true, HORIZONTAL_FACING, Direction.SOUTH, ATTACH_FACE, AttachFace.WALL),
                GBlockstate.variant(button_pressed, true, R90, R180))
            .with(GBlockstate.when(POWERED, true, HORIZONTAL_FACING, Direction.WEST, ATTACH_FACE, AttachFace.WALL),
                GBlockstate.variant(button_pressed, true, R90, R270));
    }
}
