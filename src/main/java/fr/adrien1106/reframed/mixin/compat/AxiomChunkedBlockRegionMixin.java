package fr.adrien1106.reframed.mixin.compat;

import com.llamalad7.mixinextras.sugar.Local;
import com.moulberry.axiom.render.regions.ChunkedBlockRegion;
import com.moulberry.axiom.utils.IntMatrix;
import com.moulberry.axiom.world_modification.CompressedBlockEntity;
import fr.adrien1106.reframed.client.model.MultiRetexturableModel;
import fr.adrien1106.reframed.client.model.RetexturingBakedModel;
import fr.adrien1106.reframed.util.DefaultList;
import fr.adrien1106.reframed.util.mixin.IAxiomChunkedBlockRegionMixin;
import fr.adrien1106.reframed.util.mixin.IMultipartBakedModelMixin;
import fr.adrien1106.reframed.util.mixin.ThemedBlockEntity;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import com.mojang.blaze3d.vertex.BufferBuilder;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.resources.model.BakedModel;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.stream.Stream;

import static fr.adrien1106.reframed.block.ReFramedEntity.BLOCKSTATE_KEY;

@Mixin(ChunkedBlockRegion.class) // TODO: Look here for better rotation/flip support
public abstract class AxiomChunkedBlockRegionMixin implements IAxiomChunkedBlockRegionMixin {

    @Shadow public abstract BlockState getBlockState(BlockPos pos);

    @Shadow public abstract @Nullable BlockEntity getBlockEntity(BlockPos pos);

    @Unique
    private IntMatrix transform;
    @Unique
    private IntMatrix inverse_transform;
    @Unique
    private Long2ObjectMap<CompressedBlockEntity> block_entities;

    @Unique
    private static boolean isFrameModel(BakedModel model) {
        return model instanceof RetexturingBakedModel || model instanceof MultiRetexturableModel;
    }

    @Unique
    private static List<BakedModel> getModels(BakedModel model, BlockState state) {
        if (isFrameModel(model))
            return List.of(model);
        else if (model instanceof IMultipartBakedModelMixin mpm)
            return mpm.getModels(state).stream().filter(AxiomChunkedBlockRegionMixin::isFrameModel).toList();
        else
            return List.of();
    }

    @Inject(
        method = "renderBlock",
        at = @At(
            value = "INVOKE_ASSIGN",
            target = "Lnet/minecraft/client/renderer/block/BlockRenderDispatcher;getBlockModel(Lnet/minecraft/world/level/block/state/BlockState;)Lnet/minecraft/client/resources/model/BakedModel;",
            shift = At.Shift.AFTER
        ),
        cancellable = true)
    private static void onRenderBlock(BufferBuilder blockBuilder, BlockRenderDispatcher renderer, BlockPos.MutableBlockPos pos, RandomSource rand, PoseStack matrices, BlockAndTintGetter world, Matrix4f currentPoseMatrix, Matrix4f basePoseMatrix, int x, int y, int z, BlockState state, boolean useAmbientOcclusion, CallbackInfo ci, @Local BakedModel model) {
        List<BakedModel> models;
        if ((models = getModels(model, state)).isEmpty()) return;

        DefaultList<BlockState> themes = new DefaultList<>(
            Blocks.AIR.defaultBlockState(),
            world.getBlockEntity(pos) instanceof ThemedBlockEntity themed
                ? themed.getThemes()
                : List.of()
        );
        models.stream().flatMap(m -> m instanceof MultiRetexturableModel mm
            ? mm.models().stream()
            : Stream.of((RetexturingBakedModel)m)
        ).forEach(m -> {
            m.setCamo(world, themes.get(m.getThemeIndex() - 1), pos);
            if (useAmbientOcclusion && state.getLightEmission() == 0 && m.useAmbientOcclusion()) renderer.getModelRenderer()
                    .tesselateWithAO(world, m, state, pos, matrices, blockBuilder, true, rand, state.getSeed(pos), OverlayTexture.NO_OVERLAY);
            else renderer.getModelRenderer()
                    .tesselateWithoutAO(world, m, state, pos, matrices, blockBuilder, true, rand, state.getSeed(pos), OverlayTexture.NO_OVERLAY);
        });
        ci.cancel();
    }

    @Inject(
        method = "getBlockEntity",
        at = @At("HEAD"),
        cancellable = true
    )
    private void onGetBlockEntity(BlockPos pos, CallbackInfoReturnable<BlockEntity> cir) {
        if (inverse_transform == null || block_entities == null) return;
        long key = BlockPos.asLong(
            inverse_transform.transformX(pos.getX(), pos.getY(), pos.getZ()),
            inverse_transform.transformY(pos.getX(), pos.getY(), pos.getZ()),
            inverse_transform.transformZ(pos.getX(), pos.getY(), pos.getZ())
        );
        CompoundTag compound;
        if (!block_entities.containsKey(key)
            || !(compound = block_entities.get(key).decompress()).contains(BLOCKSTATE_KEY + 1)
        ) return;
        cir.setReturnValue(new ThemedBlockEntity(compound, pos, getBlockState(pos)));
    }

    @Inject(
        method = "uploadDirty",
        at = @At("HEAD")
    )
    private void onUploadDirty(Camera camera, Vec3 translation, boolean canResort, boolean canUseAmbientOcclusion, CallbackInfo ci) {
        if (transform == null) inverse_transform = new IntMatrix();
        else inverse_transform = transform.copy();
        inverse_transform.invert();
    }

    @Inject(
        method = "flip",
        at = @At("RETURN")
    )
    private void onFlip(Direction.Axis axis, CallbackInfoReturnable<ChunkedBlockRegion> cir) {
        ((IAxiomChunkedBlockRegionMixin) cir.getReturnValue()).setTransform(transform, block_entities);
    }

    @Inject(
        method = "rotate",
        at = @At("RETURN")
    )
    private void onRotate(Direction.Axis axis, int count, CallbackInfoReturnable<ChunkedBlockRegion> cir) {
        ((IAxiomChunkedBlockRegionMixin) cir.getReturnValue()).setTransform(transform, block_entities);
    }

    @Override
    public void setTransform(IntMatrix transform, Long2ObjectMap<CompressedBlockEntity> block_entities) {
        this.transform = transform;
        this.block_entities = block_entities;
    }

    @Override
    public IntMatrix getTransform() {
        return transform;
    }

    @Override
    public Long2ObjectMap<CompressedBlockEntity> getBlockEntities() {
        return block_entities;
    }


}
