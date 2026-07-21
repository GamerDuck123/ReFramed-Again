// TODO(Ravel): Failed to fully resolve file: class com.intellij.psi.impl.source.tree.java.PsiPolyadicExpressionImpl cannot be cast to class com.intellij.psi.PsiLiteralExpression (com.intellij.psi.impl.source.tree.java.PsiPolyadicExpressionImpl and com.intellij.psi.PsiLiteralExpression are in unnamed module of loader com.intellij.ide.plugins.cl.PluginClassLoader @3aedee1f)
// TODO(Ravel): Failed to fully resolve file: class com.intellij.psi.impl.source.tree.java.PsiPolyadicExpressionImpl cannot be cast to class com.intellij.psi.PsiLiteralExpression (com.intellij.psi.impl.source.tree.java.PsiPolyadicExpressionImpl and com.intellij.psi.PsiLiteralExpression are in unnamed module of loader com.intellij.ide.plugins.cl.PluginClassLoader @3aedee1f)
// TODO(Ravel): Failed to fully resolve file: class com.intellij.psi.impl.source.tree.java.PsiPolyadicExpressionImpl cannot be cast to class com.intellij.psi.PsiLiteralExpression (com.intellij.psi.impl.source.tree.java.PsiPolyadicExpressionImpl and com.intellij.psi.PsiLiteralExpression are in unnamed module of loader com.intellij.ide.plugins.cl.PluginClassLoader @3aedee1f)
package fr.adrien1106.reframed.mixin.render;

import fr.adrien1106.reframed.block.ReFramedDoubleBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.BlockGetter;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {

    @Shadow @Final private Minecraft minecraft;

    @Redirect(method = "renderHitOutline",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getShape(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/phys/shapes/CollisionContext;)Lnet/minecraft/world/phys/shapes/VoxelShape;"))
    private VoxelShape getRenderOutline(BlockState state, BlockGetter world, BlockPos pos, CollisionContext shape_context) {
        if (state.getBlock() instanceof ReFramedDoubleBlock double_frame_block) // cast is already checked in render
            return double_frame_block.getRenderOutline(state, (BlockHitResult) minecraft.hitResult);
        return state.getShape(world, pos, shape_context);
    }
}
