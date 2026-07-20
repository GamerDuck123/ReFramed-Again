package fr.adrien1106.reframed.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import fr.adrien1106.reframed.block.ReFramedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static fr.adrien1106.reframed.block.ReFramedEntity.BLOCKSTATE_KEY;

@Mixin(BlockItem.class)
public class BlockItemMixin {

    @Inject(
        method = "updateCustomBlockEntityTag(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/item/ItemStack;)Z",
        at = @At(
            value = "INVOKE_ASSIGN",
            target = "Lnet/minecraft/world/item/BlockItem;getBlockEntityData(Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/nbt/CompoundTag;",
            shift = At.Shift.AFTER
        )
    )
    private static void placeBlockWithOffHandCamo(Level world, Player player, BlockPos pos, ItemStack stack, CallbackInfoReturnable<Boolean> cir, @Local LocalRef<CompoundTag> compound) {
        if (player == null
            || compound.get() != null
            || player.getOffhandItem().isEmpty()
            || player.getMainHandItem().isEmpty()
            || !(player.getMainHandItem().getItem() instanceof BlockItem frame)
            || !(frame.getBlock() instanceof ReFramedBlock)
            || !(player.getOffhandItem().getItem() instanceof BlockItem block)
            || block.getBlock() instanceof EntityBlock
            || (world.getBlockState(pos).hasProperty(BlockStateProperties.LAYERS) && world.getBlockState(pos).getValue(BlockStateProperties.LAYERS) > 1)
            || !Block.isShapeFullBlock(block.getBlock().defaultBlockState().getCollisionShape(world, pos))
        ) return;
        CompoundTag new_comp = new CompoundTag();
        player.getOffhandItem().shrink(1);
        new_comp.put(BLOCKSTATE_KEY + 1, NbtUtils.writeBlockState(block.getBlock().defaultBlockState()));
        compound.set(new_comp);
    }

}
