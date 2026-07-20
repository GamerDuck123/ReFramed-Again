package fr.adrien1106.reframed.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import org.apache.commons.lang3.function.TriFunction;
import org.jetbrains.annotations.Nullable;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.*;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.SOUTH;

public abstract class ConnectingReFramedBlock extends WaterloggableReFramedBlock {

    public ConnectingReFramedBlock(Properties settings) {
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

    protected abstract boolean connectsTo(BlockState state, boolean fs, Direction dir);

    @Override
    @SuppressWarnings("deprecation")
    public abstract VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context);

    public static BooleanProperty getConnectionProperty(Direction dir) {
        return switch (dir) {
            case NORTH -> NORTH;
            case EAST -> EAST;
            case SOUTH -> SOUTH;
            case WEST -> WEST;
            default -> null;
        };
    }

    public static BlockState placementState(BlockState state, BlockGetter world, BlockPos pos, TriFunction<BlockState, Boolean, Direction, Boolean> connectsTo) {
        for (Direction dir: Direction.Plane.HORIZONTAL) {
            BlockState neighbor = world.getBlockState(pos.relative(dir));
            state = state.setValue(getConnectionProperty(dir), connectsTo.apply(
                neighbor,
                neighbor.isFaceSturdy(world, pos.relative(dir), dir.getOpposite()),
                dir
            ));
        }
        return state;
    }
}
