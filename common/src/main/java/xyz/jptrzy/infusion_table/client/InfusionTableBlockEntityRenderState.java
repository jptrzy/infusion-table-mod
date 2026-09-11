package xyz.jptrzy.infusion_table.client;

import net.minecraft.client.render.block.entity.state.BlockEntityRenderState;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.item.ItemStack;

public class InfusionTableBlockEntityRenderState extends BlockEntityRenderState {
    public boolean showBook = false;

    public boolean bookGlint = false;
    public double bookAngle = 0.0;
    public double bookHeight = 0.0;
    public double bookOpenAngle = 0.0;

    public ItemStack item = ItemStack.EMPTY;
    public double itemAngle = 0.0;

    ItemRenderState itemRenderState = new ItemRenderState();
}
