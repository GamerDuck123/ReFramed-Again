package fr.adrien1106.reframed.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.LeadItem;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import org.jetbrains.annotations.Nullable;

import static fr.adrien1106.reframed.block.ConnectingReFramedBlock.getConnectionProperty;
import static fr.adrien1106.reframed.block.ConnectingReFramedBlock.placementState;
import static fr.adrien1106.reframed.block.ReFramedFenceBlock.FENCE_VOXELS;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.*;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.WEST;

public class ReFramedPostFenceBlock extends WaterloggableReFramedDoubleBlock {

    public ReFramedPostFenceBlock(Properties settings) {
        super(settings);
        registerDefaultState(defaultBlockState()
            .setValue(EAST, false)
            .setValue(NORTH, false)
            .setValue(WEST, false)
            .setValue(SOUTH, false)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(EAST, NORTH, SOUTH, WEST));
    }

    @Override
    public BlockState updateShape(BlockState state, Direction dir, BlockState other_state, LevelAccessor world, BlockPos pos, BlockPos moved) {
        BlockState new_state = super.updateShape(state, dir, other_state, world, pos, moved);
        if (dir == Direction.DOWN) return new_state;

        return placementState(new_state, world, pos, this::connectsTo);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext ctx) {
        BlockState state = super.getStateForPlacement(ctx);
        Level world = ctx.getLevel();
        BlockPos pos = ctx.getClickedPos();

        return placementState(state, world, pos, this::connectsTo);
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState rotate(BlockState state, Rotation rotation) {
        return Direction.Plane.HORIZONTAL.stream().reduce(state, (s, dir) ->
                s.setValue(getConnectionProperty(rotation.rotate(dir)), state.getValue(getConnectionProperty(dir)))
            , (prev, next) -> next);
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState mirror(BlockState state, Mirror mirror) {
        return Direction.Plane.HORIZONTAL.stream().reduce(state, (s, dir) ->
                s.setValue(getConnectionProperty(mirror.mirror(dir)), state.getValue(getConnectionProperty(dir)))
            , (prev, next) -> next);
    }

    private boolean connectsTo(BlockState state, boolean fs, Direction dir) {
        return fs || state.is(BlockTags.FENCES)
            || (state.getBlock() instanceof FenceGateBlock && FenceGateBlock.connectsToDirection(state, dir));
    }

    @Override
    public VoxelShape getShape(BlockState state, int i) {
        if (i == 1) return FENCE_VOXELS[0];
        VoxelShape shape = Shapes.empty();
        for (Direction dir: Direction.Plane.HORIZONTAL) {
            if (state.getValue(getConnectionProperty(dir)))
                shape = Shapes.or(shape, FENCE_VOXELS[dir.ordinal() - 1]);
        }
        return shape;
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        VoxelShape shape = FENCE_VOXELS[0];
        for (Direction dir: Direction.Plane.HORIZONTAL) {
            if (state.getValue(getConnectionProperty(dir)))
                shape = Shapes.or(shape, FENCE_VOXELS[dir.ordinal() - 1]);
        }
        return shape;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter view, BlockPos pos, CollisionContext ctx) {
        VoxelShape shape = FENCE_VOXELS[5];
        for (Direction dir: Direction.Plane.HORIZONTAL) {
            if (state.getValue(getConnectionProperty(dir)))
                shape = Shapes.or(shape, FENCE_VOXELS[dir.ordinal() + 4]);
        }
        return shape;
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getVisualShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return getShape(state, world, pos, context);
    }

    @Override
    public VoxelShape getOcclusionShape(BlockState state, BlockGetter view, BlockPos pos) {
        return getShape(state, view, pos, CollisionContext.empty());
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isPathfindable(BlockState state, BlockGetter world, BlockPos pos, PathComputationType type) {
        return false;
    }

    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        InteractionResult result = super.use(state, world, pos, player, hand, hit);
        if (result.consumesAction()) return result;
        if (world.isClientSide) {
            ItemStack itemStack = player.getItemInHand(hand);
            return itemStack.is(Items.LEAD) ? InteractionResult.SUCCESS : InteractionResult.PASS;
        } else {
            return LeadItem.bindPlayerMobs(player, world, pos);
        }
    }

}
