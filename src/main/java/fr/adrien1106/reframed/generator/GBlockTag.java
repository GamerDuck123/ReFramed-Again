package fr.adrien1106.reframed.generator;

import fr.adrien1106.reframed.ReFramed;
import fr.adrien1106.reframed.block.*;
import fr.adrien1106.reframed.generator.block.*;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider.BlockTagProvider;
import net.minecraft.world.level.block.Block;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.tags.BlockTags;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class GBlockTag extends BlockTagProvider {
    private static final Map<Class<? extends Block>, TagGetter> providers = new HashMap<>();
    static {
        providers.put(ReFramedPillarsWallBlock.class, new PillarsWall());
        providers.put(ReFramedWallBlock.class, new Wall());
        providers.put(ReFramedPaneBlock.class, new Pane());
        providers.put(ReFramedFenceBlock.class, new Fence());
        providers.put(ReFramedPostFenceBlock.class, new PostFence());
    }

    public GBlockTag(FabricDataOutput output, CompletableFuture<Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void addTags(Provider arg) {
        FabricTagBuilder builder = tag(BlockTags.MINEABLE_WITH_AXE);
        ReFramed.BLOCKS.forEach((block) -> {
            if (providers.containsKey(block.getClass()))
                providers.get(block.getClass()).getTags().forEach((tag) -> tag(tag).add(block));
            builder.add(block);
        });
    }
}
