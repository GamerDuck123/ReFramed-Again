package fr.adrien1106.reframed.util.mixin;

import fr.adrien1106.reframed.util.blocks.ThemeableBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.BlockPos;

import java.util.ArrayList;
import java.util.List;

import static fr.adrien1106.reframed.block.ReFramedEntity.*;

public class ThemedBlockEntity extends BlockEntity implements ThemeableBlockEntity {
    private final List<BlockState> themes;
    private final boolean isSolid;

    public ThemedBlockEntity(CompoundTag compound, BlockPos pos, BlockState state) {
        super(null, pos, state);
        themes = new ArrayList<>();
        for (int i = 1; compound.contains(BLOCKSTATE_KEY + i ); i++) {
            themes.add(NbtUtils.readBlockState(
                BuiltInRegistries.BLOCK.asLookup(),
                compound.getCompound(BLOCKSTATE_KEY + i)
            ));
        }
        isSolid = !compound.contains(BITFIELD_KEY) || (compound.getByte(BITFIELD_KEY) & SOLIDITY_MASK) != 0;
    }

    @Override
    public BlockState getTheme(int i) {
        if (i > themes.size())
            return Blocks.AIR.defaultBlockState();
        return themes.get(Math.max(0, i-1));
    }

    @Override
    public void setTheme(BlockState state, int i) {
        if (i > themes.size())
            themes.add(state);
        else
            themes.set(Math.max(0, i-1), state);
    }

    @Override
    public List<BlockState> getThemes() {
        return themes;
    }

    @Override
    public boolean isSolid() {
        return isSolid;
    }
}
