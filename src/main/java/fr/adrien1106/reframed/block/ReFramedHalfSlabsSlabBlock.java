package fr.adrien1106.reframed.block;

import fr.adrien1106.reframed.util.VoxelHelper;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.BlockGetter;

import static fr.adrien1106.reframed.block.ReFramedHalfSlabBlock.getHalfSlabShape;
import static fr.adrien1106.reframed.block.ReFramedSlabBlock.getSlabShape;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.FACING;

public class ReFramedHalfSlabsSlabBlock extends FacingDoubleReFramedBlock {

    public static VoxelShape[] HALF_SLAB_COMP_SHAPES;

    public ReFramedHalfSlabsSlabBlock(Properties settings) {
        super(settings);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return getSlabShape(state.getValue(FACING));
    }

    @Override
    public VoxelShape getShape(BlockState state, int i) {
        Direction face = state.getValue(FACING);
        return i == 2
            ? HALF_SLAB_COMP_SHAPES[face.get3DDataValue()]
            : getHalfSlabShape(face);
    }

    static {
        HALF_SLAB_COMP_SHAPES = VoxelHelper.VoxelListBuilder.create(box(0, 4, 0, 16, 8, 16),6)
            .add(box(0, 8, 0, 16, 12, 16))
            .add(box(0, 0, 4, 16, 16, 8))
            .add(box(0, 0, 8, 16, 16, 12))
            .add(box(4, 0, 0, 8, 16, 16))
            .add(box(8, 0, 0, 12, 16, 16))
            .build();
    }
}
