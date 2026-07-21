// TODO(Ravel): Failed to fully resolve file: null cannot be cast to non-null type com.intellij.psi.PsiJavaCodeReferenceElement
package fr.adrien1106.reframed.util.blocks;

import fr.adrien1106.reframed.block.ReFramedEntity;
import fr.adrien1106.reframed.block.ReFramedStairBlock;
import fr.adrien1106.reframed.block.ReFramedStairsCubeBlock;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.fabricmc.fabric.mixin.blockview.BlockViewMixin;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.player.Player;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static fr.adrien1106.reframed.util.blocks.BlockProperties.EDGE;
import static fr.adrien1106.reframed.util.blocks.BlockProperties.LIGHT;
import static fr.adrien1106.reframed.util.blocks.StairShape.*;

public class BlockHelper {

    public static Corner getPlacementCorner(BlockPlaceContext ctx) {
        Direction side = ctx.getClickedFace().getOpposite();
        Vec3 pos = getHitPos(ctx.getClickLocation(), ctx.getClickedPos());
        Tuple<Direction, Direction> sides = getHitSides(pos, side);

        return Corner.getByDirections(side, sides.getA(), sides.getB());
    }

    private static Tuple<Direction, Direction> getHitSides(Vec3 pos, Direction side) {
        Iterator<Direction.Axis> axes = Stream.of(Direction.Axis.values())
            .filter(axis -> !axis.equals(side.getAxis())).iterator();
        return new Tuple<>(getHitDirection(axes.next(), pos), getHitDirection(axes.next(), pos));
    }

    public static Edge getPlacementEdge(BlockPlaceContext ctx) {
        Direction side = ctx.getClickedFace().getOpposite();
        Vec3 pos = getHitPos(ctx.getClickLocation(), ctx.getClickedPos());
        Direction.Axis axis = getHitAxis(pos, side);

        Direction part_direction = getHitDirection(axis, pos);

        return Edge.getByDirections(side, part_direction);
    }

    public static Direction.Axis getHitAxis(Vec3 pos, Direction side) {
        Stream<Direction.Axis> axes = Stream.of(Direction.Axis.values()).filter(axis -> !axis.equals(side.getAxis()));
        return axes.reduce((axis_1, axis_2) ->
            Math.abs(axis_1.choose(pos.x, pos.y, pos.z)) > Math.abs(axis_2.choose(pos.x, pos.y, pos.z))
                ? axis_1
                : axis_2
        ).orElse(null);
    }

    public static Direction getHitDirection(Direction.Axis axis, Vec3 pos) {
        return Direction.fromAxisAndDirection(
            axis,
            axis.choose(pos.x, pos.y, pos.z) > 0
                ? Direction.AxisDirection.POSITIVE
                : Direction.AxisDirection.NEGATIVE
        );
    }

    public static Vec3 getRelativePos(Vec3 pos, BlockPos block_pos) {
        return new Vec3(
            pos.x() - block_pos.getX(),
            pos.y() - block_pos.getY(),
            pos.z() - block_pos.getZ()
        );
    }

    public static Vec3 getHitPos(Vec3 pos, BlockPos block_pos) {
        pos = getRelativePos(pos, block_pos);
        return new Vec3(
            pos.x() - .5d,
            pos.y() - .5d,
            pos.z() - .5d
        );
    }

    public static StairShape getStairsShape(Edge face, BlockGetter world, BlockPos pos) {
        StairShape shape = STRAIGHT;

        String sol = getNeighborPos(face, face.getFirstDirection(), true, face.getSecondDirection(), world, pos);
        switch (sol) {
            case "right": return INNER_RIGHT;
            case "left": return INNER_LEFT;
        }

        sol = getNeighborPos(face, face.getSecondDirection(), true, face.getFirstDirection(), world, pos);
        switch (sol) {
            case "right": return INNER_RIGHT;
            case "left": return INNER_LEFT;
        }

        sol = getNeighborPos(face, face.getFirstDirection(), false, face.getSecondDirection(), world, pos);
        switch (sol) {
            case "right" -> shape = FIRST_OUTER_RIGHT;
            case "left" -> shape = FIRST_OUTER_LEFT;
        }

        sol = getNeighborPos(face, face.getSecondDirection(), false, face.getFirstDirection(), world, pos);
        switch (sol) {
            case "right" -> {
                if (shape.equals(STRAIGHT)) shape = SECOND_OUTER_RIGHT;
                else if (shape.equals(FIRST_OUTER_RIGHT)) shape = OUTER_RIGHT;
            }
            case "left" -> {
                if (shape.equals(STRAIGHT)) shape = SECOND_OUTER_LEFT;
                else if (shape.equals(FIRST_OUTER_LEFT)) shape = OUTER_LEFT;
            }
        }

        return shape;
    }

