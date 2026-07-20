package fr.adrien1106.reframed.block;

import fr.adrien1106.reframed.ReFramed;
import fr.adrien1106.reframed.util.VoxelHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.BlockGetter;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.*;

public class ReFramedHalfSlabBlock extends ReFramedSlabBlock {

    public static VoxelShape[] HALF_SLAB_SHAPES;

	public ReFramedHalfSlabBlock(Properties settings) {
		super(settings);
	}

	@Override
	public boolean canBeReplaced(BlockState state, BlockPlaceContext context) {
        if (context.getPlayer() == null
            || context.getPlayer().isShiftKeyDown()
            || !(context.getItemInHand().getItem() instanceof BlockItem block_item)
        ) return false;

        // allow replacing with slab, step, small cube and half stair
        Block block = block_item.getBlock();
        if (block != this) return false;

        // check if the player is clicking on the inner part of the block
		return ReFramed.HALF_SLABS_SLAB
            .matchesShape(
                context.getClickLocation(),
                context.getClickedPos(),
                ReFramed.HALF_SLABS_SLAB.defaultBlockState().setValue(FACING, state.getValue(FACING)),
                2
            );
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext ctx) {
		BlockState current_state = ctx.getLevel().getBlockState(ctx.getClickedPos());

		if (current_state.is(this))
			return ReFramed.HALF_SLABS_SLAB.defaultBlockState()
				.setValue(FACING, current_state.getValue(FACING))
                .setValue(WATERLOGGED, current_state.getValue(WATERLOGGED));

		return super.getStateForPlacement(ctx).setValue(FACING, ctx.getClickedFace().getOpposite());
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
		return getHalfSlabShape(state.getValue(FACING));
	}

    public static VoxelShape getHalfSlabShape(Direction direction) {
        return HALF_SLAB_SHAPES[direction.get3DDataValue()];
    }

    @Override
	public Map<Integer, Integer> getThemeMap(BlockState state, BlockState new_state) {
        if (new_state.is(ReFramed.HALF_SLABS_SLAB)) return Map.of(1, 1);
		return super.getThemeMap(state, new_state);
	}

    static {
        HALF_SLAB_SHAPES = VoxelHelper.VoxelListBuilder.create(box(0, 0, 0, 16, 4, 16),6)
            .add(box(0, 12, 0, 16, 16, 16))
            .add(box(0, 0, 0, 16, 16, 4))
            .add(box(0, 0, 12, 16, 16, 16))
            .add(box(0, 0, 0, 4, 16, 16))
            .add(box(12, 0, 0, 16, 16, 16))
            .build();
    }
}
