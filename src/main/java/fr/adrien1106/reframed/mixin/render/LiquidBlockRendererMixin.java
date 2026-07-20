package fr.adrien1106.reframed.mixin.render;

import fr.adrien1106.reframed.block.ReFramedBlock;
import fr.adrien1106.reframed.util.blocks.ThemeableBlockEntity;
import net.minecraft.client.renderer.block.LiquidBlockRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HalfTransparentBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LiquidBlockRenderer.class)
public abstract class LiquidBlockRendererMixin {

    @Inject(
        method = "isFaceOccludedByState(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/Direction;FLnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z",
        at = @At("HEAD"),
        cancellable = true
    ) // force dynamic water side rendering
    private static void isSameSideCovered(BlockGetter world, Direction direction, float height, BlockPos pos, BlockState state, CallbackInfoReturnable<Boolean> cir) {
        if (!(state.getBlock() instanceof ReFramedBlock block)
        ) return;

        boolean is_covered = Shapes.blockOccudes(
            Shapes.box(0.0, 0.0, 0.0, 1.0, height, 1.0),
            block.getShadingShape(state, world, pos),
            direction
        );
        cir.setReturnValue(is_covered);
    }

    @Redirect(
        method = "tesselate",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/BlockAndTintGetter;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;",
            ordinal = 7
        )
    )
    private BlockState getThemeState(BlockAndTintGetter world, BlockPos pos) {
        if (!(world.getBlockEntity(pos) instanceof ThemeableBlockEntity framed_entity)) return world.getBlockState(pos);
        return framed_entity.getThemes().stream()
            .anyMatch(state -> state.getBlock() instanceof LeavesBlock
                || state.getBlock() instanceof HalfTransparentBlock
                || state.getBlock() instanceof AirBlock
            )
                ? Blocks.GLASS.defaultBlockState()
                : world.getBlockState(pos) ;
    }
}
