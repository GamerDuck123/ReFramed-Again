package fr.adrien1106.reframed.mixin.particles;

import net.minecraft.client.particle.Particle;
import net.minecraft.util.RandomSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Particle.class)
public interface AccessorParticle {
	@Accessor("random")
    RandomSource getRandom();
	
	@Accessor("rCol") void setRCol(float red);
	@Accessor("gCol") void setGCol(float green);
	@Accessor("bCol") void setBCol(float blue);
}
