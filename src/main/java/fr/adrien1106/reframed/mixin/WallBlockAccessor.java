package fr.adrien1106.reframed.mixin;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

//Used in ReFramedWallBlock, since the vanilla wall block code explodes if you add more blockstates.
@Mixin(WallBlock.class)
public interface WallBlockAccessor {
	@Accessor("shapeByIndex") Map<BlockState, VoxelShape> getShapeByIndex();
	@Accessor("collisionShapeByIndex") Map<BlockState, VoxelShape> getCollisionShapeByIndex();
}
