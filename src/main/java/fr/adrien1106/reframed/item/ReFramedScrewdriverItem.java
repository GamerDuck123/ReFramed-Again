package fr.adrien1106.reframed.item;

import fr.adrien1106.reframed.ReFramed;
import fr.adrien1106.reframed.block.ReFramedDoubleBlock;
import fr.adrien1106.reframed.util.blocks.ThemeableBlockEntity;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.InteractionResult;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;

public class ReFramedScrewdriverItem extends Item {

    public ReFramedScrewdriverItem(Properties settings) {
        super(settings);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        if (!(world.getBlockEntity(pos) instanceof ThemeableBlockEntity frame_entity)) return InteractionResult.PASS;
        BlockState state = world.getBlockState(pos);
        Player player = context.getPlayer();
        int theme_index = state.getBlock() instanceof ReFramedDoubleBlock b
            ? b.getHitShape(
                state,
                context.getClickLocation(),
                context.getClickedPos(),
                context.getClickedFace()
            )
            : 1;


        BlockState theme = frame_entity.getTheme(theme_index);
        if (!theme.hasProperty(BlockStateProperties.AXIS)) return InteractionResult.PASS;

        Direction.Axis axis = theme.getValue(BlockStateProperties.AXIS);
        SoundType group = theme.getSoundType();
        world.playSound(player, pos, group.getPlaceSound(), SoundSource.BLOCKS, group.getVolume(), group.getPitch());
        frame_entity.setTheme(theme.setValue(
            BlockStateProperties.AXIS,
            switch (axis) {
                case X -> Direction.Axis.Y;
                case Y -> Direction.Axis.Z;
                case Z -> Direction.Axis.X;
            }
        ), theme_index);
        if (world.isClientSide) ReFramed.chunkRerenderProxy.accept(world, pos);
        return InteractionResult.SUCCESS;
    }
}
