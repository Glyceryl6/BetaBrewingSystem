package com.glyceryl6.cauldron.item;

import com.glyceryl6.cauldron.block.PotionHelperCauldron;
import com.glyceryl6.cauldron.util.PotionUtil;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class ItemBrewingCauldronPotion extends Item {

    private static final HashMap<Object, Object> a = new HashMap<>();

    public ItemBrewingCauldronPotion() {
        setMaxStackSize(1);
        setMaxDamage(32767);
        setHasSubtypes(false);
    }

    public static List a_(ItemStack itemStack) {
        int i = itemStack.getItemDamage();
        List list = (List)a.get(i);
        if (list == null) {
            list = PotionHelperCauldron.getPotionEffects(i);
            a.put(i, list);
        }
        return list;
    }

    @ParametersAreNonnullByDefault
    public ItemStack onItemUseFinish(ItemStack stack, World world, EntityLivingBase entityLiving) {
        EntityPlayer entityplayer = entityLiving instanceof EntityPlayer ? (EntityPlayer)entityLiving : null;
        if (entityplayer == null || !entityplayer.capabilities.isCreativeMode) {
            stack.shrink(1);
        }
        if (entityplayer instanceof EntityPlayerMP) {
            CriteriaTriggers.CONSUME_ITEM.trigger((EntityPlayerMP)entityplayer, stack);
        }

        if (!world.isRemote) {
            List<?> list = a_(stack);
            if (list != null) {
                for (Object effect : list) {
                    if (entityplayer != null) {
                        entityplayer.addPotionEffect(new PotionEffect((PotionEffect) effect));
                    }
                }
            }
        }
        if (entityplayer != null) {
            entityplayer.addStat(Objects.requireNonNull(StatList.getObjectUseStats(this)));
        }
        if (entityplayer == null || !entityplayer.capabilities.isCreativeMode) {
            if (stack.isEmpty()) {
                return new ItemStack(Items.GLASS_BOTTLE);
            }
            if (entityplayer != null) {
                entityplayer.inventory.addItemStackToInventory(new ItemStack(Items.GLASS_BOTTLE));
            }
        }

        return stack;
    }

    public int getMaxItemUseDuration(ItemStack itemStack) {
        return 32;
    }

    public EnumAction getItemUseAction(ItemStack itemStack) {
        return EnumAction.DRINK;
    }

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
    public void addInformation(ItemStack itemStack, World world, List<String> lore, ITooltipFlag bool) {
        PotionUtil.addPotionInformation(itemStack, lore, 1.0F);
    }

    @SideOnly(Side.CLIENT)
    public boolean hasEffect(ItemStack itemStack) {
        return itemStack.getItemDamage() != 0;
    }

}