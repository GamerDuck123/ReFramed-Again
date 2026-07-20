package fr.adrien1106.reframed.block;

import fr.adrien1106.reframed.ReFramed;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.BlockPos;

import java.util.List;
import java.util.Objects;

public class ReFramedDoubleEntity extends ReFramedEntity {

    protected BlockState second_state = Blocks.AIR.defaultBlockState();

    public ReFramedDoubleEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public BlockState getTheme(int i) {
        return i == 2 ? second_state : super.getTheme(i);
    }

    @Override
    public List<BlockState> getThemes() {
        List<BlockState> themes = super.getThemes();
        themes.add(second_state);
        return themes;
    }

    public void setTheme(BlockState new_state, int i) {
        if(i == 2) {
            if (Objects.equals(second_state, new_state)) return;
            second_state = new_state;
            markDirtyAndDispatch();
        } else super.setTheme(new_state, i);
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);

        BlockState rendered_state = second_state;// keep previous state_key to check if rerender is needed
        second_state = NbtUtils.readBlockState(BuiltInRegistries.BLOCK.asLookup(), nbt.getCompound(BLOCKSTATE_KEY + 2));

        // Force a chunk remesh on the client if the displayed blockstate has changed
        if(level != null && level.isClientSide && !Objects.equals(rendered_state, second_state)) {
            ReFramed.chunkRerenderProxy.accept(level, worldPosition);
        }
    }

    @Override
    public void saveAdditional(CompoundTag nbt) {
        super.saveAdditional(nbt);

        nbt.put(BLOCKSTATE_KEY + 2, NbtUtils.writeBlockState(second_state));
    }
}
