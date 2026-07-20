package fr.adrien1106.reframed.generator;

import net.minecraft.world.level.block.Block;
import net.minecraft.data.models.blockstates.BlockStateGenerator;

public interface BlockStateProvider {
    BlockStateGenerator getMultipart(Block block);
}
