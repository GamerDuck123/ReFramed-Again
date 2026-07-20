package fr.adrien1106.reframed.util.mixin;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;

public interface IBlockRenderInfoMixin {

    void prepareForBlock(BlockState state, BlockPos pos, boolean ao, int theme_index, int model_hash);

    void prepareForBlock(BlockState state, BlockPos pos, long seed, boolean ao, int theme_index, int model_hash);

    int getThemeIndex();

    int getModelHash();
}
