package fr.adrien1106.reframed.util.mixin;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.resources.model.BakedModel;

import java.util.List;

public interface IMultipartBakedModelMixin {

    List<BakedModel> getModels(BlockState state);
}