    public static String getNeighborPos(Edge edge, Direction direction, Boolean reverse, Direction reference, BlockGetter world, BlockPos pos) {
        BlockState block_state = world.getBlockState(
            pos.offset((reverse ? direction.getOpposite() : direction).getNormal())
        );

        if (isStair(block_state) && block_state.getValue(EDGE).hasDirection(reference)) {
            if (block_state.getValue(EDGE).hasDirection(edge.getLeftDirection())) return "left";
            else if (block_state.getValue(EDGE).hasDirection(edge.getRightDirection())) return "right";
        }
        return "";
    }

    public static boolean isStair(BlockState state) {
        return state.getBlock() instanceof ReFramedStairBlock
            || state.getBlock() instanceof ReFramedStairsCubeBlock;
    }

    public static InteractionResult useCamo(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit, int theme_index) {
        if(!(world.getBlockEntity(pos) instanceof ReFramedEntity block_entity)) return InteractionResult.PASS;


        ItemStack held = player.getItemInHand(hand);
        // Changing the theme
        if(held.getItem() instanceof BlockItem block_item && block_entity.getTheme(theme_index).getBlock() == Blocks.AIR) {
            Block block = block_item.getBlock();
            BlockPlaceContext ctx = new BlockPlaceContext(new UseOnContext(player, hand, hit));
            BlockState placement_state = block.getStateForPlacement(ctx);
            if(placement_state != null && Block.isShapeFullBlock(placement_state.getCollisionShape(world, pos)) && !(block instanceof BlockApiLookup.BlockEntityApiProvider<?,?>)) {
                List<BlockState> themes = block_entity.getThemes();
                if(!world.isClientSide()) block_entity.setTheme(placement_state, theme_index);

                // check for default light emission
                if (placement_state.getLightEmission() > 0
                    && themes.stream().noneMatch(theme -> theme.getLightEmission() > 0)
                    && !block_entity.emitsLight()
                )
                    block_entity.toggleLight();

                world.setBlock(pos, state.setValue(LIGHT, block_entity.emitsLight()), 0);

                // check for default redstone emission
                if (placement_state.getSignal(world, pos, Direction.NORTH) > 0
                    && themes.stream().noneMatch(theme -> theme.getSignal(world, pos, Direction.NORTH) > 0)
                    && !block_entity.emitsRedstone()
                ) block_entity.toggleRedstone();
                if(!player.isCreative()) held.setCount(held.getCount() - 1);
                world.playSound(player, pos, placement_state.getSoundType().getPlaceSound(), SoundSource.BLOCKS, 1f, 1.1f);
                return InteractionResult.SUCCESS;
            }
        }

        return InteractionResult.PASS;
    }

    public static InteractionResult useUpgrade(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand) {
        if(!(world.getBlockEntity(pos) instanceof ReFramedEntity block_entity)) return InteractionResult.PASS;

        ItemStack held = player.getItemInHand(hand);

        // frame will emit light if applied with glowstone
        if(state.hasProperty(LIGHT) && held.getItem() == Items.GLOWSTONE_DUST) {
            block_entity.toggleLight();
            world.setBlock(pos, state.setValue(LIGHT, block_entity.emitsLight()), 1);
            world.playSound(player, pos, SoundEvents.GLASS_HIT, SoundSource.BLOCKS, 1f, 1f);
            return InteractionResult.SUCCESS;
        }

        // frame will emit redstone if applied with redstone torch can deactivate redstone block camo emission
        if(held.getItem() == Items.REDSTONE_TORCH) {
            block_entity.toggleRedstone();
            world.playSound(player, pos, SoundEvents.LEVER_CLICK, SoundSource.BLOCKS, 1f, 1f);
            return InteractionResult.SUCCESS;
        }

        // Frame will lose its collision if applied with popped chorus fruit
        if(held.getItem() == Items.POPPED_CHORUS_FRUIT) {
            block_entity.toggleSolidity();
            world.playSound(player, pos, SoundEvents.CHORUS_FRUIT_TELEPORT, SoundSource.BLOCKS, 1f, 1f);
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    public static boolean cursorMatchesFace(VoxelShape shape, Vec3 pos) {
        Map<Direction.Axis, Double> axes = Arrays.stream(Direction.Axis.values())
            .collect(Collectors.toMap(
                x -> x,
                x -> x.choose(pos.x(), pos.y(), pos.z())
            ));

        return shape.toAabbs().stream()
            .anyMatch(box ->
                axes.keySet().stream()
                    .map(x -> box.min(x) <= axes.get(x) && box.min(x) >= axes.get(x))
                    .reduce((prev, current) -> prev && current).orElse(false)
            );
    }
}
