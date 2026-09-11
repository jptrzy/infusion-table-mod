package xyz.jptrzy.infusion_table.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.command.ModelCommandRenderer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.model.BookModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.SpriteHolder;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import xyz.jptrzy.infusion_table.InfusionTable;
import xyz.jptrzy.infusion_table.block.entity.InfusionTableBlockEntity;

public class InfusionTableBlockEntityRenderer implements BlockEntityRenderer<InfusionTableBlockEntity, InfusionTableBlockEntityRenderState> {
    public static final SpriteIdentifier BOOK_TEXTURE;
    private final SpriteHolder spriteHolder;
    private final BookModel bookModel;

    public InfusionTableBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        this.spriteHolder = ctx.spriteHolder();
        this.bookModel = new BookModel(ctx.getLayerModelPart(EntityModelLayers.BOOK));
    }

    @Override
    public InfusionTableBlockEntityRenderState createRenderState() {
        return new InfusionTableBlockEntityRenderState();
    }

    @Override
    public void updateRenderState(InfusionTableBlockEntity blockEntity, InfusionTableBlockEntityRenderState state, float tickProgress, Vec3d cameraPos, @Nullable ModelCommandRenderer.CrumblingOverlayCommand crumblingOverlay) {
        BlockEntityRenderer.super.updateRenderState(blockEntity, state, tickProgress, cameraPos, crumblingOverlay);

        state.bookGlint = blockEntity.book.hasGlint();
        state.showBook = blockEntity.book.isEmpty();
        state.bookHeight = MathHelper.sin((blockEntity.getWorld().getTime() + tickProgress) * 0.1F);
        // TODO use lerp
        state.bookAngle = MathHelper.lerpAngleRadians(tickProgress, blockEntity.bookLastRot, blockEntity.bookRot);
        state.bookOpenAngle = blockEntity.bookLastOpenAngle + (blockEntity.bookOpenAngle - blockEntity.bookLastOpenAngle) * tickProgress;

        ItemModelManager modelManager = MinecraftClient.getInstance().getItemModelManager();
        modelManager.clearAndUpdate(
                state.itemRenderState,
                blockEntity.item,
                ItemDisplayContext.GROUND,
                blockEntity.getWorld(),
                null,
                0
        );

        state.item = blockEntity.item;
        state.itemAngle = (blockEntity.getWorld().getTime() + tickProgress) * 2;
    }

    @Override
    public void render(InfusionTableBlockEntityRenderState state, MatrixStack matrices, OrderedRenderCommandQueue queue, CameraRenderState cameraState) {
        if (state.showBook) return;

        matrices.push();

        matrices.translate(0.5, 0.75, 0.5);
        matrices.translate(0.0D, (double)(0.1F + state.bookHeight * 0.01F), 0.0D);

        matrices.multiply(RotationAxis.POSITIVE_Y.rotation( (float) -state.bookAngle ));
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(80.0F));

        BookModel.BookModelState bookModelState = new BookModel.BookModelState(1, 0, 0, (float) state.bookOpenAngle);
        bookModel.setAngles(bookModelState);
        bookModel.getRootPart().traverse().forEach(part -> {
            queue.submitModelPart(
                    part, matrices,
                    BOOK_TEXTURE.getRenderLayer(RenderLayer::getEntitySolid),
                    state.lightmapCoordinates, OverlayTexture.DEFAULT_UV,
                    this.spriteHolder.getSprite(BOOK_TEXTURE), false, state.bookGlint);
        });

        matrices.pop();

        if(state.bookOpenAngle >= 1 && !state.item.isEmpty()){
            matrices.push();

            matrices.translate(0.5, 1.2, 0.5);
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((float) state.itemAngle));

            state.itemRenderState.render(
                    matrices, queue,
                    state.lightmapCoordinates, OverlayTexture.DEFAULT_UV,
                    0);

            matrices.pop();
        }
    }

    static {
        BOOK_TEXTURE = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, Identifier.of("entity/enchanting_table_book"));
    }
}
