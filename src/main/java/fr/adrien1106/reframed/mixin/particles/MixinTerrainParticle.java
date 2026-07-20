package fr.adrien1106.reframed.mixin.particles;

import fr.adrien1106.reframed.block.ReFramedBlock;
import fr.adrien1106.reframed.mixin.particles.AccessorTextureSheetParticle;
import fr.adrien1106.reframed.util.blocks.ThemeableBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.TerrainParticle;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TerrainParticle.class)
public class MixinTerrainParticle {
	@Inject(
		method = "<init>(Lnet/minecraft/client/multiplayer/ClientLevel;DDDDDDLnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;)V",
		at = @At("TAIL")
	)
	void modifyParticleSprite(ClientLevel clientWorld, double d, double e, double f, double g, double h, double i, BlockState state, BlockPos pos, CallbackInfo ci) {
		AccessorParticle a = (AccessorParticle) this;
		if(a.getRandom().nextBoolean()
			&& clientWorld.getBlockEntity(pos) instanceof ThemeableBlockEntity themeable
			&& state.getBlock() instanceof ReFramedBlock block
		) {
			BlockState theme = themeable.getTheme(block.getTopThemeIndex(state));
			if(theme == null || theme.isAir()) return;
			
			TextureAtlasSprite replacement = Minecraft.getInstance().getBlockRenderer().getBlockModelShaper().getParticleIcon(theme);
			((AccessorTextureSheetParticle) this).setNewSprite(replacement);
			
			//basically just re-implement what the constructor does - since we mixin at tail, this already ran
			//some modifyvariable magic on the BlockState wouldn't hurt, i suppose, but, eh
			//it'd need to capture method arguments but in this version of mixin it requires using threadlocals,
			//and 99.9999% of block dust particles are not for frame blocks, so it seems like a waste of cpu cycles
			int color = Minecraft.getInstance().getBlockColors().getColor(theme, clientWorld, pos, 0);
			a.setRCol(0.6f * ((color & 0xFF0000) >> 16) / 255f);
			a.setGCol(0.6f * ((color & 0x00FF00) >> 8) / 255f);
			a.setBCol(0.6f * (color & 0x0000FF) / 255f);
		}
	}
}
