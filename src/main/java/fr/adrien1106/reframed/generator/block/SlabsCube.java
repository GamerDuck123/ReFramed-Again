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
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.world.level.ItemLike;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.Direction;

import static net.minecraft.data.models.blockstates.VariantProperties.Rotation.*;
import static net.minecraft.data.models.blockstates.VariantProperties.Rotation.R90;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.AXIS;

public class SlabsCube implements RecipeSetter, BlockStateProvider {

    @Override
    public void setRecipe(RecipeOutput exporter, ItemLike convertible) {
        RecipeProvider.stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, convertible, ReFramed.CUBE);
        ShapelessRecipeBuilder
            .shapeless(RecipeCategory.BUILDING_BLOCKS, convertible)
            .requires(ReFramed.SLAB, 2)
            .unlockedBy(FabricRecipeProvider.getHasName(ReFramed.CUBE), FabricRecipeProvider.has(ReFramed.CUBE))
            .unlockedBy(FabricRecipeProvider.getHasName(convertible), FabricRecipeProvider.has(convertible))
            .save(exporter);
    }

    @Override
    public MultiPartGenerator getMultipart(Block block) {
        ResourceLocation model_id = ReFramed.id("double_slab_special");
        return MultiPartGenerator.multiPart(block)
            .with(GBlockstate.when(AXIS, Direction.Axis.Y),
                GBlockstate.variant(model_id, true, R0, R0))
            .with(GBlockstate.when(AXIS, Direction.Axis.Z),
                GBlockstate.variant(model_id, true, R270, R0))
            .with(GBlockstate.when(AXIS, Direction.Axis.X),
                GBlockstate.variant(model_id, true, R90, R90));
    }
}
