package fr.adrien1106.reframed.block;

import fr.adrien1106.reframed.ReFramed;
import fr.adrien1106.reframed.util.VoxelHelper;
import fr.adrien1106.reframed.util.blocks.BlockHelper;
import fr.adrien1106.reframed.util.blocks.Edge;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.VoxelShape;
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
import static fr.adrien1106.reframed.util.blocks.BlockProperties.EDGE;
import static fr.adrien1106.reframed.util.blocks.BlockProperties.EDGE_FACE;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.*;

public class ReFramedHalfLayerBlock extends LayeredReFramedBlock {

    public static final VoxelShape[] HALF_LAYER_VOXELS;

    public ReFramedHalfLayerBlock(Properties settings) {
        super(settings);
        registerDefaultState(defaultBlockState().setValue(EDGE, Edge.NORTH_DOWN).setValue(EDGE_FACE, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(EDGE, EDGE_FACE));
    }

    @Override
    public boolean canBeReplaced(BlockState state, BlockPlaceContext context) {
        if (super.canBeReplaced(state, context)) return true;

        if (context.getPlayer() == null
            || context.getPlayer().isShiftKeyDown()
            || !(context.getItemInHand().getItem() instanceof BlockItem block_item)
        ) return false;

        Edge edge = state.getValue(EDGE);
        Direction face = edge.getDirection(state.getValue(EDGE_FACE));
        if (block_item.getBlock() == ReFramed.SLAB)
            return ReFramed.SLAB
                .matchesShape(
                    context.getClickLocation(),
                    context.getClickedPos(),
                    ReFramed.SLAB.defaultBlockState().setValue(FACING, edge.getOtherDirection(face).getOpposite())
                );

        if (block_item.getBlock() == ReFramed.STEP)
            return ReFramed.STEP
                .matchesShape(
                    context.getClickLocation(),
                    context.getClickedPos(),
                    ReFramed.STEP.defaultBlockState().setValue(EDGE, edge.getOpposite(face))
                );

        return false;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return getHalfLayerShape(
            state.getValue(EDGE),
            state.getValue(EDGE_FACE),
            state.getValue(LAYERS)
        );
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext ctx) {
        BlockState previous = ctx.getLevel().getBlockState(ctx.getClickedPos());
        BlockState state = super.getStateForPlacement(ctx);

        if (previous.is(this))
            return state;

        if (previous.is(ReFramed.SLABS_HALF_LAYER) || previous.is(ReFramed.STEPS_HALF_LAYER))
            return previous.setValue(LAYERS, Math.min(8, previous.getValue(LAYERS) + 1));

        if (previous.is(ReFramed.SLAB)) {
            Direction face = previous.getValue(FACING);
            Edge edge;
            if (face.getAxis() == ctx.getClickedFace().getAxis()) {
                edge = BlockHelper.getPlacementEdge(ctx);
                if (face == ctx.getClickedFace()) edge = edge.getOpposite(edge.getOtherDirection(ctx.getClickedFace()));
            } else edge = Edge.getByDirections(face, ctx.getClickedFace().getOpposite());

            return ReFramed.SLABS_HALF_LAYER.defaultBlockState()
                .setValue(EDGE, edge)
                .setValue(EDGE_FACE, edge.getDirectionIndex(face))
                .setValue(WATERLOGGED, previous.getValue(WATERLOGGED));
        }

        if (previous.is(ReFramed.STEP)) {
            int face_index = 0;
            Edge edge = previous.getValue(EDGE);
            if (!ReFramed.STEP.matchesShape(
                ctx.getClickLocation(),
                ctx.getClickedPos(),
                ReFramed.STEP.defaultBlockState().setValue(EDGE, edge.getOpposite(1))
            )) face_index = 1;
            return ReFramed.STEPS_HALF_LAYER.defaultBlockState()
                .setValue(EDGE, edge)
                .setValue(EDGE_FACE, face_index)
                .setValue(WATERLOGGED, previous.getValue(WATERLOGGED));
        }

        Edge edge = BlockHelper.getPlacementEdge(ctx);
        return state.setValue(EDGE, edge).setValue(EDGE_FACE, edge.getDirectionIndex(ctx.getClickedFace().getOpposite()));
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState rotate(BlockState state, Rotation rotation) {
        Edge edge = state.getValue(EDGE);
        Direction face = rotation.rotate(edge.getDirection(state.getValue(EDGE_FACE)));
        edge = edge.rotate(rotation);
        return state.setValue(EDGE, edge).setValue(EDGE_FACE, edge.getDirectionIndex(face));
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState mirror(BlockState state, Mirror mirror) {
        Edge edge = state.getValue(EDGE);
        Direction face = mirror.mirror(edge.getDirection(state.getValue(EDGE_FACE)));
        edge = edge.mirror(mirror);
        return state.setValue(EDGE, edge).setValue(EDGE_FACE, edge.getDirectionIndex(face));
    }

    @Override
    public Map<Integer, Integer> getThemeMap(BlockState state, BlockState new_state) {
        if (new_state.is(ReFramed.SLABS_HALF_LAYER)
            || new_state.is(ReFramed.STEPS_HALF_LAYER)
        ) return Map.of(1, 2);
        return super.getThemeMap(state, new_state);
    }

    public static VoxelShape getHalfLayerShape(Edge edge, int face, int layer) {
        return HALF_LAYER_VOXELS[edge.ordinal() * 16 + face * 8 + layer - 1];
    }

    static {
        VoxelListBuilder builder = VoxelListBuilder.create(box(0, 0, 0, 16, 8, 2), 192)
            .add(box(0, 0, 0, 16, 8, 4))
            .add(box(0, 0, 0, 16, 8, 6))
            .add(box(0, 0, 0, 16, 8, 8))
            .add(box(0, 0, 0, 16, 8, 10))
            .add(box(0, 0, 0, 16, 8, 12))
            .add(box(0, 0, 0, 16, 8, 14))
            .add(box(0, 0, 0, 16, 8, 16));

        for (int i = 0; i < 8; i++) {
            builder.add(i, VoxelHelper::rotateCX, VoxelHelper::mirrorZ);
        }
        for (int i = 0; i < 48; i++) {
            builder.add(i, VoxelHelper::rotateCX);
        }
        for (int i = 0; i < 64; i++) {
            builder.add(i, VoxelHelper::rotateCY);
        }
        for (int i = 64; i < 80; i++) {
            builder.add(i, VoxelHelper::rotateX);
        }
        for (int i = 80; i < 96; i++) {
            builder.add(i, VoxelHelper::rotateX);
        }
        for (int i = 96; i < 112; i++) {
            builder.add(i, VoxelHelper::rotateX);
        }
        for (int i = 112; i < 128; i++) {
            builder.add(i, VoxelHelper::rotateX);
        }

        HALF_LAYER_VOXELS = builder.build();
    }
}
