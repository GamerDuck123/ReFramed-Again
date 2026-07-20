package fr.adrien1106.reframed.mixin.logic;

import fr.adrien1106.reframed.block.ReFramedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionResult;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockItem.class)
public class BlockItemMixin {

    @Unique private final ThreadLocal<BlockState> old_state = new ThreadLocal<>();
    @Unique private final ThreadLocal<BlockEntity> old_entity = new ThreadLocal<>();

    @Redirect(
        method = "place(Lnet/minecraft/world/item/context/BlockPlaceContext;)Lnet/minecraft/world/InteractionResult;",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/block/Block;setPlacedBy(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;)V"
        )
    )
    private void placeMoreInfo(Block block, Level world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        if (!(block instanceof ReFramedBlock frame_block)) {
            block.setPlacedBy(world, pos, state, placer, itemStack);
            return;
        }
        frame_block.onPlaced(world, pos, state, placer, itemStack, old_state.get(), old_entity.get());
    }

    @Inject(
        method = "place(Lnet/minecraft/world/item/context/BlockPlaceContext;)Lnet/minecraft/world/InteractionResult;",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/item/BlockItem;placeBlock(Lnet/minecraft/world/item/context/BlockPlaceContext;Lnet/minecraft/world/level/block/state/BlockState;)Z",
            shift = At.Shift.BEFORE
        )
    )
    private void savePreviousInfo(BlockPlaceContext context, CallbackInfoReturnable<InteractionResult> cir) {
        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        old_state.set(world.getBlockState(pos));
        old_entity.set(world.getBlockEntity(pos));
    }

}
