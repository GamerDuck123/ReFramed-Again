package fr.adrien1106.reframed.block;

import fr.adrien1106.reframed.util.VoxelHelper;
import net.minecraft.block.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.Explosion;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.*;

public class ReFramedButtonBlock extends WaterloggableReFramedBlock {

    public static final VoxelShape[] BUTTON_VOXELS;

    public ReFramedButtonBlock(Properties settings) {
        super(settings);
        registerDefaultState(defaultBlockState()
            .setValue(HORIZONTAL_FACING, Direction.NORTH)
            .setValue(ATTACH_FACE, AttachFace.WALL)
            .setValue(POWERED, false)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(HORIZONTAL_FACING, ATTACH_FACE, POWERED));
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        return canPlaceAt(world, pos, getDirection(state).getOpposite());
    }

    public static boolean canPlaceAt(LevelReader world, BlockPos pos, Direction direction) {
        BlockPos other_pos = pos.relative(direction);
        return world.getBlockState(other_pos).isFaceSturdy(world, other_pos, direction.getOpposite());
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext ctx) {
        BlockState state = super.getStateForPlacement(ctx);
        Direction side = ctx.getClickedFace();
        return state
            .setValue(HORIZONTAL_FACING, side.getAxis() == Direction.Axis.Y
                ? ctx.getHorizontalDirection()
                : side
            )
            .setValue(ATTACH_FACE, side.getAxis() != Direction.Axis.Y
                ? AttachFace.WALL
                : side == Direction.UP ? AttachFace.FLOOR : AttachFace.CEILING
            );
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState other_state, LevelAccessor world, BlockPos pos, BlockPos other_pos) {
        return getDirection(state).getOpposite() == direction && !state.canSurvive(world, pos)
            ? Blocks.AIR.defaultBlockState()
            : super.updateShape(state, direction, other_state, world, pos, other_pos);
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        InteractionResult result = super.use(state, world, pos, player, hand, hit);
        if (result.consumesAction()) return result;

        if (state.getValue(POWERED)) return InteractionResult.CONSUME;
        powerOn(state, world, pos);
        playClickSound(player, world, pos, true);
        world.gameEvent(player, GameEvent.BLOCK_ACTIVATE, pos);

        return InteractionResult.sidedSuccess(world.isClientSide);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onExplosionHit(BlockState state, Level world, BlockPos pos, Explosion explosion, BiConsumer<ItemStack, BlockPos> stackMerger) {
        if (explosion.getBlockInteraction() == Explosion.BlockInteraction.TRIGGER_BLOCK && !world.isClientSide() && !(Boolean)state.getValue(POWERED)) {
            powerOn(state, world, pos);
        }

        super.onExplosionHit(state, world, pos, explosion, stackMerger);
    }

    public void powerOn(BlockState state, Level world, BlockPos pos) {
        world.setBlock(pos, state.setValue(POWERED, true), 3);
        updateNeighbors(state, world, pos);
        world.scheduleTick(pos, this, 30);
    }

    protected void playClickSound(@Nullable Player player, LevelAccessor world, BlockPos pos, boolean powered) {
        world.playSound(
            powered ? player : null,
            pos,
            powered ? BlockSetType.OAK.buttonClickOn() : BlockSetType.OAK.buttonClickOff(),
            SoundSource.BLOCKS
        );
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return BUTTON_VOXELS[
            (state.getValue(POWERED) ? 12 : 0) +
            (4 * state.getValue(ATTACH_FACE).ordinal()) +
            state.getValue(HORIZONTAL_FACING).ordinal() - 2
        ];
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

    @Override
    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState new_state, boolean moved) {
        super.onRemove(state, world, pos, new_state, false);

        if(!state.is(new_state.getBlock())) {
            if (!moved && state.getValue(POWERED)) updateNeighbors(state, world, pos);
        }
    }

    @Override
    public int getSignal(BlockState state, BlockGetter view, BlockPos pos, Direction dir) {
        return state.getValue(POWERED) ? 15 : super.getSignal(state, view, pos, dir);
    }

    @Override
    public int getDirectSignal(BlockState state, BlockGetter view, BlockPos pos, Direction dir) {
        return dir == getDirection(state) ? state.getSignal(view, pos, dir) : 0;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isSignalSource(BlockState state) {
        return true;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        if (state.getValue(POWERED)) tryPowerWithProjectiles(state, world, pos);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void entityInside(BlockState state, Level world, BlockPos pos, Entity entity) {
        if (!world.isClientSide && !state.getValue(POWERED)) tryPowerWithProjectiles(state, world, pos);
    }

    protected void tryPowerWithProjectiles(BlockState state, Level world, BlockPos pos) {
        AbstractArrow projectile = world.getEntitiesOfClass(
            AbstractArrow.class,
            state.getShape(world, pos).bounds().move(pos)
        ).stream().findFirst().orElse(null);
        boolean has_projectile = projectile != null;
        if (has_projectile != state.getValue(POWERED)) {
            world.setBlock(pos, state.setValue(POWERED, has_projectile), 3);
            this.updateNeighbors(state, world, pos);
            this.playClickSound(null, world, pos, has_projectile);
            world.gameEvent(projectile, has_projectile ? GameEvent.BLOCK_ACTIVATE : GameEvent.BLOCK_DEACTIVATE, pos);
        }

        if (has_projectile) {
            world.scheduleTick(pos, this, 30);
        }

    }

    private void updateNeighbors(BlockState state, Level world, BlockPos pos) {
        world.updateNeighborsAt(pos, this);
        world.updateNeighborsAt(pos.relative(getDirection(state).getOpposite()), this);
    }

    protected static Direction getDirection(BlockState state) {
        return switch (state.getValue(ATTACH_FACE)) {
            case CEILING -> Direction.DOWN;
            case FLOOR -> Direction.UP;
            default -> state.getValue(HORIZONTAL_FACING);
        };
    }


    static {
        VoxelShape SHAPE = box(5, 0, 6, 11, 2, 10);
        VoxelShape POWERED_SHAPE = box(5, 0, 6, 11, 1, 10);
        BUTTON_VOXELS = VoxelHelper.VoxelListBuilder.create(SHAPE, 24)
            .add()
            .add(0, VoxelHelper::rotateY)
            .add()
            .add(VoxelHelper::rotateZ, VoxelHelper::rotateY)
            .add(VoxelHelper::mirrorZ)
            .add(VoxelHelper::rotateY)
            .add(VoxelHelper::mirrorX)
            .add(0, VoxelHelper::mirrorY)
            .add()
            .add(2, VoxelHelper::mirrorY)
            .add()
            .add(POWERED_SHAPE)
            .add()
            .add(12, VoxelHelper::rotateY)
            .add()
            .add(VoxelHelper::rotateZ, VoxelHelper::rotateY)
            .add(VoxelHelper::mirrorZ)
            .add(VoxelHelper::rotateY)
            .add(VoxelHelper::mirrorX)
            .add(12, VoxelHelper::mirrorY)
            .add()
            .add(13, VoxelHelper::mirrorY)
            .add()
            .build();
    }
}
