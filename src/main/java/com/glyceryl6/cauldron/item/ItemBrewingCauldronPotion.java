package com.glyceryl6.cauldron.item;

import com.glyceryl6.cauldron.Cauldron;
import com.glyceryl6.cauldron.block.PotionCauldron;
import com.glyceryl6.cauldron.block.PotionHelperCauldron;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashMap;
import java.util.List;

public class ItemBrewingCauldronPotion extends ItemPotion {

    private final HashMap<Object, Object> a = new HashMap<>();

    public ItemBrewingCauldronPotion() {
        setMaxStackSize(1);
        setMaxDamage(0);
        setHasSubtypes(false);
    }

    public List a_(ItemStack itemStack) {
        int i = itemStack.getItemDamage();
        List list = (List)this.a.get(i);
        if (list == null) {
            list = PotionHelperCauldron.d(i);
            this.a.put(i, list);
        }
        return list;
    }

    @ParametersAreNonnullByDefault
    public ItemStack onItemUseFinish(ItemStack itemStack, World world, EntityLivingBase entityLiving) {
        if (!world.isRemote) {
            List<?> list = a_(itemStack);
            if (list != null) {
                for (Object effect : list) {
                    if (entityLiving instanceof EntityPlayer) {
                        entityLiving.addPotionEffect((PotionEffect) effect);
                    }
                }
            }
        }
        if (itemStack.stackSize <= 0) {
            return new ItemStack(Items.GLASS_BOTTLE);
        }
        if (entityLiving instanceof EntityPlayer) {
            itemStack.stackSize--;
            ((EntityPlayer) entityLiving).inventory.addItemStackToInventory(new ItemStack(Items.GLASS_BOTTLE));
        }
        return itemStack;
    }

    public String getItemStackDisplayName(ItemStack itemStack) {
        if (itemStack.getItemDamage() == 0) {
            return new TextComponentTranslation("item.emptyPotion.name").getFormattedText();
        }
        String string = PotionHelperCauldron.c(itemStack.getItemDamage());
        return new TextComponentTranslation(string).getFormattedText();
    }

    @SuppressWarnings("deprecation")
    public void addInformation(ItemStack itemStack, World world, List<String> list, ITooltipFlag bool) {
        if (itemStack.getItemDamage() == 0) return;
        List<PotionEffect> list1 = Cauldron.POTION_ITEM.a_(itemStack);
        if (list1 != null && !list1.isEmpty()) {
            for (PotionEffect effect : list1) {
                String string = new TextComponentTranslation(effect.getEffectName()).getFormattedText();
                if (effect.getAmplifier() > 0) {
                    string = string + " " + I18n.translateToLocal("potion.potency." + effect.getAmplifier()).trim();
                }
                if (effect.getDuration() > 20) {
                    string = string + " (" + PotionCauldron.getDurationString(effect) + ")";
                }
                if (PotionCauldron.potionTypes[Potion.getIdFromPotion(effect.getPotion())].isUsable()) {
                    list.add(new TextComponentTranslation(string).setStyle((new Style()).setColor(TextFormatting.RED)).getFormattedText());
                    continue;
                }
                list.add(new TextComponentTranslation(string).setStyle((new Style()).setColor(TextFormatting.BLUE)).getFormattedText());
            }
        } else {
            list.add(new TextComponentTranslation("effect.none").setStyle((new Style()).setColor(TextFormatting.GRAY)).getFormattedText());
        }
    }

    @ParametersAreNonnullByDefault
    public void getSubItems(CreativeTabs tabs, NonNullList<ItemStack> stacks) {}

    @SideOnly(Side.CLIENT)
    @ParametersAreNonnullByDefault
    public boolean hasEffect(ItemStack itemStack) {
        return true;
    }

}