// TODO(Ravel): Failed to fully resolve file: null cannot be cast to non-null type com.intellij.psi.PsiJavaCodeReferenceElement
package fr.adrien1106.reframed.client.model.apperance;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import fr.adrien1106.reframed.ReFramed;
import fr.adrien1106.reframed.client.ReFramedClient;
import fr.adrien1106.reframed.client.model.DynamicBakedModel;
import fr.adrien1106.reframed.client.model.QuadPosBounds;
import fr.adrien1106.reframed.compat.RebakedModel;
import fr.adrien1106.reframed.mixin.model.WeightedBakedModelAccessor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.renderer.v1.Renderer;
import net.fabricmc.fabric.api.renderer.v1.material.BlendMode;
import net.fabricmc.fabric.api.renderer.v1.material.MaterialFinder;
import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

@Environment(EnvType.CLIENT)
public class CamoAppearanceManager {


	protected static final Material DEFAULT_SPRITE_MAIN = new Material(InventoryMenu.BLOCK_ATLAS, new ResourceLocation(ReFramed.MODID, "block/framed_block"));
	protected static final Material DEFAULT_SPRITE_SECONDARY = new Material(InventoryMenu.BLOCK_ATLAS, new ResourceLocation(ReFramed.MODID, "block/framed_accent_block"));
	private static final Material BARRIER_SPRITE_ID = new Material(InventoryMenu.BLOCK_ATLAS, new ResourceLocation("minecraft:item/barrier"));
	private static final Cache<BlockState, CamoAppearance> APPEARANCE_CACHE = CacheBuilder.newBuilder().maximumSize(2048).build();

	public CamoAppearanceManager(Function<Material, TextureAtlasSprite> spriteLookup) {
		MaterialFinder finder = ReFramedClient.HELPER.getFabricRenderer().materialFinder();
		for(BlendMode blend : BlendMode.values()) {
			finder.clear().disableDiffuse(false).blendMode(blend);
			
			materials.put(blend, finder.ambientOcclusion(TriState.TRUE).find());
			ao_materials.put(blend, finder.ambientOcclusion(TriState.DEFAULT).find()); //not "true" since that *forces* AO, i just want to *allow* AO
		}
		
		TextureAtlasSprite sprite = spriteLookup.apply(DEFAULT_SPRITE_MAIN);
		if(sprite == null) throw new IllegalStateException("Couldn't locate " + DEFAULT_SPRITE_MAIN + " !");
		this.default_appearance = new SingleSpriteAppearance(sprite, materials.get(BlendMode.CUTOUT), serial_number.getAndIncrement());

		sprite = spriteLookup.apply(DEFAULT_SPRITE_SECONDARY);
		if(sprite == null) throw new IllegalStateException("Couldn't locate " + DEFAULT_SPRITE_MAIN + " !");
		this.accent_appearance = new SingleSpriteAppearance(sprite, materials.get(BlendMode.CUTOUT), serial_number.getAndIncrement());

		sprite = spriteLookup.apply(BARRIER_SPRITE_ID);
		this.barrier_appearance = new SingleSpriteAppearance(sprite, materials.get(BlendMode.CUTOUT), serial_number.getAndIncrement());
	}
	
	private final CamoAppearance default_appearance;
	private final CamoAppearance accent_appearance;
	private final CamoAppearance barrier_appearance;

	private final AtomicInteger serial_number = new AtomicInteger(0); //Mutable
	
	private final EnumMap<BlendMode, RenderMaterial> ao_materials = new EnumMap<>(BlendMode.class);
	private final EnumMap<BlendMode, RenderMaterial> materials = new EnumMap<>(BlendMode.class); //Immutable contents

	public static void dumpCahe() {
		APPEARANCE_CACHE.invalidateAll();
	}

	public CamoAppearance getDefaultAppearance(int appearance) {
		return appearance == 2 ? accent_appearance: default_appearance;
	}
	
	public CamoAppearance getCamoAppearance(BlockAndTintGetter world, BlockState state, BlockPos pos, int theme_index, boolean item) {
		BakedModel model = Minecraft.getInstance().getBlockRenderer().getBlockModel(state);

		// add support for connected textures that uses dynamic baking
		if (model instanceof DynamicBakedModel dynamic_model) {
			// cache items as they get rendered more often
			if (item && APPEARANCE_CACHE.asMap().containsKey(state)) return APPEARANCE_CACHE.getIfPresent(state);

			model = dynamic_model.computeQuads(world, state, pos, theme_index);
			// if model isn't rebaked its just wrapped (i.e. not dynamic and may be cached)
			if (model instanceof RebakedModel) {
				CamoAppearance appearance = computeAppearance(model, state, !item);
				if (item) APPEARANCE_CACHE.put(state, appearance);
				return appearance;
			}
		}

		// refresh cache
		if (APPEARANCE_CACHE.asMap().containsKey(state)) return APPEARANCE_CACHE.getIfPresent(state);

		CamoAppearance appearance = computeAppearance(model, state, false);
		APPEARANCE_CACHE.put(state, appearance);
		return appearance;
	}
	
	public RenderMaterial getCachedMaterial(BlockState state, boolean ao) {
		Map<BlendMode, RenderMaterial> m = ao ? ao_materials : materials;
		return m.get(BlendMode.fromRenderLayer(ItemBlockRenderTypes.getChunkRenderType(state)));
	}
	
