package fr.adrien1106.reframed.block;

import fr.adrien1106.reframed.util.VoxelHelper;
import net.minecraft.block.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.Explosion;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.*;

public class ReFramedTrapdoorBlock extends WaterloggableReFramedBlock {

    public static final VoxelShape[] TRAPDOOR_VOXELS;

    public ReFramedTrapdoorBlock(Properties settings) {
        super(settings);
        registerDefaultState(defaultBlockState()
            .setValue(HORIZONTAL_FACING, Direction.NORTH)
            .setValue(HALF, Half.BOTTOM)
            .setValue(OPEN, false)
            .setValue(POWERED, false)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(HORIZONTAL_FACING, HALF, OPEN, POWERED));
    }

    @Override
    @SuppressWarnings("deprecation")
    public void neighborChanged(BlockState state, Level world, BlockPos pos, Block source, BlockPos sourcePos, boolean notify) {
        if (world.isClientSide) return;
        boolean powered = world.hasNeighborSignal(pos);
        if (powered != state.getValue(POWERED)) {
            if (state.getValue(OPEN) != powered) {
                state = state.setValue(OPEN, powered);
                playToggleSound(null, world, pos, powered);
            }

            world.setBlock(pos, state.setValue(POWERED, powered), 2);
            if (state.getValue(WATERLOGGED)) {
                world.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
            }
        }
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext ctx) {
        BlockState state = super.getStateForPlacement(ctx);
        Level world = ctx.getLevel();
        BlockPos pos = ctx.getClickedPos();
        Direction side = ctx.getClickedFace();

        if (side.getAxis().isVertical()) state = state
            .setValue(HORIZONTAL_FACING, ctx.getHorizontalDirection().getOpposite())
            .setValue(HALF, side == Direction.UP ? Half.BOTTOM : Half.TOP);
        else state = state
            .setValue(HORIZONTAL_FACING, side)
            .setValue(HALF, ctx.getClickLocation().y() - pos.getY() > 0.5 ? Half.TOP : Half.BOTTOM);

        if (world.hasNeighborSignal(pos)) state = state.setValue(OPEN, true).setValue(POWERED, true);

        return state;
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        InteractionResult result = super.use(state, world, pos, player, hand, hit);
        if (result.consumesAction()) return result;
        flip(state, world, pos, player);
        return InteractionResult.sidedSuccess(world.isClientSide);
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isPathfindable(BlockState state, BlockGetter world, BlockPos pos, PathComputationType type) {
        return switch (type) {
            case LAND, AIR -> state.getValue(OPEN);
            case WATER -> state.getValue(WATERLOGGED);
        };
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onExplosionHit(BlockState state, Level world, BlockPos pos, Explosion explosion, BiConsumer<ItemStack, BlockPos> stack_merger) {
        if (explosion.getBlockInteraction() == Explosion.BlockInteraction.TRIGGER_BLOCK
                && !world.isClientSide()
                && !state.getValue(POWERED)
        ) flip(state, world, pos, null);

        super.onExplosionHit(state, world, pos, explosion, stack_merger);
    }

    private void flip(BlockState state, Level world, BlockPos pos, @Nullable Player player) {
        state = state.cycle(OPEN);
        world.setBlock(pos, state, 2);

        this.playToggleSound(player, world, pos, state.getValue(OPEN));
    }

    protected void playToggleSound(@Nullable Player player, Level world, BlockPos pos, boolean open) {
        world.playSound(player, pos, open ? BlockSetType.OAK.trapdoorOpen() : BlockSetType.OAK.trapdoorClose(), SoundSource.BLOCKS, 1.0F, world.getRandom().nextFloat() * 0.1F + 0.9F);
        world.gameEvent(player, open ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, pos);
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        int index;
        if (!state.getValue(OPEN)) index = state.getValue(HALF) == Half.BOTTOM ? 0 : 1;
        else index = state.getValue(HORIZONTAL_FACING).ordinal();
        return TRAPDOOR_VOXELS[index];
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(HORIZONTAL_FACING, rotation.rotate(state.getValue(HORIZONTAL_FACING)));
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.setValue(HORIZONTAL_FACING, mirror.mirror(state.getValue(HORIZONTAL_FACING)));
    }

    static {
        VoxelShape SHAPE = box(0, 0, 0, 16, 3, 16);
        TRAPDOOR_VOXELS = VoxelHelper.VoxelListBuilder.create(SHAPE, 6)
            .add(VoxelHelper::mirrorY)
            .add(VoxelHelper::rotateX)
            .add(VoxelHelper::mirrorZ)
            .add(VoxelHelper::rotateY)
            .add(VoxelHelper::mirrorX)
            .build();
    }
}
