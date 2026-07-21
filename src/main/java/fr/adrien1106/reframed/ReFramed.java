package fr.adrien1106.reframed;

import fr.adrien1106.reframed.block.*;
import fr.adrien1106.reframed.item.ReFramedHammerItem;
import fr.adrien1106.reframed.item.ReFramedBlueprintItem;
import fr.adrien1106.reframed.item.ReFramedBlueprintWrittenItem;
import fr.adrien1106.reframed.item.ReFramedScrewdriverItem;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.Registry;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

import static fr.adrien1106.reframed.util.blocks.BlockProperties.LIGHT;

/**
 * TODO Dynamic Ambient Occlusion                                            -> not scheduled
 * TODO better connected textures                                            -> not scheduled
 * TODO support continuity overlays                                          -> not scheduled
 * TODO slopes                                                               -> thinking about it
 */
public class ReFramed implements ModInitializer {
	public static final String MODID = "reframed";

	public static final ArrayList<Block> BLOCKS = new ArrayList<>();
	public static ReFramedBlock
        CUBE,
        SMALL_CUBE, SMALL_CUBES_STEP,
        STAIR, STAIRS_CUBE,
        HALF_STAIR, HALF_STAIRS_SLAB, HALF_STAIRS_STAIR, HALF_STAIRS_CUBE_STAIR, HALF_STAIRS_STEP_STAIR,
        SLAB, SLABS_CUBE, SLABS_STAIR, SLABS_OUTER_STAIR, SLABS_INNER_STAIR, SLABS_HALF_LAYER, SLABS_LAYER,
        HALF_SLAB, HALF_SLABS_SLAB,
        STEP, STEPS_SLAB, STEPS_CROSS, STEPS_HALF_LAYER,
        LAYER, HALF_LAYER,
        PILLAR, PILLARS_WALL, WALL,
        PANE, TRAPDOOR, DOOR,
        BUTTON,
        POST, POST_FENCE, FENCE;

	public static final ArrayList<Item> ITEMS = new ArrayList<>();
	public static Item HAMMER, SCREWDRIVER, BLUEPRINT, BLUEPRINT_WRITTEN;

	public static CreativeModeTab ITEM_GROUP;

	public static BlockEntityType<ReFramedEntity> REFRAMED_BLOCK_ENTITY;
	public static BlockEntityType<ReFramedDoubleEntity> REFRAMED_DOUBLE_BLOCK_ENTITY;

	public static BiConsumer<Level, BlockPos> chunkRerenderProxy = (world, pos) -> {};
	
