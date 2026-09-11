package xyz.jptrzy.infusion_table.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexConsumers;
import net.minecraft.client.render.block.BlockModelRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.entity.EntityRenderers;
import net.minecraft.client.render.entity.model.BookModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import xyz.jptrzy.infusion_table.InfusionTable;
import xyz.jptrzy.infusion_table.block.entity.InfusionTableBlockEntity;

public class InfusionTableBlockEntityRenderer implements BlockEntityRenderer<InfusionTableBlockEntity> {
    public static final SpriteIdentifier BOOK_TEXTURE;
    private final BookModel book_model;

    public InfusionTableBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        this.book_model = new BookModel(ctx.getLayerModelPart(EntityModelLayers.BOOK));
    }

    @Override
    public void render(InfusionTableBlockEntity entity, float tickProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, Vec3d cameraPos) {
        if (entity.book.isEmpty()) return;

        matrices.push();

        matrices.translate(0.5, 0.75, 0.5);
        matrices.translate(0.0D, (double)(0.1F + MathHelper.sin((entity.getWorld().getTime() + tickProgress) * 0.1F) * 0.01F), 0.0D);

        matrices.multiply(RotationAxis.POSITIVE_Y.rotation( -(entity.bookLastRot + InfusionTable.aroundRadial(entity.bookRot - entity.bookLastRot) * tickProgress) ));
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(80.0F));

        VertexConsumer vertexConsumer = BOOK_TEXTURE.getVertexConsumer(vertexConsumers, RenderLayer::getEntitySolid);
        vertexConsumer = getBookGlintConsumer(vertexConsumers, vertexConsumer, entity.book.hasGlint());

        book_model.setPageAngles(1, 0, 0, entity.bookLastOpenAngle + (entity.bookOpenAngle - entity.bookLastOpenAngle) * tickProgress );
        book_model.render(matrices, vertexConsumer, light, overlay);

        matrices.pop();

        if(entity.bookOpenAngle >= 1 && !entity.item.isEmpty()){
            matrices.push();

            matrices.translate(0.5, 1.2, 0.5);

            // Rotate the item
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((entity.getWorld().getTime() + tickProgress) * 2));

            MinecraftClient.getInstance().getItemRenderer().renderItem(entity.item, ItemDisplayContext.GROUND, light, overlay, matrices, vertexConsumers, entity.getWorld(), 0);

            matrices.pop();
        }
    }

    public static VertexConsumer getBookGlintConsumer(VertexConsumerProvider vertexConsumers, VertexConsumer vertexConsumer, boolean glint) {
        if (!glint) return vertexConsumer;

        return VertexConsumers.union(vertexConsumers.getBuffer(RenderLayer.getEntityGlint()), vertexConsumer);
    }

    static {
        BOOK_TEXTURE = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, Identifier.of("entity/enchanting_table_book"));
    }


}
