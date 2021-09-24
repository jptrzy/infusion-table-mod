package net.jptrzy.infusion.table.blocks;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3f;

@Environment(EnvType.CLIENT)
public class InfusionTableEntityRenderer implements BlockEntityRenderer<InfusionTableEntity> {

    public InfusionTableEntityRenderer(BlockEntityRendererFactory.Context ctx) {
    }

    @Override
    public void render(InfusionTableEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        matrices.push();

        matrices.translate(0.5, 1, 0.5);

        // Rotate the item
        matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion((entity.getWorld().getTime() + tickDelta) * 2));

        MinecraftClient.getInstance().getItemRenderer().renderItem(entity.getItem(), ModelTransformation.Mode.GROUND, light, overlay, matrices, vertexConsumers, 0);

        matrices.pop();
    }
}
