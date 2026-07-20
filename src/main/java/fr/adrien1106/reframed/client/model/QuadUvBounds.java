package fr.adrien1106.reframed.client.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadView;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Mth;

@Environment(EnvType.CLIENT)
public record QuadUvBounds(float minU, float maxU, float minV, float maxV) {
	public static QuadUvBounds read(QuadView quad) {
		float u0 = quad.u(0), u1 = quad.u(1), u2 = quad.u(2), u3 = quad.u(3);
		float v0 = quad.v(0), v1 = quad.v(1), v2 = quad.v(2), v3 = quad.v(3);
		return new QuadUvBounds(
			Math.min(Math.min(u0, u1), Math.min(u2, u3)),
			Math.max(Math.max(u0, u1), Math.max(u2, u3)),
			Math.min(Math.min(v0, v1), Math.min(v2, v3)),
			Math.max(Math.max(v0, v1), Math.max(v2, v3))
		);
	}
	
	boolean displaysSprite(TextureAtlasSprite sprite) {
		return sprite.getU0() <= minU && sprite.getU1() >= maxU && sprite.getV0() <= minV && sprite.getV1() >= maxV;
	}
	
	void normalizeUv(MutableQuadView quad, TextureAtlasSprite sprite) {
		float remappedMinU = norm(minU, sprite.getU0(), sprite.getU1());
		float remappedMaxU = norm(maxU, sprite.getU0(), sprite.getU1());
		float remappedMinV = norm(minV, sprite.getV0(), sprite.getV1());
		float remappedMaxV = norm(maxV, sprite.getV0(), sprite.getV1());
		quad.uv(0, Mth.equal(quad.u(0), minU) ? remappedMinU : remappedMaxU, Mth.equal(quad.v(0), minV) ? remappedMinV : remappedMaxV);
		quad.uv(1, Mth.equal(quad.u(1), minU) ? remappedMinU : remappedMaxU, Mth.equal(quad.v(1), minV) ? remappedMinV : remappedMaxV);
		quad.uv(2, Mth.equal(quad.u(2), minU) ? remappedMinU : remappedMaxU, Mth.equal(quad.v(2), minV) ? remappedMinV : remappedMaxV);
		quad.uv(3, Mth.equal(quad.u(3), minU) ? remappedMinU : remappedMaxU, Mth.equal(quad.v(3), minV) ? remappedMinV : remappedMaxV);
	}
	
	static float norm(float value, float low, float high) {
		float value2 = Mth.clamp(value, low, high);
		return (value2 - low) / (high - low);
	}

}
