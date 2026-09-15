package xyz.jptrzy.infusion_table.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.object.book.BookModel;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.sprite.SpriteGetter;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;
import xyz.jptrzy.infusion_table.InfusionTable;
import xyz.jptrzy.infusion_table.block.entity.InfusionTableBlockEntity;

public class InfusionTableBlockEntityRenderer implements BlockEntityRenderer<InfusionTableBlockEntity, InfusionTableBlockEntityRenderState> {
    public static final SpriteId BOOK_TEXTURE;
    private final SpriteGetter sprites;
    private final BookModel bookModel;

    public InfusionTableBlockEntityRenderer(BlockEntityRendererProvider.Context ctx) {
        this.sprites = ctx.sprites();
        this.bookModel = new BookModel(ctx.bakeLayer(ModelLayers.BOOK));
    }

    @Override
    public InfusionTableBlockEntityRenderState createRenderState() {
        return new InfusionTableBlockEntityRenderState();
    }

    @Override
    public void extractRenderState(InfusionTableBlockEntity blockEntity, InfusionTableBlockEntityRenderState state, float partialTicks, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);

        state.bookGlint = blockEntity.book.hasFoil();
        state.showBook = blockEntity.book.isEmpty();
        state.bookHeight = Mth.sin((blockEntity.getLevel().getGameTime() + partialTicks) * 0.1F);
        state.bookAngle = Mth.rotLerpRad(partialTicks, blockEntity.bookLastRot, blockEntity.bookRot);
        state.bookOpenAngle = Mth.lerp(partialTicks, blockEntity.bookLastOpenAngle, blockEntity.bookOpenAngle);

        ItemModelResolver resolver = Minecraft.getInstance().getItemModelResolver();

        resolver.appendItemLayers(
                state.itemStackRenderState,
                blockEntity.item,
                ItemDisplayContext.GROUND,
                blockEntity.getLevel(),
                null,
                0
        );

        state.item = blockEntity.item;
        state.itemAngle = (blockEntity.getLevel().getGameTime() + partialTicks) * 2;
    }

    @Override
    public void submit(InfusionTableBlockEntityRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        if (state.showBook) return;

        poseStack.pushPose();

        poseStack.translate(0.5, 0.75, 0.5);
        poseStack.translate(0.0D, (double)(0.1F + state.bookHeight * 0.01F), 0.0D);

        poseStack.mulPose(Axis.YP.rotation((float) -state.bookAngle));
        poseStack.mulPose(Axis.ZP.rotationDegrees(80.0F));

        BookModel.State bookModelState = BookModel.State.forAnimation(1, 0, 0, (float) state.bookOpenAngle);
        bookModel.setupAnim(bookModelState);
        bookModel.root().getAllParts().forEach(part -> {
            submitNodeCollector.submitModelPart(
                    part, poseStack,
                    BOOK_TEXTURE.renderType(RenderTypes::entitySolid),
                    state.lightCoords, OverlayTexture.NO_OVERLAY,
                    this.sprites.get(BOOK_TEXTURE), false, state.bookGlint);
        });

        poseStack.popPose();

        if(state.bookOpenAngle >= 1 && !state.item.isEmpty()){
            poseStack.pushPose();

            poseStack.translate(0.5, 1.2, 0.5);
            poseStack.mulPose(Axis.YP.rotationDegrees((float) state.itemAngle));


            state.itemStackRenderState.submit(
                    poseStack, submitNodeCollector,
                    state.lightCoords, OverlayTexture.NO_OVERLAY,
                    0);

            poseStack.popPose();
        }
    }

    static {
        BOOK_TEXTURE = Sheets.BLOCK_ENTITIES_MAPPER.defaultNamespaceApply("enchantment/enchanting_table_book");
    }
}
