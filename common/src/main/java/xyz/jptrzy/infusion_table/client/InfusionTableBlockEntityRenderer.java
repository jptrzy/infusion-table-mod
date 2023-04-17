package xyz.jptrzy.infusion_table.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexConsumers;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.entity.model.BookModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import xyz.jptrzy.infusion_table.block.entity.InfusionTableBlockEntity;

public class InfusionTableBlockEntityRenderer implements BlockEntityRenderer<InfusionTableBlockEntity> {
    public static final SpriteIdentifier BOOK_TEXTURE;
    private final BookModel book_model;

    public InfusionTableBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        this.book_model = new BookModel(ctx.getLayerModelPart(EntityModelLayers.BOOK));
    }

    @Override
    public void render(InfusionTableBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        if (entity.book.isEmpty()) {
            return;
        }

        matrices.push();

        matrices.translate(0.5, 0.75, 0.5);
        matrices.translate(0.0D, (double)(0.1F + MathHelper.sin((entity.getWorld().getTime() + tickDelta) * 0.1F) * 0.01F), 0.0D);

        matrices.multiply(Vec3f.POSITIVE_Y.getRadialQuaternion( -(entity.bookLastRot + aroundRadial(entity.bookRot - entity.bookLastRot) * tickDelta) ));
        matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(80.0F));

        VertexConsumer vertexConsumer = BOOK_TEXTURE.getVertexConsumer(vertexConsumers, RenderLayer::getEntitySolid);
        vertexConsumer = getBookGlintConsumer(vertexConsumers, vertexConsumer, !entity.book.isEnchantable());

        book_model.setPageAngles(1, 0, 0, entity.bookLastOpenAngle + (entity.bookOpenAngle - entity.bookLastOpenAngle) * tickDelta );
        book_model.renderBook(matrices, vertexConsumer, light, overlay, 1.0F, 1.0F, 1.0F, 1.0F);

        matrices.pop();
    }

    public static float aroundRadial(float angle){
        while(angle >= 3.1415927F) {
            angle -= 6.2831855F;
        }

        while(angle < -3.1415927F) {
            angle += 6.2831855F;
        }

        return angle;
    }

    static {
        BOOK_TEXTURE = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, new Identifier("entity/enchanting_table_book"));
    }

    public static VertexConsumer getBookGlintConsumer(VertexConsumerProvider vertexConsumers, VertexConsumer vertexConsumer, boolean glint) {
        if (glint) {
            return MinecraftClient.isFabulousGraphicsOrBetter() ? VertexConsumers.union(vertexConsumers.getBuffer(RenderLayer.getGlintTranslucent()), vertexConsumer) : VertexConsumers.union(vertexConsumers.getBuffer(RenderLayer.getEntityGlint()), vertexConsumer);
        } else {
            return vertexConsumer;
        }
    }
}
