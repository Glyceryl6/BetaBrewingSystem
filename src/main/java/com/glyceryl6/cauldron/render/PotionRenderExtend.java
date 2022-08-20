package com.glyceryl6.cauldron.render;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

@MethodsReturnNonnullByDefault
public class PotionRenderExtend extends RenderSnowball<EntityPotion> {

    public PotionRenderExtend(RenderManager renderManager, RenderItem renderItem) {
        super(renderManager, Items.POTIONITEM, renderItem);
    }

    @Override
    public ItemStack getStackToRender(EntityPotion potion) {
        return potion.getDataManager().get(EntityPotion.ITEM);
    }

}