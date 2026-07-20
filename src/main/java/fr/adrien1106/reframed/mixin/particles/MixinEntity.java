package fr.adrien1106.reframed.mixin.particles;

import com.llamalad7.mixinextras.sugar.Local;
import fr.adrien1106.reframed.block.ReFramedBlock;
import fr.adrien1106.reframed.util.blocks.ThemeableBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(Entity.class)
public abstract class MixinEntity {
	@ModifyArg(
		method = "spawnSprintParticle",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/core/particles/BlockParticleOption;<init>(Lnet/minecraft/core/particles/ParticleType;Lnet/minecraft/world/level/block/state/BlockState;)V")
	)
	private BlockState modifyParticleState(BlockState state, @Local(ordinal = 0) BlockPos landing_pos) {
		Level world = ((Entity) (Object) this).level();
		
		if(world.getBlockEntity(landing_pos) instanceof ThemeableBlockEntity themeable
			&& state.getBlock() instanceof ReFramedBlock block) {
				BlockState theme = themeable.getTheme(block.getTopThemeIndex(state));
				if(!theme.isAir()) return theme;
		}
		
		return state;
	}
}
