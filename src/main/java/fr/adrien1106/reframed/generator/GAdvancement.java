package fr.adrien1106.reframed.generator;

import fr.adrien1106.reframed.ReFramed;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.world.item.Items;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;

import static net.minecraft.advancements.AdvancementRewards.Builder.experience;

public class GAdvancement extends FabricAdvancementProvider {
    protected GAdvancement(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateAdvancement(Consumer<AdvancementHolder> consumer) {
        Advancement.Builder builder = Advancement.Builder.advancement()
            .display(
                Items.CAKE,
                Component.literal("Is Everything A Lie ?"),
                Component.translatable("advancements.reframed.description"),
                new ResourceLocation("textures/gui/advancements/backgrounds/adventure.png"),
                AdvancementType.TASK,
                true,
                true,
                false
            ).rewards(net.minecraft.advancements.AdvancementRewards.Builder.experience(1000));
        ReFramed.BLOCKS.forEach(block ->
            builder.addCriterion(
                "get_" + BuiltInRegistries.BLOCK.getKey(block).getPath(),
                InventoryChangeTrigger.TriggerInstance.hasItems(block)
            )
        );
        builder.save(consumer, ReFramed.MODID + "/root");
    }
}
