package fr.adrien1106.reframed.block;

import fr.adrien1106.reframed.util.VoxelHelper;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.LeadItem;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;

public class ReFramedFenceBlock extends ConnectingReFramedBlock {

    public static final VoxelShape[] FENCE_VOXELS;

    public ReFramedFenceBlock(Properties settings) {
        super(settings);
    }

    @Override
    protected boolean connectsTo(BlockState state, boolean fs, Direction dir) {
        return fs || state.is(BlockTags.FENCES)
            || (state.getBlock() instanceof FenceGateBlock && FenceGateBlock.connectsToDirection(state, dir));
    }

    @Override
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

    @Override
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

    static {
        VoxelShape POST = box(6, 0, 6, 10, 16, 10);
        VoxelShape POST_COLLISION = box(6, 0, 6, 10, 24, 10);
        VoxelShape SIDE = Shapes.join(
            box(7, 12, 0, 9, 15, 6),
            box(7, 6, 0, 9, 9, 6),
            BooleanOp.OR
        );
        VoxelShape SIDE_COLLISION = box(7, 0, 0, 9, 24, 6);
        FENCE_VOXELS = VoxelHelper.VoxelListBuilder.create(POST, 5)
            .add(SIDE)
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
