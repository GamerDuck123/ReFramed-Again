package fr.adrien1106.reframed.mixin.render;

import fr.adrien1106.reframed.block.ReFramedBlock;
import fr.adrien1106.reframed.util.blocks.ThemeableBlockEntity;
import net.minecraft.block.*;
import net.minecraft.client.render.block.FluidRenderer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FluidRenderer.class)
public abstract class FluidRendererMixin {

    @Inject(
        method = "isSideCovered(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/Direction;FLnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)Z",
        at = @At("HEAD"),
        cancellable = true
    ) // force dynamic water side rendering
    private static void isSameSideCovered(BlockView world, Direction direction, float height, BlockPos pos, BlockState state, CallbackInfoReturnable<Boolean> cir) {
        if (!(state.getBlock() instanceof ReFramedBlock block)
        ) return;

        boolean is_covered = VoxelShapes.isSideCovered(
            VoxelShapes.cuboid(0.0, 0.0, 0.0, 1.0, height, 1.0),
            block.getShadingShape(state, world, pos),
            direction
        );
        cir.setReturnValue(is_covered);
    }

    @Redirect(
        method = "render",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/BlockRenderView;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;",
            ordinal = 7
        )
    )
    private BlockState getThemeState(BlockRenderView world, BlockPos pos) {
        if (!(world.getBlockEntity(pos) instanceof ThemeableBlockEntity framed_entity)) return world.getBlockState(pos);
        return framed_entity.getThemes().stream()
            .anyMatch(state -> state.getBlock() instanceof LeavesBlock
                || state.getBlock() instanceof TranslucentBlock
                || state.getBlock() instanceof AirBlock
            )
                ? Blocks.GLASS.getDefaultState()
                : world.getBlockState(pos) ;
    }
}
