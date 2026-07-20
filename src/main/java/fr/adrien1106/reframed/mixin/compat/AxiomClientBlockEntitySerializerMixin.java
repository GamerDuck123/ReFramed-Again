package fr.adrien1106.reframed.mixin.compat;

import com.moulberry.axiom.world_modification.ClientBlockEntitySerializer;
import fr.adrien1106.reframed.util.blocks.ThemeableBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.HolderLookup;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientBlockEntitySerializer.class)
public class AxiomClientBlockEntitySerializerMixin {

    @Inject(
        method = "serialize",
        at = @At("HEAD"),
        remap = false,
        cancellable = true
    )
    private static void serialize(BlockEntity blockEntity, HolderLookup.Provider provider, CallbackInfoReturnable<CompoundTag> cir) {
        if (!(blockEntity instanceof ThemeableBlockEntity)) return;
        cir.setReturnValue(blockEntity.saveWithoutMetadata());
    }
}
