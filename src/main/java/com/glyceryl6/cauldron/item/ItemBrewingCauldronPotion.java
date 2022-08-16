package com.glyceryl6.cauldron.item;

import com.glyceryl6.cauldron.Cauldron;
import com.glyceryl6.cauldron.block.PotionCauldron;
import com.glyceryl6.cauldron.block.PotionHelperCauldron;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashMap;
import java.util.List;

public class ItemBrewingCauldronPotion extends Item {

    private final HashMap<Object, Object> a = new HashMap<>();

    public ItemBrewingCauldronPotion() {
        setMaxStackSize(1);
        setMaxDamage(32767);
        setHasSubtypes(false);
    }

    public List a_(ItemStack itemStack) {
        int i = itemStack.getItemDamage();
        List list = (List)this.a.get(i);
        if (list == null) {
            list = PotionHelperCauldron.getPotionEffects(i);
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
            if (!((EntityPlayer) entityLiving).capabilities.isCreativeMode) {
                itemStack.stackSize--;
            }
            ((EntityPlayer) entityLiving).inventory.addItemStackToInventory(new ItemStack(Items.GLASS_BOTTLE));
        }
        return itemStack;
    }

    public int getMaxItemUseDuration(ItemStack itemStack) {
        return 32;
    }

    public EnumAction getItemUseAction(ItemStack itemStack) {
        return EnumAction.DRINK;
    }

    @ParametersAreNonnullByDefault
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        player.setActiveHand(hand);
        return new ActionResult(EnumActionResult.SUCCESS, player.getHeldItem(hand));
    }

    public String getItemStackDisplayName(ItemStack itemStack) {
        if (itemStack.getItemDamage() == 0) {
            return new TextComponentTranslation("item.emptyPotion.name").getFormattedText();
        }
        String string = PotionHelperCauldron.getPotionPrefix(itemStack.getItemDamage());
        return new TextComponentTranslation(string).getFormattedText();
    }

    @SideOnly(Side.CLIENT)
    @SuppressWarnings("deprecation")
    public void addInformation(ItemStack itemStack, World world, List<String> list, ITooltipFlag bool) {
        if (itemStack.getItemDamage() == 0) return;
        List<PotionEffect> list1 = Cauldron.POTION_ITEM.a_(itemStack);
        if (list1 != null && !list1.isEmpty()) {
            for (PotionEffect effect : list1) {
                String string = I18n.translateToLocal(effect.getEffectName()).trim();
                if (effect.getAmplifier() > 0) {
                    string = string + " " + I18n.translateToLocal("potion.potency." + effect.getAmplifier()).trim();
                }
                if (effect.getDuration() > 20) {
                    string = string + " (" + Potion.getPotionDurationString(effect, 1.0F) + ")";
                }
                if (PotionCauldron.potionTypes[Potion.getIdFromPotion(effect.getPotion())].isUsable()) {
                    list.add(TextFormatting.RED + I18n.translateToLocal(string));
                    continue;
                }
                list.add(TextFormatting.BLUE + I18n.translateToLocal(string));
            }
        } else {
            list.add(TextFormatting.GRAY + I18n.translateToLocal("effect.none"));
        }
    }

    @SideOnly(Side.CLIENT)
    public boolean hasEffect(ItemStack itemStack) {
        return itemStack.getItemDamage() != 0;
    }

}