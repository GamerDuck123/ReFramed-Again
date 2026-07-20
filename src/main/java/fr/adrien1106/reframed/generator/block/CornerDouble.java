package fr.adrien1106.reframed.generator.block;

import fr.adrien1106.reframed.ReFramed;
import fr.adrien1106.reframed.generator.GBlockstate;
import fr.adrien1106.reframed.util.blocks.Corner;
import net.minecraft.world.level.block.Block;
import net.minecraft.data.models.blockstates.MultiPartGenerator;
import net.minecraft.resources.ResourceLocation;

import static fr.adrien1106.reframed.util.blocks.BlockProperties.CORNER;
import static fr.adrien1106.reframed.util.blocks.BlockProperties.CORNER_FACE;
import static net.minecraft.data.models.blockstates.VariantProperties.Rotation.*;

public class CornerDouble {


    public static MultiPartGenerator getMultipart(Block block, String model_name) {
        ResourceLocation model_id = ReFramed.id(model_name + "_special");
        ResourceLocation side_id = ReFramed.id(model_name + "_side_special");
        return MultiPartGenerator.multiPart(block)
            // BOTTOM
            .with(GBlockstate.when(CORNER, Corner.EAST_SOUTH_DOWN, CORNER_FACE, 2),
                GBlockstate.variant(model_id, true, R0, R0))
            .with(GBlockstate.when(CORNER, Corner.SOUTH_WEST_DOWN, CORNER_FACE, 2),
                GBlockstate.variant(model_id, true, R0, R90))
            .with(GBlockstate.when(CORNER, Corner.WEST_NORTH_DOWN, CORNER_FACE, 2),
                GBlockstate.variant(model_id, true, R0, R180))
            .with(GBlockstate.when(CORNER, Corner.NORTH_EAST_DOWN, CORNER_FACE, 2),
                GBlockstate.variant(model_id, true, R0, R270))
            // TOP
            .with(GBlockstate.when(CORNER, Corner.NORTH_EAST_UP, CORNER_FACE, 2),
                GBlockstate.variant(model_id, true, R180, R0))
            .with(GBlockstate.when(CORNER, Corner.EAST_SOUTH_UP, CORNER_FACE, 2),
                GBlockstate.variant(model_id, true, R180, R90))
            .with(GBlockstate.when(CORNER, Corner.SOUTH_WEST_UP, CORNER_FACE, 2),
                GBlockstate.variant(model_id, true, R180, R180))
            .with(GBlockstate.when(CORNER, Corner.WEST_NORTH_UP, CORNER_FACE, 2),
                GBlockstate.variant(model_id, true, R180, R270))
            // EAST
            .with(GBlockstate.when(CORNER, Corner.EAST_SOUTH_DOWN, CORNER_FACE, 0),
                GBlockstate.variant(side_id, true, R0, R0))
            .with(GBlockstate.when(CORNER, Corner.EAST_SOUTH_UP, CORNER_FACE, 0),
                GBlockstate.variant(side_id, true, R90, R0))
            .with(GBlockstate.when(CORNER, Corner.NORTH_EAST_UP, CORNER_FACE, 1),
                GBlockstate.variant(side_id, true, R180, R0))
            .with(GBlockstate.when(CORNER, Corner.NORTH_EAST_DOWN, CORNER_FACE, 1),
                GBlockstate.variant(side_id, true, R270, R0))
            // SOUTH
            .with(GBlockstate.when(CORNER, Corner.SOUTH_WEST_DOWN, CORNER_FACE, 0),
                GBlockstate.variant(side_id, true, R0, R90))
            .with(GBlockstate.when(CORNER, Corner.SOUTH_WEST_UP, CORNER_FACE, 0),
                GBlockstate.variant(side_id, true, R90, R90))
            .with(GBlockstate.when(CORNER, Corner.EAST_SOUTH_UP, CORNER_FACE, 1),
                GBlockstate.variant(side_id, true, R180, R90))
            .with(GBlockstate.when(CORNER, Corner.EAST_SOUTH_DOWN, CORNER_FACE, 1),
                GBlockstate.variant(side_id, true, R270, R90))
            // WEST
            .with(GBlockstate.when(CORNER, Corner.WEST_NORTH_DOWN, CORNER_FACE, 0),
                GBlockstate.variant(side_id, true, R0, R180))
            .with(GBlockstate.when(CORNER, Corner.WEST_NORTH_UP, CORNER_FACE, 0),
                GBlockstate.variant(side_id, true, R90, R180))
            .with(GBlockstate.when(CORNER, Corner.SOUTH_WEST_UP, CORNER_FACE, 1),
                GBlockstate.variant(side_id, true, R180, R180))
            .with(GBlockstate.when(CORNER, Corner.SOUTH_WEST_DOWN, CORNER_FACE, 1),
                GBlockstate.variant(side_id, true, R270, R180))
            // NORTH
            .with(GBlockstate.when(CORNER, Corner.NORTH_EAST_DOWN, CORNER_FACE, 0),
                GBlockstate.variant(side_id, true, R0, R270))
            .with(GBlockstate.when(CORNER, Corner.NORTH_EAST_UP, CORNER_FACE, 0),
                GBlockstate.variant(side_id, true, R90, R270))
            .with(GBlockstate.when(CORNER, Corner.WEST_NORTH_UP, CORNER_FACE, 1),
                GBlockstate.variant(side_id, true, R180, R270))
            .with(GBlockstate.when(CORNER, Corner.WEST_NORTH_DOWN, CORNER_FACE, 1),
                GBlockstate.variant(side_id, true, R270, R270))
            ;
    }
}
