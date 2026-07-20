package fr.adrien1106.reframed.block;

import fr.adrien1106.reframed.ReFramed;
import fr.adrien1106.reframed.util.blocks.BlockProperties;
import fr.adrien1106.reframed.util.blocks.ThemeableBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

//Keeping the weight of this block entity down, both in terms of memory consumption and NBT sync traffic,
//is pretty important since players might place a lot of them. There were tons and tons of these at Blanketcon.
//To that end, most of the state_key has been crammed into a bitfield.
public class ReFramedEntity extends BlockEntity implements ThemeableBlockEntity {
	protected BlockState first_state = Blocks.AIR.defaultBlockState();
	protected byte bit_field = SOLIDITY_MASK;
	
	public static final byte LIGHT_MASK    = 0b001;
	public static final byte REDSTONE_MASK = 0b010;
	public static final byte SOLIDITY_MASK = 0b100;

	public static final String BLOCKSTATE_KEY = "s";
	public static final String BITFIELD_KEY = "b";
	
	public ReFramedEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}
	
	@Override
	public void load(CompoundTag nbt) {
		super.load(nbt);
		
		BlockState rendered_state = first_state; // keep previous state_key to check if rerender is needed
		first_state = NbtUtils.readBlockState(BuiltInRegistries.BLOCK.asLookup(), nbt.getCompound(BLOCKSTATE_KEY + 1));
		if (nbt.contains(BITFIELD_KEY)) bit_field = nbt.getByte(BITFIELD_KEY);
		
		// Force a chunk remesh on the client if the displayed blockstate has changed
		if(level != null && level.isClientSide && !Objects.equals(rendered_state, first_state))
			ReFramed.chunkRerenderProxy.accept(level, worldPosition);
	}
	
	@Override
	public void saveAdditional(CompoundTag nbt) {
		super.saveAdditional(nbt);

		nbt.put(BLOCKSTATE_KEY + 1, NbtUtils.writeBlockState(first_state));
		if(bit_field != SOLIDITY_MASK) nbt.putByte(BITFIELD_KEY, bit_field);
	}

	public static @NotNull BlockState readStateFromItem(ItemStack stack, int state) {
		CompoundTag nbt = BlockItem.getBlockEntityData(stack);
		if(nbt == null) return Blocks.AIR.defaultBlockState();
		
		//slightly paranoid NBT handling cause you never know what mysteries are afoot with items
		Tag element;
		if(nbt.contains(BLOCKSTATE_KEY + state)) element = nbt.get(BLOCKSTATE_KEY + state);
		else return Blocks.AIR.defaultBlockState();
		
		if(!(element instanceof CompoundTag compound)) return Blocks.AIR.defaultBlockState();
		else return NbtUtils.readBlockState(BuiltInRegistries.BLOCK.asLookup(), compound);
	}
	
	//Awkward: usually the BlockState is the source of truth for things like the "emits light" blockstate, but if you
	//ctrl-pick a glowing block and place it, it should still be glowing. This is some hacky shit that guesses the value of
	//the LIGHT blockstate based off information in the NBT tag, and also prevents bugginess like "the blockstate is not
	//glowing but the copied NBT thinks glowstone dust was already added, so it refuses to accept more dust"
	public static @Nullable BlockState getNbtLightLevel(@Nullable BlockState state, ItemStack stack) {
		if(state == null || stack == null) return state;
		
		CompoundTag nbt = BlockItem.getBlockEntityData(stack);
		if(nbt == null) return state;
		
		if(state.hasProperty(BlockProperties.LIGHT)) {
			state = state.setValue(BlockProperties.LIGHT,
				((nbt.contains(BITFIELD_KEY)
					? nbt.getByte(BITFIELD_KEY)
					: SOLIDITY_MASK)
					& LIGHT_MASK) != 0
			);
		}
		
		return state;
	}

	@Override
	public BlockState getTheme(int i) {
		return first_state;
	}

	@Override
	public List<BlockState> getThemes() {
		List<BlockState> themes = new ArrayList<>();
		themes.add(first_state);
		return themes;
	}

	public void setTheme(BlockState new_state, int i) {
		if(!Objects.equals(first_state, new_state) && i == 1) {
			first_state = new_state;
			markDirtyAndDispatch();
		}
	}

	/* --------------------------------------------------- ADDONS --------------------------------------------------- */
	public boolean emitsLight() {
		return (bit_field & LIGHT_MASK) != 0;
	}
	
	public void toggleLight() {
		if (emitsLight()) bit_field &= ~LIGHT_MASK;
		else bit_field |= LIGHT_MASK;
		markDirtyAndDispatch();
	}
	
	public void toggleRedstone() {
		if (emitsRedstone()) bit_field &= ~REDSTONE_MASK;
		else bit_field |= REDSTONE_MASK;

		if(level != null) level.blockUpdated(worldPosition, getBlockState().getBlock());
		markDirtyAndDispatch();
	}

	public boolean emitsRedstone() {
		return (bit_field & REDSTONE_MASK) != 0;
	}
	
	public void toggleSolidity() {
		if (isSolid()) bit_field &= ~SOLIDITY_MASK;
		else bit_field |= SOLIDITY_MASK;

		if(level != null) {
			level.setBlockAndUpdate(worldPosition, getBlockState());
			ReFramed.chunkRerenderProxy.accept(level, worldPosition);
		}
		markDirtyAndDispatch();
	}
	
	public boolean isSolid() {
		return (bit_field & SOLIDITY_MASK) != 0;
	}

	@Nullable
	@Override
	public Packet<ClientGamePacketListener> getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}
	
	@Override
	public CompoundTag getUpdateTag() {
		return saveWithoutMetadata();
	}
	
	protected void dispatch() {
		if(level instanceof ServerLevel sworld) sworld.getChunkSource().blockChanged(worldPosition);
	}
	
	protected void markDirtyAndDispatch() {
		setChanged();
		dispatch();
	}
}