	@Override
	public void onInitialize() {
		CUBE                    = registerBlock("cube"                   , new ReFramedBlock(cp(Blocks.OAK_PLANKS)));
		SMALL_CUBE              = registerBlock("small_cube"             , new ReFramedSmallCubeBlock(cp(Blocks.OAK_PLANKS)));
	  	SMALL_CUBES_STEP        = registerBlock("small_cubes_step"       , new ReFramedSmallCubesStepBlock(cp(Blocks.OAK_PLANKS)));
		STAIR                   = registerBlock("stair"                  , new ReFramedStairBlock(cp(Blocks.OAK_STAIRS)));
		STAIRS_CUBE             = registerBlock("stairs_cube"            , new ReFramedStairsCubeBlock(cp(Blocks.OAK_STAIRS)));
		HALF_STAIR              = registerBlock("half_stair"             , new ReFramedHalfStairBlock(cp(Blocks.OAK_STAIRS)));
		HALF_STAIRS_SLAB        = registerBlock("half_stairs_slab"       , new ReFramedHalfStairsSlabBlock(cp(Blocks.OAK_STAIRS)));
		HALF_STAIRS_STAIR       = registerBlock("half_stairs_stair"      , new ReFramedHalfStairsStairBlock(cp(Blocks.OAK_STAIRS)));
        HALF_STAIRS_CUBE_STAIR  = registerBlock("half_stairs_cube_stair" , new ReFramedHalfStairsCubeStairBlock(cp(Blocks.OAK_STAIRS)));
        HALF_STAIRS_STEP_STAIR  = registerBlock("half_stairs_step_stair" , new ReFramedHalfStairsStepStairBlock(cp(Blocks.OAK_STAIRS)));
		LAYER                   = registerBlock("layer"                  , new ReFramedLayerBlock(cp(Blocks.OAK_SLAB)));
        HALF_LAYER              = registerBlock("half_layer"             , new ReFramedHalfLayerBlock(cp(Blocks.OAK_SLAB)));
		SLAB                    = registerBlock("slab"                   , new ReFramedSlabBlock(cp(Blocks.OAK_SLAB)));
		SLABS_CUBE              = registerBlock("slabs_cube"             , new ReFramedSlabsCubeBlock(cp(Blocks.OAK_SLAB)));
        SLABS_STAIR             = registerBlock("slabs_stair"            , new ReFramedSlabsStairBlock(cp(Blocks.OAK_STAIRS)));
        SLABS_OUTER_STAIR       = registerBlock("slabs_outer_stair"      , new ReFramedSlabsOuterStairBlock(cp(Blocks.OAK_STAIRS)));
        SLABS_INNER_STAIR       = registerBlock("slabs_inner_stair"      , new ReFramedSlabsInnerStairBlock(cp(Blocks.OAK_STAIRS)));
        SLABS_HALF_LAYER        = registerBlock("slabs_half_layer"       , new ReFramedSlabsHalfLayerBlock(cp(Blocks.OAK_SLAB)));
        SLABS_LAYER             = registerBlock("slabs_layer"            , new ReFramedSlabsLayerBlock(cp(Blocks.OAK_SLAB)));
        HALF_SLAB               = registerBlock("half_slab"              , new ReFramedHalfSlabBlock(cp(Blocks.OAK_SLAB)));
        HALF_SLABS_SLAB         = registerBlock("half_slabs_slab"        , new ReFramedHalfSlabsSlabBlock(cp(Blocks.OAK_SLAB)));
		STEP                    = registerBlock("step"                   , new ReFramedStepBlock(cp(Blocks.OAK_SLAB)));
		STEPS_SLAB              = registerBlock("steps_slab"             , new ReFramedStepsSlabBlock(cp(Blocks.OAK_SLAB)));
        STEPS_CROSS             = registerBlock("steps_cross"            , new ReFramedStepsCrossBlock(cp(Blocks.OAK_SLAB)));
        STEPS_HALF_LAYER        = registerBlock("steps_half_layer"       , new ReFramedStepsHalfLayerBlock(cp(Blocks.OAK_SLAB)));
		PILLAR                  = registerBlock("pillar"                 , new ReFramedPillarBlock(cp(Blocks.OAK_FENCE)));
		PILLARS_WALL            = registerBlock("pillars_wall"           , new ReFramedPillarsWallBlock(cp(Blocks.OAK_FENCE)));
		WALL                    = registerBlock("wall"                   , new ReFramedWallBlock(cp(Blocks.OAK_FENCE)));
        PANE                    = registerBlock("pane"                   , new ReFramedPaneBlock(cp(Blocks.OAK_FENCE)));
        TRAPDOOR                = registerBlock("trapdoor"               , new ReFramedTrapdoorBlock(cp(Blocks.OAK_TRAPDOOR)));
        DOOR                    = registerBlock("door"                   , new ReFramedDoorBlock(cp(Blocks.OAK_DOOR)));
        BUTTON                  = registerBlock("button"                 , new ReFramedButtonBlock(cp(Blocks.OAK_BUTTON)));
        POST                    = registerBlock("post"                   , new ReFramedPostBlock(cp(Blocks.OAK_FENCE)));
        FENCE                   = registerBlock("fence"                  , new ReFramedFenceBlock(cp(Blocks.OAK_FENCE)));
        POST_FENCE              = registerBlock("post_fence"             , new ReFramedPostFenceBlock(cp(Blocks.OAK_FENCE)));

		HAMMER                  = registerItem("hammer"                  , new ReFramedHammerItem(new Item.Properties().stacksTo(1)));
		SCREWDRIVER             = registerItem("screwdriver"             , new ReFramedScrewdriverItem(new Item.Properties().stacksTo(1)));
		BLUEPRINT               = registerItem("blueprint"               , new ReFramedBlueprintItem(new Item.Properties()));
		BLUEPRINT_WRITTEN       = registerItem("blueprint_written"       , new ReFramedBlueprintWrittenItem(new Item.Properties().stacksTo(1)));


		REFRAMED_BLOCK_ENTITY = Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, id("camo"),
            BlockEntityType.Builder.of(
				(pos, state) -> new ReFramedEntity(REFRAMED_BLOCK_ENTITY, pos, state),
				BLOCKS.stream()
					.filter(block -> !(block instanceof ReFramedDoubleBlock))
					.toArray(Block[]::new)).build(null)
		);

		REFRAMED_DOUBLE_BLOCK_ENTITY = Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, id("double_camo"),
			BlockEntityType.Builder.of(
				(pos, state) -> new ReFramedDoubleEntity(REFRAMED_DOUBLE_BLOCK_ENTITY, pos, state),
				BLOCKS.stream()
					.filter(block -> block instanceof ReFramedDoubleBlock)
					.toArray(Block[]::new)).build(null)
		);

		ITEM_GROUP = Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, id("tab"), FabricItemGroup.builder()
			.title(Component.translatable("itemGroup.reframed.tab"))
			.icon(() -> new ItemStack(SLAB))
			.displayItems((ctx, e) -> e.acceptAll(
				Stream.concat(
					ITEMS.stream().filter(item -> item != BLUEPRINT_WRITTEN),
					BLOCKS.stream().map(Block::asItem)
				).map(Item::getDefaultInstance).toList())
			).build()
		);
	}

	private static net.minecraft.world.level.block.state.BlockBehaviour.Properties cp(Block base) {
		return net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(base)
			.lightLevel(state -> state.hasProperty(LIGHT) && state.getValue(LIGHT) ? 15 : 0)
			.sound(SoundType.WOOD)
			.destroyTime(0.2f)
			.isSuffocating(Blocks::never)
			.isRedstoneConductor(Blocks::always);
//			.blockVision(Blocks::always);
	}

	private static <I extends Item> I registerItem(String path, I item) {
		ResourceLocation id = id(path);
		Registry.register(BuiltInRegistries.ITEM, id, item);
		ITEMS.add(item);
		return item;
	}
	
	private static <B extends Block> B registerBlock(String path, B block) {
		ResourceLocation id = id(path);
		
		Registry.register(BuiltInRegistries.BLOCK, id, block);
		Registry.register(BuiltInRegistries.ITEM, id, new BlockItem(block, new Item.Properties()));
		BLOCKS.add(block);
		return block;
	}

	public static ResourceLocation id(String path) {
		return new ResourceLocation(MODID, path);
	}
}
