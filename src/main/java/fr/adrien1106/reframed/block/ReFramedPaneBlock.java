package fr.adrien1106.reframed.block;

import fr.adrien1106.reframed.util.VoxelHelper;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.tags.BlockTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.level.BlockGetter;

public class ReFramedPaneBlock extends ConnectingReFramedBlock {

    public static final VoxelShape[] PANE_VOXELS;

    public ReFramedPaneBlock(Properties settings) {
        super(settings);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        VoxelShape shape = PANE_VOXELS[0];
        for (Direction dir: Direction.Plane.HORIZONTAL) {
            if (state.getValue(getConnectionProperty(dir)))
                shape = Shapes.or(shape, PANE_VOXELS[dir.ordinal() - 1]);
        }
        return shape;
    }

    @Override
    protected boolean connectsTo(BlockState state, boolean fs, Direction dir) {
        return !isExceptionForConnection(state) && fs || state.getBlock() instanceof IronBarsBlock || state.is(BlockTags.WALLS);
    }

    static {
        VoxelShape POST = box(7, 0, 7, 9, 16, 9);
        VoxelShape SIDE = box(7, 0, 0, 9, 16, 7);
        PANE_VOXELS = VoxelHelper.VoxelListBuilder.create(POST, 5)
            .add(SIDE)
            .add(VoxelHelper::mirrorZ)
            .add(VoxelHelper::rotateY)
            .add(VoxelHelper::mirrorX)
            .build();
    }
}
