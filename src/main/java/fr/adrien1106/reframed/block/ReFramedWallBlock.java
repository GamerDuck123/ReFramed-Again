package fr.adrien1106.reframed.block;

import fr.adrien1106.reframed.util.VoxelHelper;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WallSide;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.*;

public class ReFramedWallBlock extends WaterloggableReFramedBlock {

    public static final VoxelShape[] WALL_VOXELS;

    public ReFramedWallBlock(Properties settings) {
        super(settings);
        registerDefaultState(defaultBlockState()
            .setValue(UP, true)
            .setValue(EAST_WALL, WallSide.NONE)
            .setValue(NORTH_WALL, WallSide.NONE)
            .setValue(WEST_WALL, WallSide.NONE)
            .setValue(SOUTH_WALL, WallSide.NONE)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(UP, EAST_WALL, NORTH_WALL, SOUTH_WALL, WEST_WALL));
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
        new_state = getWallState(new_state, top_state, neighbors, top_shape, fs, world, pos);
        return new_state.setValue(UP, shouldHavePost(new_state, top_state, top_shape));
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
        state = getWallState(state, top_state, neighbors, top_shape, fs, world, pos);
        return state.setValue(UP, shouldHavePost(state, top_state, top_shape));
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        VoxelShape shape = state.getValue(UP) ? WALL_VOXELS[0]: Shapes.empty();
        for (Direction dir : Direction.Plane.HORIZONTAL) {
            WallSide wall_shape = state.getValue(getWallShape(dir));
            if (wall_shape != WallSide.NONE)
                shape = Shapes.or(shape, WALL_VOXELS[1 + (wall_shape.ordinal()-1) * 4 + (dir.ordinal() - 2)]);
        }
        return shape;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter view, BlockPos pos, CollisionContext ctx) {
        if (isGhost(view, pos)) return Shapes.empty();
        VoxelShape shape = state.getValue(UP) ? WALL_VOXELS[9]: Shapes.empty();
        for (Direction dir : Direction.Plane.HORIZONTAL) {
            if (state.getValue(getWallShape(dir)) != WallSide.NONE)
                shape = Shapes.or(shape, WALL_VOXELS[8 + dir.ordinal()]);
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

    public static BlockState getWallState(BlockState state, BlockState top_state, Map<Direction, BlockState> neighbors, VoxelShape top_shape, boolean fs, LevelReader world, BlockPos pos) {
        for (Direction dir : Direction.Plane.HORIZONTAL) {
            BlockPos offset = pos.relative(dir);
            BlockState neighbor = neighbors.get(dir);
            boolean side_full = neighbor.isFaceSturdy(world, offset, dir.getOpposite());
            Property<WallSide> wall_shape = getWallShape(dir);
            if (shouldConnectTo(neighbor, side_full, dir.getOpposite())) {
                state = state.setValue(
                    wall_shape,
                    fs
                        || (top_state.hasProperty(wall_shape) && top_state.getValue(wall_shape) != WallSide.NONE)
                        || shouldUseTall(WALL_VOXELS[dir.ordinal() + 3], top_shape)
                        ? WallSide.TALL
                        : WallSide.LOW
                );
            } else state = state.setValue(wall_shape, WallSide.NONE);
        }
        return state;
    }

    public static boolean shouldHavePost(BlockState state, BlockState top_state, VoxelShape top_shape) {
        // above has post
        if ((top_state.hasProperty(UP) && top_state.getValue(UP)) || (!top_state.hasProperty(UP) && top_state.hasProperty(NORTH_WALL))) return true;

        if (Stream.of(Direction.SOUTH, Direction.EAST) // Opposites are different
            .anyMatch(dir -> state.getValue(getWallShape(dir)) != state.getValue(getWallShape(dir.getOpposite())))
        ) return true;

        // no sides
        if (Direction.Plane.HORIZONTAL.stream().allMatch(dir -> state.getValue(getWallShape(dir)) == WallSide.NONE))
            return true;

        // Matching sides
        if (Stream.of(Direction.SOUTH, Direction.EAST)
            .anyMatch(dir ->
                   state.getValue(getWallShape(dir)) == state.getValue(getWallShape(dir.getOpposite()))
            )) return false;

        return top_state.is(BlockTags.WALL_POST_OVERRIDE) || top_shape == null || shouldUseTall(WALL_VOXELS[0], top_shape);
    }

    public static boolean shouldConnectTo(BlockState state, boolean side_full, Direction side) {
        Block block = state.getBlock();
        boolean bl = block instanceof FenceGateBlock && FenceGateBlock.connectsToDirection(state, side);
        return state.is(BlockTags.WALLS) || !WallBlock.isExceptionForConnection(state) && side_full || block instanceof IronBarsBlock || bl;
    }

    public static boolean shouldUseTall(VoxelShape self_shape, VoxelShape other_shape) {
        return !Shapes.joinIsNotEmpty(
            self_shape,
            other_shape,
            BooleanOp.ONLY_FIRST
        );
    }

    public static Property<WallSide> getWallShape(Direction dir) {
        return switch (dir) {
            case EAST -> EAST_WALL;
            case NORTH -> NORTH_WALL;
            case WEST -> WEST_WALL;
            case SOUTH -> SOUTH_WALL;
            default -> null;
        };
    }

    static {
        VoxelShape POST = box(4, 0, 4, 12, 16, 12);
        VoxelShape POST_COLLISION = box(4, 0, 4, 12, 24, 12);
        VoxelShape LOW = box(5, 0, 0, 11, 14, 8);
        VoxelShape TALL = box(5, 0, 0, 11, 16, 8);
        VoxelShape SIDE_COLLISION = box(5, 0, 0, 11, 24, 8);
        WALL_VOXELS = VoxelHelper.VoxelListBuilder.create(POST, 14)
            .add(LOW)
            .add(VoxelHelper::mirrorZ)
            .add(VoxelHelper::rotateY)
            .add(VoxelHelper::mirrorX)
            .add(TALL)
            .add(VoxelHelper::mirrorZ)
            .add(VoxelHelper::rotateY)
            .add(VoxelHelper::mirrorX)
            .add(POST_COLLISION)
            .add(SIDE_COLLISION)
            .add(VoxelHelper::mirrorZ)
            .add(VoxelHelper::rotateY)
            .add(VoxelHelper::mirrorX)
            .build();
    }
}
