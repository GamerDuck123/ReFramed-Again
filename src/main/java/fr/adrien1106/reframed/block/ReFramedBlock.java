package fr.adrien1106.reframed.block;

import fr.adrien1106.reframed.ReFramed;
import fr.adrien1106.reframed.util.blocks.BlockHelper;
import fr.adrien1106.reframed.util.blocks.ThemeableBlockEntity;
import net.minecraft.block.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.Containers;
import net.minecraft.core.NonNullList;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static fr.adrien1106.reframed.block.ReFramedEntity.BLOCKSTATE_KEY;
import static fr.adrien1106.reframed.util.blocks.BlockProperties.LIGHT;

public class ReFramedBlock extends Block implements EntityBlock {

	public ReFramedBlock(Properties settings) {
		super(settings.dynamicShape());
		registerDefaultState(defaultBlockState().setValue(LIGHT, false));
	}

	@Override
	@SuppressWarnings("deprecation")
	public int getLightBlock(BlockState state, BlockGetter world, BlockPos pos) {
		if (state.getValue(LIGHT)) return 0;
		if (!(world.getBlockEntity(pos) instanceof ReFramedEntity frame_entity)
			|| frame_entity.getTheme(0).canOcclude())
			return world.getMaxLightLevel();
		return 0;
	}

	@Override
	@SuppressWarnings("deprecation")
	public boolean isPathfindable(BlockState state, BlockGetter world, BlockPos pos, PathComputationType type) {
		return false;
	}

