package fr.adrien1106.reframed.item;

import fr.adrien1106.reframed.ReFramed;
import fr.adrien1106.reframed.block.ReFramedEntity;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.ChatFormatting;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static fr.adrien1106.reframed.block.ReFramedEntity.BLOCKSTATE_KEY;

public class ReFramedBlueprintWrittenItem extends Item {
    public ReFramedBlueprintWrittenItem(Properties settings) {
        super(settings);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!player.isShiftKeyDown() || !stack.hasTag()) return super.use(world, player, hand);
        stack.shrink(1);
        player.addItem(ReFramed.BLUEPRINT.getDefaultInstance());
        world.playSound(player, player.blockPosition(), SoundEvents.BOOK_PUT, SoundSource.PLAYERS);

        return InteractionResultHolder.success(stack);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        BlockPos pos = context.getClickedPos();
        Level world = context.getLevel();
        if (!(world.getBlockEntity(pos) instanceof ReFramedEntity frame_entity)
            || frame_entity.getThemes().stream().anyMatch(state -> state.getBlock() != Blocks.AIR)
            || !context.getItemInHand().hasTag()
        ) return InteractionResult.PASS;

        CompoundTag tag = BlockItem.getBlockEntityData(context.getItemInHand());
        if(tag == null) return InteractionResult.FAIL;

        Player player = context.getPlayer();
        if (!player.isCreative()) { // verify player has blocks and remove them
            Inventory inventory = player.getInventory();
            List<ItemStack> stacks = getBlockStates(tag).values().stream()
                .map(BlockBehaviour.BlockStateBase::getBlock)
                .map(Block::asItem)
                .map(Item::getDefaultInstance)
                .toList();
            if (stacks.stream().anyMatch(stack -> !inventory.contains(stack)))
                return InteractionResult.FAIL;
            stacks.stream().map(inventory::findSlotMatchingItem).forEach(index -> inventory.removeItem(index, 1));
            player.playSound(SoundEvents.ITEM_PICKUP, 0.5f, 0.5f);
        }
        for (int i = 1; tag.contains(BLOCKSTATE_KEY + i); i++) {
            BlockState state = NbtUtils.readBlockState(
                BuiltInRegistries.BLOCK.asLookup(),
                tag.getCompound(BLOCKSTATE_KEY + i)
            );
            frame_entity.setTheme(state, i);
        }
        if (world.isClientSide) ReFramed.chunkRerenderProxy.accept(world, pos);
        world.playSound(player, player.blockPosition(), SoundEvents.BOOK_PAGE_TURN, SoundSource.PLAYERS);

        return InteractionResult.SUCCESS;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag context) {
        CompoundTag tag = BlockItem.getBlockEntityData(stack);
        if(tag == null) return;

        Map<Integer, BlockState> states = getBlockStates(tag);
        states.forEach((index, state) -> tooltip.add(
            Component.literal("Theme " + index + ": ")
                .append(
                    Component.translatable(state.getBlock().getDescriptionId())
                        .withStyle(ChatFormatting.GRAY)
                )
        ));
        super.appendHoverText(stack, world, tooltip, context);
    }

    private static Map<Integer, BlockState> getBlockStates(CompoundTag tag) {
        return tag.getAllKeys().stream()
            .filter(key ->
                key.startsWith(BLOCKSTATE_KEY)
                && key.replace(BLOCKSTATE_KEY,"").chars().allMatch(Character::isDigit)
            )
            .collect(Collectors.toMap(
                key -> Integer.parseInt(key.substring(BLOCKSTATE_KEY.length())),
                key -> NbtUtils.readBlockState(
                    BuiltInRegistries.BLOCK.asLookup(),
                    tag.getCompound(key)
                )
            ));
    }
}
