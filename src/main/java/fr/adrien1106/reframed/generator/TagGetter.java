package fr.adrien1106.reframed.generator;

import net.minecraft.world.level.block.Block;
import net.minecraft.tags.TagKey;

import java.util.List;

public interface TagGetter {

    List<TagKey<Block>> getTags();
}