	// I'm pretty sure ConcurrentHashMap semantics allow for this function to be called multiple times on the same key, on different threads.
	// The computeIfAbsent map update will work without corrupting the map, but there will be some "wasted effort" computing the value twice.
	// The results are going to be the same, apart from their serialNumbers differing (= their equals & hashCode differing).
	// Tiny amount of wasted space in some caches if CamoAppearances are used as a map key, then. IMO it's not a critical issue.
	private CamoAppearance computeAppearance(BakedModel model, BlockState state, boolean is_dynamic) {
		if(state.getBlock() == Blocks.BARRIER) return barrier_appearance;

		if (!(model instanceof WeightedBakedModelAccessor weighted_model)) {
			return new ComputedAppearance(
				getAppearance(model),
				getCachedMaterial(state, true),
				getCachedMaterial(state, false),
				is_dynamic ? -1 : serial_number.getAndIncrement()
			);
		}
		List<WeightedEntry.Wrapper<Appearance>> appearances = weighted_model.getList().stream()
			.map(baked_model -> WeightedEntry.wrap(getAppearance(baked_model.getData()), baked_model.getWeight().asInt()))
			.toList();

		return new WeightedComputedAppearance(
			appearances,
			getCachedMaterial(state, true),
			getCachedMaterial(state, false),
			is_dynamic ? -1 : serial_number.getAndIncrement()
		);
	}

	private Appearance getAppearance(BakedModel model) {
		// Only for parsing vanilla quads:
		Renderer r = ReFramedClient.HELPER.getFabricRenderer();
		QuadEmitter quad_emitter = r.meshBuilder().getEmitter();
		RenderMaterial material = r.materialFinder().clear().find();
		RandomSource random = RandomSource.create();

		Map<Direction, List<SpriteProperties>> sprites = new EnumMap<>(Direction.class);

		//Read quads off the model by their `cullface`
		Arrays.stream(Direction.values()).forEach(direction -> {
			List<BakedQuad> quads = model.getQuads(null, direction, random);
			if(quads.isEmpty()) { // add default appearance if none present
				sprites.put(direction, default_appearance.getSprites(direction, 0));
				return;
			}

			sprites.put(direction, new ArrayList<>());
			quads.forEach(quad -> {
				TextureAtlasSprite sprite = quad.getSprite();
				if(sprite == null) return;
				sprites.computeIfPresent(direction, (dir, pairs) -> {
					quad_emitter.fromVanilla(quad, material, direction);
					pairs.add(new SpriteProperties(
						sprite,
						getBakeFlags(quad_emitter, sprite),
						QuadPosBounds.read(quad_emitter),
						quad.isTinted())
					);
					return pairs;
				});
			});
		});

		return new Appearance(sprites, model.useAmbientOcclusion());
	}

	private static int getBakeFlags(QuadEmitter emitter, TextureAtlasSprite sprite) {
		boolean[][] order_matrix = getOrderMatrix(emitter, sprite);
		int flag = 0;
		if (!isClockwise(order_matrix)) { // check if quad has been mirrored on model
			// check which mirroring is more efficient in terms of rotations
			int rotation_u = getRotation(flipOrderMatrix(order_matrix, MutableQuadView.BAKE_FLIP_U));
			int rotation_v = getRotation(flipOrderMatrix(order_matrix, MutableQuadView.BAKE_FLIP_V));
			if (rotation_u < rotation_v) flag = MutableQuadView.BAKE_FLIP_U | rotation_u;
			else flag = MutableQuadView.BAKE_FLIP_V | rotation_v;
		} else flag |= getRotation(order_matrix);
		return flag;
	}

	private static int getRotation(boolean[][] order_matrix) {
		int rotations = MutableQuadView.BAKE_ROTATE_NONE;
		rotations |= order_matrix[0][0] && !order_matrix[0][1] ? MutableQuadView.BAKE_ROTATE_90 : 0;
		rotations |= !order_matrix[0][0] && !order_matrix[0][1] ? MutableQuadView.BAKE_ROTATE_180 : 0;
		rotations |= !order_matrix[0][0] && order_matrix[0][1] ? MutableQuadView.BAKE_ROTATE_270 : 0;
		return rotations;
	}

	private static boolean isClockwise(boolean[][] rotation_matrix) {
		for (int i = 1; i < rotation_matrix.length; i++) {
			if (rotation_matrix[i][0] != rotation_matrix[i-1][1] && rotation_matrix[i][0] != rotation_matrix[i][1])
				return false;
		}
		return true;
	}

	private static boolean[][] flipOrderMatrix(boolean[][] order_matrix, int flag) {
		boolean[][] new_matrix = new boolean[4][2];
		for (int i = 0; i < 4; i++) {
			new_matrix[i][0] = (flag == MutableQuadView.BAKE_FLIP_U) != order_matrix[i][0];
			new_matrix[i][1] = (flag == MutableQuadView.BAKE_FLIP_V) != order_matrix[i][1];
		}
		return new_matrix;
	}

	private static boolean[][] getOrderMatrix(QuadEmitter emitter, TextureAtlasSprite sprite) {
		float minU = Math.min(sprite.getU0(), sprite.getU1());
		float maxU = Math.max(sprite.getU0(), sprite.getU1());
		float minV = Math.min(sprite.getV0(), sprite.getV1());
		float maxV = Math.max(sprite.getV0(), sprite.getV1());
		float u_center = (minU + maxU) / 2;
		float v_center = (minV + maxV) / 2;
		boolean[][] order_matrix = new boolean[4][2];
		for (int i = 0; i < 4; i++) {
			order_matrix[i][0] = emitter.u(i) < u_center;
			order_matrix[i][1] = emitter.v(i) < v_center;
		}
		return order_matrix;
	}
}
