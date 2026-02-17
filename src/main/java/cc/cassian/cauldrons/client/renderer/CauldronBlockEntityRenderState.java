package cc.cassian.cauldrons.client.renderer;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;

import java.util.ArrayList;
import java.util.List;

public class CauldronBlockEntityRenderState extends BlockEntityRenderState {
    public List<ItemStackRenderState> items = new ArrayList<>();
}
