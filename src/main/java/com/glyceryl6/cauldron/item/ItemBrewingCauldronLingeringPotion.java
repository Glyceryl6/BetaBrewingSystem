package com.glyceryl6.cauldron.item;

import com.glyceryl6.cauldron.block.PotionHelperCauldron;
import com.glyceryl6.cauldron.util.PotionUtil;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;
import java.util.Objects;

public class ItemBrewingCauldronLingeringPotion extends ItemBrewingCauldronPotion {

    public String getItemStackDisplayName(ItemStack itemStack) {
        if (itemStack.getItemDamage() == 0) {
            return new TextComponentTranslation("item.emptyLingeringPotion.name").getFormattedText();
        }
        String string = PotionHelperCauldron.getPotionPrefix(itemStack.getItemDamage());
        return new TextComponentTranslation("lingering_" + string).getFormattedText();
    }

    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack itemStack, World world, List<String> lore, ITooltipFlag bool) {
        PotionUtil.addPotionInformation(itemStack, lore, 0.25F);
    }

    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack itemStack = player.getHeldItem(hand);
        ItemStack itemStack1 = player.capabilities.isCreativeMode ? itemStack.copy() : itemStack.splitStack(1);
        world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_LINGERINGPOTION_THROW,
                SoundCategory.NEUTRAL, 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
        if (!world.isRemote) {
            EntityPotion potion = new EntityPotion(world, player, itemStack1);
            potion.shoot(player, player.rotationPitch, player.rotationYaw, -20.0F, 0.5F, 1.0F);
            world.spawnEntity(potion);
        }

        player.addStat(Objects.requireNonNull(StatList.getObjectUseStats(this)));
        return new ActionResult<>(EnumActionResult.SUCCESS, itemStack);
    }

}
