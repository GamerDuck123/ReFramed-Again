package fr.adrien1106.reframed.block;

import fr.adrien1106.reframed.util.VoxelHelper;
import fr.adrien1106.reframed.util.blocks.BlockHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.entity.LivingEntity;
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
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
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

public class ReFramedDoorBlock extends WaterloggableReFramedBlock {

    public static final VoxelShape[] DOOR_VOXELS;

    public ReFramedDoorBlock(Properties settings) {
        super(settings);
        registerDefaultState(defaultBlockState()
            .setValue(HORIZONTAL_FACING, Direction.NORTH)
            .setValue(DOOR_HINGE, DoorHingeSide.LEFT)
            .setValue(DOUBLE_BLOCK_HALF, DoubleBlockHalf.LOWER)
            .setValue(OPEN, false)
            .setValue(POWERED, false)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(HORIZONTAL_FACING, DOOR_HINGE, DOUBLE_BLOCK_HALF, OPEN, POWERED));
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        BlockPos pos_down = pos.below();
        BlockState state_down = world.getBlockState(pos_down);
        return state.getValue(DOUBLE_BLOCK_HALF) == DoubleBlockHalf.LOWER ? !state_down.isAir() : state_down.is(this);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void neighborChanged(BlockState state, Level world, BlockPos pos, Block source, BlockPos sourcePos, boolean notify) {
        if (world.isClientSide) return;
        boolean powered = world.hasNeighborSignal(pos)
            || world.hasNeighborSignal(
                state.getValue(DOUBLE_BLOCK_HALF) == DoubleBlockHalf.LOWER
                    ? pos.above()
                    : pos.below()
        );
        if (!defaultBlockState().is(source) && powered != state.getValue(POWERED)) {
            if (state.getValue(OPEN) != powered)
                playToggleSound(null, world, pos, powered);

            world.setBlock(pos, state.setValue(POWERED, powered).setValue(OPEN, powered), 2);
            if (state.getValue(WATERLOGGED)) {
                world.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
            }
        }
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext ctx) {
        Level world = ctx.getLevel();
        BlockPos pos = ctx.getClickedPos();
        Direction facing = ctx.getHorizontalDirection().getOpposite();
        if (pos.getY() >= world.getMaxBuildHeight() - 1 || !world.getBlockState(pos.above()).canBeReplaced(ctx)) return null;
        BlockState state = super.getStateForPlacement(ctx)
            .setValue(DOUBLE_BLOCK_HALF, DoubleBlockHalf.LOWER)
            .setValue(HORIZONTAL_FACING, facing);

        if (world.hasNeighborSignal(pos) || world.hasNeighborSignal(pos.above()))
            state = state.setValue(OPEN, true).setValue(POWERED, true);


        return state.setValue(DOOR_HINGE, getHinge(facing, pos, world, BlockHelper.getRelativePos(ctx.getClickLocation(), pos)));
    }

    @Override
    public void onPlaced(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack, BlockState old_state, BlockEntity old_entity) {
        world.setBlock(
            pos.above(),
            state
                .setValue(DOUBLE_BLOCK_HALF, DoubleBlockHalf.UPPER)
                .setValue(WATERLOGGED, world.getFluidState(pos.above()).is(Fluids.WATER)),
            3
        );
    }

    @Override
    public BlockState playerWillDestroy(Level world, BlockPos pos, BlockState state, Player player) {
        if (!world.isClientSide() && (player.isCreative() || player.hasCorrectToolForDrops(state))) {
            DoubleBlockHalf half = state.getValue(DOUBLE_BLOCK_HALF);
            BlockPos other_pos = half == DoubleBlockHalf.LOWER ? pos.above() : pos.below();
            BlockState other_state = world.getBlockState(other_pos);
            if (other_state.is(this) && other_state.getValue(DOUBLE_BLOCK_HALF) != half) {
                world.setBlock(other_pos, other_state.getValue(WATERLOGGED) ? Blocks.WATER.defaultBlockState(): Blocks.AIR.defaultBlockState(), 35);
                world.levelEvent(player, 2001, other_pos, Block.getId(other_state));
            }
        }

        return super.playerWillDestroy(world, pos, state, player);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState other, LevelAccessor world, BlockPos pos, BlockPos moved) {
        if (direction.getAxis() == Direction.Axis.Y
            && other.is(this)
            && other.getValue(DOUBLE_BLOCK_HALF) != state.getValue(DOUBLE_BLOCK_HALF)
            && other.getValue(OPEN) != state.getValue(OPEN)
        ) return state.cycle(OPEN);
        Direction facing = state.getValue(HORIZONTAL_FACING);
        if (direction == (
            state.getValue(DOOR_HINGE) == DoorHingeSide.RIGHT
                ? facing.getClockWise()
                : facing.getCounterClockWise())
            && other.is(this)
            && other.getValue(DOUBLE_BLOCK_HALF) == state.getValue(DOUBLE_BLOCK_HALF)
            && other.getValue(DOOR_HINGE) != state.getValue(DOOR_HINGE)
            && !state.getValue(POWERED)
        ) return state.setValue(OPEN, other.getValue(OPEN));
        return super.updateShape(state, direction, other, world, pos, moved);
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
            case WATER -> false;
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
        world.setBlock(pos, state, 10);

        this.playToggleSound(player, world, pos, state.getValue(OPEN));
    }

    protected void playToggleSound(@Nullable Player player, Level world, BlockPos pos, boolean open) {
        world.playSound(player, pos, open ? BlockSetType.OAK.doorOpen() : BlockSetType.OAK.doorClose(), SoundSource.BLOCKS, 1.0F, world.getRandom().nextFloat() * 0.1F + 0.9F);
        world.gameEvent(player, open ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, pos);
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        Direction direction = state.getValue(HORIZONTAL_FACING);
        if (state.getValue(OPEN)) direction = switch (state.getValue(DOOR_HINGE)) {
            case RIGHT -> direction.getCounterClockWise();
            case LEFT -> direction.getClockWise();
        };
        return DOOR_VOXELS[direction.ordinal() - 2];
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(HORIZONTAL_FACING, rotation.rotate(state.getValue(HORIZONTAL_FACING)));
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState mirror(BlockState state, Mirror mirror) {
        return mirror == Mirror.NONE ? state : state.setValue(HORIZONTAL_FACING, mirror.mirror(state.getValue(HORIZONTAL_FACING))).cycle(DOOR_HINGE);
    }

    @Override
    @SuppressWarnings("deprecation")
    public long getSeed(BlockState state, BlockPos pos) {
        return Mth.getSeed(pos.getX(), pos.below(state.getValue(DOUBLE_BLOCK_HALF) == DoubleBlockHalf.LOWER ? 0 : 1).getY(), pos.getZ());
    }

    private DoorHingeSide getHinge(Direction facing, BlockPos pos, Level world, Vec3 hit_pos) {
        Direction left = facing.getClockWise();
        BlockPos left_pos = pos.relative(left);
        BlockState left_state = world.getBlockState(left_pos);
        Direction right = facing.getCounterClockWise();
        BlockPos right_pos = pos.relative(right);
        BlockState right_state = world.getBlockState(right_pos);
        DoorHingeSide hinge = null;

        if (left_state.isFaceSturdy(world, left_pos, right))
            hinge = DoorHingeSide.LEFT;
        if (right_state.isFaceSturdy(world, right_pos, left))
            hinge = hinge == DoorHingeSide.LEFT ? null : DoorHingeSide.RIGHT;

        if (hinge != null) return hinge;

        if (left_state.is(this)
            && left_state.getValue(HORIZONTAL_FACING) == facing
            && left_state.getValue(DOUBLE_BLOCK_HALF) == DoubleBlockHalf.LOWER
            && left_state.getValue(DOOR_HINGE) == DoorHingeSide.LEFT
        ) hinge = DoorHingeSide.RIGHT;
        if (right_state.is(this)
            && right_state.getValue(HORIZONTAL_FACING) == facing
            && right_state.getValue(DOUBLE_BLOCK_HALF) == DoubleBlockHalf.LOWER
            && right_state.getValue(DOOR_HINGE) == DoorHingeSide.RIGHT
        ) hinge = hinge == DoorHingeSide.RIGHT ? null : DoorHingeSide.LEFT;

        if (hinge != null) return hinge;

        return switch (facing.getAxis()) {
            case Z -> {
                if (left.getAxisDirection() == Direction.AxisDirection.POSITIVE)
                    yield hit_pos.x() < 0.5 ? DoorHingeSide.RIGHT : DoorHingeSide.LEFT;
                else // left.getDirection() == Direction.AxisDirection.NEGATIVE
                    yield hit_pos.x() < 0.5 ? DoorHingeSide.LEFT : DoorHingeSide.RIGHT;
            }
            case X -> {
                if (left.getAxisDirection() == Direction.AxisDirection.POSITIVE)
                    yield hit_pos.z() < 0.5 ? DoorHingeSide.RIGHT : DoorHingeSide.LEFT;
                else // left.getDirection() == Direction.AxisDirection.NEGATIVE
                    yield hit_pos.z() < 0.5 ? DoorHingeSide.LEFT : DoorHingeSide.RIGHT;
            }
            default -> DoorHingeSide.LEFT;
        };
    }

    static {
        VoxelShape SHAPE = box(0.0, 0.0, 0.0, 16.0, 16.0, 3.0);
        DOOR_VOXELS = VoxelHelper.VoxelListBuilder.create(SHAPE, 4)
            .add(VoxelHelper::mirrorZ)
            .add(VoxelHelper::rotateY)
            .add(VoxelHelper::mirrorX)
            .build();
    }
}
