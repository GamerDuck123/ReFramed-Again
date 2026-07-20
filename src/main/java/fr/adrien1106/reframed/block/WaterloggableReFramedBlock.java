package fr.adrien1106.reframed.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import org.jetbrains.annotations.Nullable;

public class WaterloggableReFramedBlock extends ReFramedBlock implements SimpleWaterloggedBlock {
	public WaterloggableReFramedBlock(Properties settings) {
		super(settings);
		registerDefaultState(defaultBlockState().setValue(BlockStateProperties.WATERLOGGED, false));
	}
	
	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder.add(BlockStateProperties.WATERLOGGED));
	}

	@Override
	@SuppressWarnings("deprecation")
	public boolean useShapeForLightOcclusion(BlockState state) {
		return true;
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext ctx) {
		BlockState sup = super.getStateForPlacement(ctx);
		if(sup != null) sup = sup.setValue(BlockStateProperties.WATERLOGGED, ctx.getLevel().getFluidState(ctx.getClickedPos()).is(Fluids.WATER));
		return sup;
	}
	
	@Override
    @SuppressWarnings("deprecation")
	public FluidState getFluidState(BlockState state) {
		return state.getValue(BlockStateProperties.WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}

    @Override
    @SuppressWarnings("deprecation")
	public BlockState updateShape(BlockState state, Direction direction, BlockState otherState, LevelAccessor world, BlockPos pos, BlockPos moved) {
		if(state.getValue(BlockStateProperties.WATERLOGGED)) world.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
		return super.updateShape(state, direction, otherState, world, pos, moved);
	}
}
