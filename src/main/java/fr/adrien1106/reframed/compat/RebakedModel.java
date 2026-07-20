package fr.adrien1106.reframed.compat;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;

import java.util.List;
import java.util.Map;

public class RebakedModel implements BakedModel {
    protected final Map<Direction, List<BakedQuad>> face_quads;
    protected boolean ambient_occlusion;

    public RebakedModel(Map<Direction, List<BakedQuad>> face_quads, boolean ambient_occlusion) {
        this.face_quads = face_quads;
        this.ambient_occlusion = ambient_occlusion;
    }

    @Override
    public List<BakedQuad> getQuads(BlockState state, Direction direction, RandomSource random) {
        return face_quads.getOrDefault(direction, List.of());
    }

    @Override
    public boolean useAmbientOcclusion() {
        return ambient_occlusion;
    }

    @Override
    public boolean isGui3d() {
        return false;
    }

    @Override
    public boolean usesBlockLight() {
        return false;
    }

    @Override
    public boolean isCustomRenderer() {
        return false;
    }

    @Override
    public TextureAtlasSprite getParticleIcon() {
        return null;
    }

    @Override
    public ItemTransforms getTransforms() {
        return ItemTransforms.NO_TRANSFORMS;
    }

    @Override
    public ItemOverrides getOverrides() {
        return ItemOverrides.EMPTY;
    }
}
