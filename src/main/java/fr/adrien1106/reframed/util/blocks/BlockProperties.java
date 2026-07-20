package fr.adrien1106.reframed.util.blocks;

import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class BlockProperties {
    public static final BooleanProperty LIGHT = BooleanProperty.create("emits_light");
    public static final EnumProperty<Edge> EDGE = EnumProperty.create("edge", Edge.class);
    public static final IntegerProperty EDGE_FACE = IntegerProperty.create("face", 0, 1);
    public static final EnumProperty<Corner> CORNER = EnumProperty.create("corner", Corner.class);
    public static final IntegerProperty CORNER_FACE = IntegerProperty.create("face", 0, 2);
    public static final IntegerProperty CORNER_FEATURE = IntegerProperty.create("corner_feature", 0, 1);
    public static final EnumProperty<StairShape> STAIR_SHAPE = EnumProperty.create("shape", StairShape.class);
    public static final IntegerProperty HALF_LAYERS = IntegerProperty.create("layers", 1, 4);

}
