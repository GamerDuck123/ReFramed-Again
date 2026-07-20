package fr.adrien1106.reframed.block;

import fr.adrien1106.reframed.ReFramed;
import fr.adrien1106.reframed.util.blocks.BlockHelper;
import fr.adrien1106.reframed.util.blocks.ThemeableBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import static net.minecraft.world.phys.shapes.Shapes.empty;

public abstract class ReFramedDoubleBlock extends ReFramedBlock {
    public ReFramedDoubleBlock(Properties settings) {
        super(settings);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return ReFramed.REFRAMED_DOUBLE_BLOCK_ENTITY.create(pos, state);
    }

    public int getHitShape(BlockState state, BlockHitResult hit) {
        return getHitShape(state, hit.getLocation(), hit.getBlockPos(), hit.getDirection());
    }

    public int getHitShape(BlockState state, Vec3 hit, BlockPos pos, Direction side) {
        VoxelShape first_shape = getShape(state, 1);
        VoxelShape second_shape = getShape(state, 2);

        // Determine if any of the two shape is covering the side entirely
        if (isFaceFull(first_shape, side)) return 1;
        if (isFaceFull(second_shape, side)) return 2;

        Vec3 rel = BlockHelper.getRelativePos(hit, pos);
        if (BlockHelper.cursorMatchesFace(first_shape, rel)) return 1;
        if (BlockHelper.cursorMatchesFace(second_shape, rel)) return 2;
        return 0;
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter world, BlockPos pos) {
        return world.getBlockEntity(pos) instanceof ThemeableBlockEntity framed_entity
            && framed_entity.getThemes().stream().allMatch(theme -> theme.propagatesSkylightDown(world, pos));
    }

    public VoxelShape getRenderOutline(BlockState state, BlockHitResult hit) {
        return getShape(state, getHitShape(state, hit));
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter view, BlockPos pos, CollisionContext ctx) {
        return isGhost(view, pos) ? empty() : getShape(state, view, pos, ctx);
    }

    @Override
    public VoxelShape getOcclusionShape(BlockState state, BlockGetter view, BlockPos pos) {
        return getCollisionShape(state, view, pos, CollisionContext.empty());
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!canUse(world, pos, player)) return InteractionResult.PASS;
        InteractionResult result = BlockHelper.useUpgrade(state, world, pos, player, hand);
        if (result.consumesAction()) return result;
        return BlockHelper.useCamo(state, world, pos, player, hand, hit, getHitShape(state, hit));
    }
}
