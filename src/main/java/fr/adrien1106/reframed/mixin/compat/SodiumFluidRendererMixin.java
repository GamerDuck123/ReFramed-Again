package fr.adrien1106.reframed.mixin.compat;

import com.llamalad7.mixinextras.sugar.Local;
import fr.adrien1106.reframed.block.ReFramedBlock;
import fr.adrien1106.reframed.util.blocks.ThemeableBlockEntity;
import me.jellysquid.mods.sodium.client.render.chunk.compile.pipeline.FluidRenderer;
import me.jellysquid.mods.sodium.client.world.WorldSlice;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.minecraft.block.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(FluidRenderer.class)
public abstract class SodiumFluidRendererMixin {

    @Redirect(
        method = "isSideExposed",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/block/BlockState;isOpaque()Z"
        )
    )
    private boolean isSideOpaqueExposed(BlockState state) {
        if (!(state.getBlock() instanceof ReFramedBlock)) return state.isOpaque();
        return true; // forces to compute correct shape
    }

    @Redirect(
        method = "isSideExposed",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/block/BlockState;getCullingShape(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/util/shape/VoxelShape;"
        )
    )
    private VoxelShape isSideShapeExposed(BlockState state, BlockView world, BlockPos pos) {
        if (!(state.getBlock() instanceof ReFramedBlock block)) return state.getCullingShape(world, pos);
        return block.getShadingShape(state, world, pos);
    }

    @Redirect(
        method = "render",
        at = @At(
            value = "INVOKE",
            target = "Lnet/fabricmc/fabric/api/client/render/fluid/v1/FluidRenderHandlerRegistry;isBlockTransparent(Lnet/minecraft/block/Block;)Z"
        )
    )
    private boolean getThemeState(FluidRenderHandlerRegistry fluid_handler, Block block, @Local(argsOnly = true) WorldSlice world, @Local(ordinal = 2) BlockPos pos, @Local BlockState state, @Local Direction dir) {
        if (!(block instanceof ReFramedBlock rfblock && world.getBlockEntity(pos) instanceof ThemeableBlockEntity framed_entity)) return fluid_handler.isBlockTransparent(block);
        return !VoxelShapes.isSideCovered(VoxelShapes.fullCube(), rfblock.getShadingShape(state, world, pos), dir)
            && framed_entity.getThemes().stream()
                .anyMatch(s -> s.getBlock() instanceof LeavesBlock
                    || s.getBlock() instanceof TranslucentBlock
                    || s.getBlock() instanceof AirBlock
                );
    }
}
