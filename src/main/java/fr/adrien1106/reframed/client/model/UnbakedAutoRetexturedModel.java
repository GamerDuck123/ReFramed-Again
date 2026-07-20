package fr.adrien1106.reframed.client.model;

import fr.adrien1106.reframed.client.ReFramedClient;
import net.fabricmc.fabric.api.renderer.v1.Renderer;
import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;
import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.renderer.v1.mesh.MeshBuilder;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class UnbakedAutoRetexturedModel extends UnbakedRetexturedModel {

	public UnbakedAutoRetexturedModel(ResourceLocation parent) {
		super(parent);
		item_state = Blocks.AIR.defaultBlockState();
	}
	
	@Nullable
	@Override
	public BakedModel bake(ModelBaker baker, Function<Material, TextureAtlasSprite> texture_getter, ModelState bake_settings, ResourceLocation identifier) {
		return new RetexturingBakedModel(
			baker.bake(parent, bake_settings),
			ReFramedClient.HELPER.getCamoAppearanceManager(texture_getter),
			theme_index,
			bake_settings,
			item_state
		) {
			protected Mesh convertModel(BlockState state) {
				Renderer r = ReFramedClient.HELPER.getFabricRenderer();
				MeshBuilder builder = r.meshBuilder();
				QuadEmitter emitter = builder.getEmitter();
				RenderMaterial mat = appearance_manager.getCachedMaterial(state, false);
				
				RandomSource rand = RandomSource.create(42);

				for(Direction direction : DIRECTIONS_AND_NULL) {
					for(BakedQuad quad : wrapped.getQuads(state, direction, rand)) {
						emitter.fromVanilla(quad, mat, direction);
						QuadUvBounds.read(emitter).normalizeUv(emitter, quad.getSprite());
						emitter.tag(emitter.lightFace().ordinal() + 1);
						emitter.emit();
					}
				}
				
				return builder.build();
			}
		};
	}
}
