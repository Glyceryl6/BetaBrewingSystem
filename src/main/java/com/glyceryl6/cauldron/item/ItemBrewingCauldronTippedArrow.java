package com.glyceryl6.cauldron.item;

import com.glyceryl6.cauldron.Cauldron;
import com.glyceryl6.cauldron.block.PotionHelperCauldron;
import com.glyceryl6.cauldron.util.PotionUtil;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.init.Items;
import net.minecraft.init.PotionTypes;
import net.minecraft.item.ItemArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collection;
import java.util.List;

@MethodsReturnNonnullByDefault
public class ItemBrewingCauldronTippedArrow extends ItemArrow {

    public ItemBrewingCauldronTippedArrow() {
        setMaxDamage(32767);
        setHasSubtypes(false);
    }

    public String getItemStackDisplayName(ItemStack itemStack) {
        if (itemStack.getItemDamage() == 0) {
            return new TextComponentTranslation("item.potion_arrow.name").getFormattedText();
        }
        String string = PotionHelperCauldron.getPotionPrefix(itemStack.getItemDamage());
        return new TextComponentTranslation("arrow_" + string).getFormattedText();
    }

    @ParametersAreNonnullByDefault
    public EntityArrow createArrow(World worldIn, ItemStack stack, EntityLivingBase shooter) {
        EntityTippedArrow tippedArrow = new EntityTippedArrow(worldIn, shooter);
        this.setPotionEffect(stack, tippedArrow);
        return tippedArrow;
    }

    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
        PotionUtil.addPotionInformation(stack, tooltip, 0.125F);
    }

    public static int getCustomColor(ItemStack stack) {
        NBTTagCompound nbttagcompound = stack.getTagCompound();
        return nbttagcompound != null ? nbttagcompound.getInteger("Color") : -1;
    }

    public void setPotionEffect(ItemStack stack, EntityTippedArrow tippedArrow) {
        if (stack.getItem() == Cauldron.TIPPED_ARROW_ITEM) {
            Collection<PotionEffect> collection = ItemBrewingCauldronPotion.a_(stack);
            if (!collection.isEmpty()) {
                for (PotionEffect potioneffect : collection) {
                    tippedArrow.customPotionEffects.add(new PotionEffect(potioneffect));
                }
            }

            int i = getCustomColor(stack);

            if (i == -1) {
                this.refreshColor(tippedArrow);
            } else {
                this.setFixedColor(tippedArrow, i);
            }
        }
        else if (stack.getItem() == Items.ARROW) {
            tippedArrow.potion = PotionTypes.EMPTY;
            tippedArrow.customPotionEffects.clear();
            tippedArrow.getDataManager().set(EntityTippedArrow.COLOR, -1);
        }
    }

    private void refreshColor(EntityTippedArrow tippedArrow) {
        tippedArrow.fixedColor = false;
        tippedArrow.getDataManager().set(EntityTippedArrow.COLOR, PotionUtils.getPotionColorFromEffectList(PotionUtils.mergeEffects(tippedArrow.potion, tippedArrow.customPotionEffects)));
    }

    private void setFixedColor(EntityTippedArrow tippedArrow, int color) {
        tippedArrow.fixedColor = true;
        tippedArrow.getDataManager().set(EntityTippedArrow.COLOR, color);
    }

}