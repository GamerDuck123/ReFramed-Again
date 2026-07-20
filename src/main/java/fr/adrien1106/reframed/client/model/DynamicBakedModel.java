package fr.adrien1106.reframed.client.model;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import org.jetbrains.annotations.Nullable;

public interface DynamicBakedModel {
    BakedModel computeQuads(@Nullable BlockAndTintGetter level, BlockState origin_state, @Nullable BlockPos pos, int theme_index);
}
