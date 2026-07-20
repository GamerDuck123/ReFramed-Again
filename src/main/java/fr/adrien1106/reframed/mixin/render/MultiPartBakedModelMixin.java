package fr.adrien1106.reframed.mixin.render;

import fr.adrien1106.reframed.util.mixin.IMultipartBakedModelMixin;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.MultiPartBakedModel;
import org.apache.commons.lang3.tuple.Pair;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

@Mixin(MultiPartBakedModel.class)
public class MultiPartBakedModelMixin implements IMultipartBakedModelMixin {

    @Shadow @Final private List<Pair<Predicate<BlockState>, BakedModel>> selectors;

    @Override
    public List<BakedModel> getModels(BlockState state) {
        return selectors.stream().map(pair -> pair.getLeft().test(state) ? pair.getRight(): null).filter(Objects::nonNull).toList();
    }
}
