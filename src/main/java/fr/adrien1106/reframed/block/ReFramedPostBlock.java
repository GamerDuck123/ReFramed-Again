package fr.adrien1106.reframed.block;

import fr.adrien1106.reframed.util.VoxelHelper;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.BlockGetter;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.AXIS;

public class ReFramedPostBlock extends PillarReFramedBlock {

    public static final VoxelShape[] POST_VOXELS;

    public ReFramedPostBlock(Properties settings) {
        super(settings);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return getPillarShape(state.getValue(AXIS));
    }

    public static VoxelShape getPillarShape(Direction.Axis axis) {
        return POST_VOXELS[axis.ordinal()];
    }

    static {
        final VoxelShape POST = box(0, 6, 6, 16, 10, 10);
        POST_VOXELS = VoxelHelper.VoxelListBuilder.create(POST, 3)
            .add(VoxelHelper::rotateZ)
            .add(VoxelHelper::rotateX)
            .build();
    }
}
