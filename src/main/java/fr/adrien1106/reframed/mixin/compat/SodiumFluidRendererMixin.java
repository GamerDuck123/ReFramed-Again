package fr.adrien1106.reframed.mixin.compat;

import com.llamalad7.mixinextras.sugar.Local;
import fr.adrien1106.reframed.block.ReFramedBlock;
import fr.adrien1106.reframed.util.blocks.ThemeableBlockEntity;
import me.jellysquid.mods.sodium.client.render.chunk.compile.pipeline.FluidRenderer;
import me.jellysquid.mods.sodium.client.world.WorldSlice;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HalfTransparentBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.level.BlockGetter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(FluidRenderer.class)
public abstract class SodiumFluidRendererMixin {

    @Redirect(
        method = "isSideExposed",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/block/state/BlockState;canOcclude()Z"
        )
    )
    private boolean isSideOpaqueExposed(BlockState state) {
        if (!(state.getBlock() instanceof ReFramedBlock)) return state.canOcclude();
        return true; // forces to compute correct shape
    }

    @Redirect(
        method = "isSideExposed",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/block/state/BlockState;getOcclusionShape(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/phys/shapes/VoxelShape;"
        )
    )
    private VoxelShape isSideShapeExposed(BlockState state, BlockGetter world, BlockPos pos) {
        if (!(state.getBlock() instanceof ReFramedBlock block)) return state.getOcclusionShape(world, pos);
        return block.getShadingShape(state, world, pos);
    }

    @Redirect(
        method = "render",
        at = @At(
            value = "INVOKE",
            target = "Lnet/fabricmc/fabric/api/client/render/fluid/v1/FluidRenderHandlerRegistry;isBlockTransparent(Lnet/minecraft/world/level/block/Block;)Z"
        )
    )
    private boolean getThemeState(FluidRenderHandlerRegistry fluid_handler, Block block, @Local(argsOnly = true) WorldSlice world, @Local(ordinal = 2) BlockPos pos, @Local BlockState state, @Local Direction dir) {
        if (!(block instanceof ReFramedBlock rfblock && world.getBlockEntity(pos) instanceof ThemeableBlockEntity framed_entity)) return fluid_handler.isBlockTransparent(block);
        return !Shapes.blockOccudes(Shapes.block(), rfblock.getShadingShape(state, world, pos), dir)
            && framed_entity.getThemes().stream()
                .anyMatch(s -> s.getBlock() instanceof LeavesBlock
                    || s.getBlock() instanceof HalfTransparentBlock
                    || s.getBlock() instanceof AirBlock
                );
    }
}
