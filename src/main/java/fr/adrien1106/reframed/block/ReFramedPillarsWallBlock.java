package fr.adrien1106.reframed.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.level.block.state.properties.WallSide;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.stream.Collectors;

import static fr.adrien1106.reframed.block.ReFramedWallBlock.*;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.*;
import static net.minecraft.world.phys.shapes.Shapes.empty;

public class ReFramedPillarsWallBlock extends WaterloggableReFramedDoubleBlock {

    public ReFramedPillarsWallBlock(Properties settings) {
        super(settings);
        registerDefaultState(defaultBlockState()
            .setValue(EAST_WALL, WallSide.NONE)
            .setValue(NORTH_WALL, WallSide.NONE)
            .setValue(WEST_WALL, WallSide.NONE)
            .setValue(SOUTH_WALL, WallSide.NONE)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(EAST_WALL, NORTH_WALL, SOUTH_WALL, WEST_WALL));
    }

    @Override
    public BlockState updateShape(BlockState state, Direction dir, BlockState other_state, LevelAccessor world, BlockPos pos, BlockPos moved) {
        BlockState new_state = super.updateShape(state, dir, other_state, world, pos, moved);
        if (dir == Direction.DOWN) return new_state;
        BlockState top_state = dir == Direction.UP? other_state: world.getBlockState(pos.above());
        boolean fs = top_state.isFaceSturdy(world, pos.above(), Direction.DOWN);
        VoxelShape top_shape = fs ? null : top_state.getCollisionShape(world, pos.above()).getFaceShape(Direction.DOWN);
        Map<Direction, BlockState> neighbors = Direction.Plane.HORIZONTAL.stream()
            .collect(Collectors.toMap(d -> d, d -> {
                if (d == dir) return other_state;
                return world.getBlockState(pos.relative(d));
            }));
        return getWallState(new_state, top_state, neighbors, top_shape, fs, world, pos);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext ctx) {
        BlockState state = super.getStateForPlacement(ctx);
        Level world = ctx.getLevel();
        BlockPos pos = ctx.getClickedPos();

        BlockState top_state = world.getBlockState(pos.above());
        boolean fs = top_state.isFaceSturdy(world, pos.above(), Direction.DOWN);
        VoxelShape top_shape = fs ? null : top_state.getCollisionShape(world, pos.above()).getFaceShape(Direction.DOWN);

        Map<Direction, BlockState> neighbors = Direction.Plane.HORIZONTAL.stream()
            .collect(Collectors.toMap(d -> d, d -> world.getBlockState(pos.relative(d))));
        return getWallState(state, top_state, neighbors, top_shape, fs, world, pos);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter view, BlockPos pos, CollisionContext ctx) {
        if (isGhost(view, pos)) return empty();
        VoxelShape shape = WALL_VOXELS[9];
        for (Direction dir : Direction.Plane.HORIZONTAL) {
            if (state.getValue(getWallShape(dir)) != WallSide.NONE)
                shape = Shapes.or(shape, WALL_VOXELS[8 + dir.ordinal()]);
        }
        return shape;
    }

    @Override
    public VoxelShape getOcclusionShape(BlockState state, BlockGetter view, BlockPos pos) {
        return isGhost(view, pos) ? empty(): getShape(state, view, pos, CollisionContext.empty());
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        VoxelShape shape = WALL_VOXELS[0];
        for (Direction dir: Direction.Plane.HORIZONTAL) {
            WallSide wall_shape = state.getValue(getWallShape(dir));
            if (wall_shape != WallSide.NONE)
                shape = Shapes.or(shape, WALL_VOXELS[1 + (wall_shape.ordinal()-1) * 4 + (dir.ordinal() - 2)]);
        }
        return shape;
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState rotate(BlockState state, Rotation rotation) {
        return Direction.Plane.HORIZONTAL.stream().reduce(state, (s, dir) ->
            s.setValue(getWallShape(rotation.rotate(dir)), state.getValue(getWallShape(dir)))
        , (prev, next) -> next);
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState mirror(BlockState state, Mirror mirror) {
        return Direction.Plane.HORIZONTAL.stream().reduce(state, (s, dir) ->
                s.setValue(getWallShape(mirror.mirror(dir)), state.getValue(getWallShape(dir)))
            , (prev, next) -> next);
    }

    @Override
    public VoxelShape getShape(BlockState state, int i) {
        if (i == 1) return WALL_VOXELS[0];
        VoxelShape shape = Shapes.empty();
        for (Direction dir: Direction.Plane.HORIZONTAL) {
            WallSide wall_shape = state.getValue(getWallShape(dir));
            if (wall_shape != WallSide.NONE)
                shape = Shapes.or(shape, WALL_VOXELS[1 + (wall_shape.ordinal()-1) * 4 + (dir.ordinal() - 2)]);
        }
        return shape;
    }
}
