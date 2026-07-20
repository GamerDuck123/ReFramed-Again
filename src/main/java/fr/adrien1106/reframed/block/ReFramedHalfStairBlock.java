package fr.adrien1106.reframed.block;

import fr.adrien1106.reframed.ReFramed;
import fr.adrien1106.reframed.util.VoxelHelper;
import fr.adrien1106.reframed.util.blocks.BlockHelper;
import fr.adrien1106.reframed.util.blocks.Corner;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.level.BlockGetter;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

// TODO(Ravel): ambiguous static import, members with name VoxelListBuilder have different new names
//
// TODO(Ravel): ambiguous static import, members with name VoxelListBuilder have different new names
//
// TODO(Ravel): ambiguous static import, members with name VoxelListBuilder have different new names
//
import static fr.adrien1106.reframed.util.VoxelHelper.VoxelListBuilder;
import static fr.adrien1106.reframed.util.blocks.BlockProperties.*;
import static fr.adrien1106.reframed.util.blocks.Corner.*;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.FACING;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.WATERLOGGED;

public class ReFramedHalfStairBlock extends WaterloggableReFramedBlock {

    public static final VoxelShape[] HALF_STAIR_VOXELS;

    public ReFramedHalfStairBlock(Properties settings) {
        super(settings);
        registerDefaultState(defaultBlockState().setValue(CORNER, NORTH_EAST_DOWN).setValue(CORNER_FACE, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(CORNER,CORNER_FACE));
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean canBeReplaced(BlockState state, BlockPlaceContext context) {
        if (context.getPlayer() == null
            || context.getPlayer().isShiftKeyDown()
            || !(context.getItemInHand().getItem() instanceof BlockItem block_item)
        ) return false;

        // allow replacing with slab, step, small cube and half stair
        Block block = block_item.getBlock();
        Corner corner = state.getValue(CORNER);
        Direction dir = corner.getDirection(state.getValue(CORNER_FACE));
        if (block == this || block == ReFramed.STEP)
            return ReFramed.HALF_STAIRS_STAIR.matchesShape(
                context.getClickLocation(),
                context.getClickedPos(),
                ReFramed.HALF_STAIRS_STAIR.defaultBlockState().setValue(EDGE, corner.getEdge(dir)),
                dir.getAxisDirection() == Direction.AxisDirection.POSITIVE ? 1 : 2
            );

        if (block == ReFramed.SMALL_CUBE)
            return ReFramed.SMALL_CUBE.matchesShape(
                context.getClickLocation(),
                context.getClickedPos(),
                ReFramed.SMALL_CUBE.defaultBlockState().setValue(CORNER, corner.change(dir))
            ) || ReFramed.SMALL_CUBE.matchesShape(
                context.getClickLocation(),
                context.getClickedPos(),
                ReFramed.SMALL_CUBE.defaultBlockState().setValue(CORNER, corner.getOpposite(dir))
            );

        if (block == ReFramed.SLAB)
            return ReFramed.SLAB.matchesShape(
                context.getClickLocation(),
                context.getClickedPos(),
                ReFramed.SLAB.defaultBlockState().setValue(FACING, dir.getOpposite())
            );

        return false;
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext ctx) {
        BlockState current_state = ctx.getLevel().getBlockState(ctx.getClickedPos());
        if (current_state.is(ReFramed.SMALL_CUBE)) {
            Corner corner = current_state.getValue(CORNER).getOpposite(ctx.getClickedFace().getOpposite());
            return ReFramed.HALF_STAIRS_SLAB.defaultBlockState()
                .setValue(CORNER, corner)
                .setValue(CORNER_FACE, corner.getDirectionIndex(ctx.getClickedFace().getOpposite()))
                .setValue(WATERLOGGED, current_state.getValue(WATERLOGGED));
        }

        if (current_state.is(this))
            return ReFramed.HALF_STAIRS_STAIR.defaultBlockState()
                .setValue(EDGE, current_state.getValue(CORNER).getEdge(current_state.getValue(CORNER).getDirection(current_state.getValue(CORNER_FACE))))
                .setValue(WATERLOGGED, current_state.getValue(WATERLOGGED));
        if (current_state.is(ReFramed.SLAB)) {
            Corner corner = BlockHelper.getPlacementCorner(ctx);
            Direction face = current_state.getValue(FACING);
            if (!corner.hasDirection(face)) corner = corner.change(face.getOpposite());
            return ReFramed.SLABS_INNER_STAIR.defaultBlockState()
                .setValue(CORNER, corner)
                .setValue(CORNER_FACE, corner.getDirectionIndex(face))
                .setValue(WATERLOGGED, current_state.getValue(WATERLOGGED));
        }

        Corner corner = BlockHelper.getPlacementCorner(ctx);
        return super.getStateForPlacement(ctx)
            .setValue(CORNER, corner)
            .setValue(CORNER_FACE, corner.getDirectionIndex(ctx.getClickedFace().getOpposite()));
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return getHalfStairShape(state.getValue(CORNER), state.getValue(CORNER_FACE));
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState rotate(BlockState state, Rotation rotation) {
        Corner corner = state.getValue(CORNER).rotate(rotation);
        Direction face = state.getValue(CORNER).getDirection(state.getValue(CORNER_FACE));
        return state.setValue(CORNER, corner).setValue(CORNER_FACE, corner.getDirectionIndex(rotation.rotate(face)));
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState mirror(BlockState state, Mirror mirror) {
        Corner corner = state.getValue(CORNER).mirror(mirror);
        Direction face = state.getValue(CORNER).getDirection(state.getValue(CORNER_FACE));
        return state.setValue(CORNER, corner).setValue(CORNER_FACE, corner.getDirectionIndex(mirror.mirror(face)));
    }

    @Override
    public Map<Integer, Integer> getThemeMap(BlockState state, BlockState new_state) {
        if (new_state.is(ReFramed.HALF_STAIRS_SLAB)
            || new_state.is(ReFramed.HALF_STAIRS_CUBE_STAIR)
            || new_state.is(ReFramed.HALF_STAIRS_STEP_STAIR)
        ) return Map.of(1, 1);
        if (new_state.is(ReFramed.SLABS_INNER_STAIR)) return Map.of(1, 2);
        if (new_state.is(ReFramed.HALF_STAIRS_STAIR))
            return Map.of(
                1,
                state.getValue(CORNER)
                    .getDirection(state.getValue(CORNER_FACE))
                    .getAxisDirection() == Direction.AxisDirection.POSITIVE ? 2 : 1
            );
        return super.getThemeMap(state, new_state);
    }

    public static VoxelShape getHalfStairShape(Corner corner, int face) {
        return HALF_STAIR_VOXELS[face + corner.getID() * 3];
    }

    static {
        final VoxelShape HALF_STAIR = Shapes.join(
            box(8, 0, 0, 16, 16, 8),
            box(0, 0, 0, 8, 8, 8),
            BooleanOp.OR
        );
        HALF_STAIR_VOXELS = VoxelListBuilder.create(HALF_STAIR, 24)
            .add(0 , VoxelHelper::rotateY, VoxelHelper::mirrorZ)
            .add(0 , VoxelHelper::rotateCX, VoxelHelper::mirrorZ)

            .add(0 , VoxelHelper::rotateY)
            .add(1 , VoxelHelper::rotateY)
            .add(2 , VoxelHelper::rotateY)

            .add(3 , VoxelHelper::rotateY)
            .add(4 , VoxelHelper::rotateY)
            .add(5 , VoxelHelper::rotateY)

            .add(6 , VoxelHelper::rotateY)
            .add(7 , VoxelHelper::rotateY)
            .add(8 , VoxelHelper::rotateY)

            .add(0 , VoxelHelper::mirrorY)
            .add(1 , VoxelHelper::mirrorY)
            .add(2 , VoxelHelper::mirrorY)

            .add(12, VoxelHelper::rotateY)
            .add(13, VoxelHelper::rotateY)
            .add(14, VoxelHelper::rotateY)

            .add(15, VoxelHelper::rotateY)
            .add(16, VoxelHelper::rotateY)
            .add(17, VoxelHelper::rotateY)

            .add(18, VoxelHelper::rotateY)
            .add(19, VoxelHelper::rotateY)
            .add(20, VoxelHelper::rotateY)
            .build();
    }
}
