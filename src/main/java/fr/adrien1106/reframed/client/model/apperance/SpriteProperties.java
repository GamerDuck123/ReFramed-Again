package fr.adrien1106.reframed.client.model.apperance;

import fr.adrien1106.reframed.client.model.QuadPosBounds;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public record SpriteProperties(TextureAtlasSprite sprite, int flags, QuadPosBounds bounds, boolean has_colors) {
}