	@Override
	public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return ReFramed.REFRAMED_BLOCK_ENTITY.create(pos, state);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder.add(LIGHT));
	}
	
	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext ctx) {
		return ReFramedEntity.getNbtLightLevel(super.getStateForPlacement(ctx), ctx.getItemInHand());
	}
	
	@Override
    @SuppressWarnings("deprecation")
	public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		if (!canUse(world, pos, player)) return InteractionResult.PASS;
		InteractionResult result = BlockHelper.useUpgrade(state, world, pos, player, hand);
		if (result.consumesAction()) return result;
		return BlockHelper.useCamo(state, world, pos, player, hand, hit, 1);

	}

	protected boolean canUse(Level world, BlockPos pos, Player player) {
		return player.mayBuild() && world.mayInteract(player, pos);
	}
	
	@Override
    @SuppressWarnings("deprecation")
	public void onRemove(BlockState state, Level world, BlockPos pos, BlockState new_state, boolean moved) {
        if (!new_state.is(state.getBlock())) world.removeBlockEntity(pos);

		if(!(new_state.getBlock() instanceof ReFramedBlock) &&
			world.getBlockEntity(pos) instanceof ReFramedEntity frame_entity &&
			world.getGameRules().getBoolean(GameRules.RULE_DOBLOCKDROPS)
		) {
			NonNullList<ItemStack> drops = NonNullList.create();

			List<BlockState> themes = frame_entity.getThemes();
			themes.forEach(theme -> {
				if(theme.getBlock() != Blocks.AIR) drops.add(new ItemStack(theme.getBlock()));
			});

			Containers.dropContents(world, pos, drops);
		}
		super.onRemove(state, world, pos, new_state, moved);
	}

	public void onPlaced(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack, BlockState old_state, BlockEntity old_entity) {
		if (!(world.getBlockEntity(pos) instanceof ReFramedEntity frame_entity)) {
			setPlacedBy(world, pos, state, placer, stack);
			return;
		}

		// apply state change keeping the old information
		if (old_state.getBlock() instanceof ReFramedBlock old_frame_block
			&& old_entity instanceof ReFramedEntity old_frame_entity) {
			Map<Integer, Integer> theme_map = old_frame_block.getThemeMap(old_state, state);
			theme_map.forEach((self, other) ->
				frame_entity.setTheme(old_frame_entity.getTheme(self), other)
			);

			// apply any changes needed to keep previous properties
			if (old_frame_entity.emitsLight() && !frame_entity.emitsLight()) {
				frame_entity.toggleLight();
				world.setBlockAndUpdate(pos, state.setValue(LIGHT, true));
			}
			if (old_frame_entity.emitsRedstone() && !frame_entity.emitsRedstone()) {
				frame_entity.toggleRedstone();
				world.blockUpdated(pos, this);
			}
			if (old_frame_entity.isSolid() && !frame_entity.isSolid()) frame_entity.toggleSolidity();

			// apply themes from item
			CompoundTag tag = BlockItem.getBlockEntityData(stack);
			if(tag != null) {
				// determine a list of themes than can be used
				Iterator<Integer> free_themes = IntStream
					.rangeClosed(1, frame_entity.getThemes().size())
					.filter(value -> !theme_map.containsValue(value))
					.iterator();
				// apply all the themes possible from item
				for (int i = 1; tag.contains(BLOCKSTATE_KEY + i) && free_themes.hasNext(); i++) {
					BlockState theme = NbtUtils.readBlockState(BuiltInRegistries.BLOCK.asLookup(), tag.getCompound(BLOCKSTATE_KEY + i));
					if (theme == null || theme.getBlock() == Blocks.AIR) continue;
					frame_entity.setTheme(theme, free_themes.next());
				}
			}
		} else if(world.isClientSide) { // prevents flashing with default texture before server sends the update
			CompoundTag tag = BlockItem.getBlockEntityData(stack);
			if(tag != null) frame_entity.load(tag);
		}
		setPlacedBy(world, pos, state, placer, stack);
	}

    public boolean matchesShape(Vec3 hit, BlockPos pos, BlockState state) {
        return matchesShape(hit, pos, state, 0);
    }

    public boolean matchesShape(Vec3 hit, BlockPos pos, BlockState state, int i) {
        Vec3 rel = BlockHelper.getRelativePos(hit, pos);
        return matchesShape(rel, getShape(state, i));
    }

    public boolean matchesShape(Vec3 rel_hit, VoxelShape shape) {
        return BlockHelper.cursorMatchesFace(shape, rel_hit);
    }
	
	@Override
    @SuppressWarnings("deprecation")
	public VoxelShape getCollisionShape(BlockState state, BlockGetter view, BlockPos pos, CollisionContext ctx) {
		return isGhost(view, pos)
			? Shapes.empty()
			: super.getCollisionShape(state, view, pos, ctx);
	}

	@Override
    @SuppressWarnings("deprecation")
	public VoxelShape getOcclusionShape(BlockState state, BlockGetter view, BlockPos pos) {
		return isGhost(view, pos)
			? Shapes.empty()
			: super.getOcclusionShape(state, view, pos);
	}

    @SuppressWarnings("deprecation")
	public VoxelShape getShape(BlockState state, int i) {
		// assuming the shape don't need the world and position
		return getShape(state, null, null, null);
	}

	public boolean isGhost(BlockGetter view, BlockPos pos) {
		return view.getBlockEntity(pos) instanceof ReFramedEntity be && !be.isSolid();
	}
	
	@Override
    @SuppressWarnings("deprecation")
	public int getSignal(BlockState state, BlockGetter view, BlockPos pos, Direction dir) {
		return view.getBlockEntity(pos) instanceof ReFramedEntity be && be.emitsRedstone() ? 15 : 0;
	}
	
	@Override
    @SuppressWarnings("deprecation")
	public int getDirectSignal(BlockState state, BlockGetter view, BlockPos pos, Direction dir) {
		return getSignal(state, view, pos, dir);
	}

	/**
	 * @param state - the block state to get the top theme index from
	 * @return the index of the top theme to use for the block
	 */
	public int getTopThemeIndex(BlockState state) {
		return 1;
	}

	/**
	 * @param state     - the block state of the block that is being replaced
	 * @param new_state - the block state of the block that is replacing the block
	 * @return a map of the theme indexes to map when changing state so that the themes are preserved
	 */
	public Map<Integer, Integer> getThemeMap(BlockState state, BlockState new_state) {
		return Map.of();
	}

	public VoxelShape getShadingShape(BlockState state, BlockGetter world, BlockPos pos) {
		if (!(world.getBlockEntity(pos) instanceof ThemeableBlockEntity framed_entity)) return this.getCollisionShape(state, world, pos, CollisionContext.empty());

		AtomicInteger i = new AtomicInteger(1);
		return framed_entity.getThemes().stream().map((theme) -> {
			int index = i.getAndIncrement();
			return theme.propagatesSkylightDown(world, pos) ? Shapes.empty() : this.getShape(state, index);
		}).reduce(
			Shapes.empty(),
			(prev, current) -> Shapes.joinUnoptimized(prev, current, BooleanOp.OR)
		);
	}
}
