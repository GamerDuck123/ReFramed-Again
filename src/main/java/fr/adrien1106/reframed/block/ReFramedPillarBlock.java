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

public class ReFramedPillarBlock extends PillarReFramedBlock {

    public static final VoxelShape[] PILLAR_VOXELS;

    public ReFramedPillarBlock(Properties settings) {
        super(settings);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return getPillarShape(state.getValue(AXIS));
    }

    public static VoxelShape getPillarShape(Direction.Axis axis) {
        return PILLAR_VOXELS[axis.ordinal()];
    }

    static {
        final VoxelShape PILLAR = box(0, 4, 4, 16, 12, 12);
        PILLAR_VOXELS = VoxelHelper.VoxelListBuilder.create(PILLAR, 3)
            .add(VoxelHelper::rotateZ)
            .add(VoxelHelper::rotateX)
            .build();
    }
}
