package net.jptrzy.infusion.table.blocks;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.jptrzy.infusion.table.Main;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.entity.model.BookModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;

@Environment(EnvType.CLIENT)
public class  InfusionTableEntityRenderer implements BlockEntityRenderer<InfusionTableEntity> {

    public static final SpriteIdentifier BOOK_TEXTURE;
    private final BookModel book;

    public InfusionTableEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        this.book = new BookModel(ctx.getLayerModelPart(EntityModelLayers.BOOK));
    }

    @Override
    public void render(InfusionTableEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        if(!entity.getBook().isEmpty()){
            matrices.push();

            matrices.translate(0.5, 0.75, 0.5);
            matrices.translate(0.0D, (double)(0.1F + MathHelper.sin((entity.getWorld().getTime() + tickDelta) * 0.1F) * 0.01F), 0.0D);
            
            matrices.multiply(Vec3f.POSITIVE_Y.getRadialQuaternion( -(entity.bookLastRot + Main.aroundRadial(entity.bookRot - entity.bookLastRot) * tickDelta) ));
            matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(80.0F));

            this.book.setPageAngles(1, 0, 0, entity.bookLastOpenAngle + (entity.bookOpenAngle - entity.bookLastOpenAngle) * tickDelta );

            VertexConsumer vertexConsumer = BOOK_TEXTURE.getVertexConsumer(vertexConsumers, RenderLayer::getEntitySolid);
            vertexConsumer = getBookGlintConsumer(vertexConsumers, vertexConsumer, !entity.getBook().isEnchantable());
            this.book.renderBook(matrices, vertexConsumer, light, overlay, 1.0F, 1.0F, 1.0F, 1.0F);

            matrices.pop();

            if(entity.getBookOpenAngle() >= 1 && !entity.getItem().isEmpty()){
                matrices.push();

                matrices.translate(0.5, 1.2, 0.5);

                // Rotate the item
                matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion((entity.getWorld().getTime() + tickDelta) * 2));

                MinecraftClient.getInstance().getItemRenderer().renderItem(entity.getItem(), ModelTransformation.Mode.GROUND, light, overlay, matrices, vertexConsumers, 0);

                matrices.pop();
            }
        }
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
