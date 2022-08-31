package com.glyceryl6.cauldron.item;

import com.glyceryl6.cauldron.RegistryContents;
import com.glyceryl6.cauldron.potion.PotionHelper;
import com.glyceryl6.cauldron.util.PotionUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collection;
import java.util.List;

@ParametersAreNonnullByDefault
public class SpecialTippedArrow extends ArrowItem {

    public SpecialTippedArrow(Properties properties) {
        super(properties);
    }

    @NotNull
    public String getDescriptionId(ItemStack stack) {
        String key = "item.beta_brewing_system.potion_arrow";
        if (stack.getDamageValue() == 0) {
            return new TranslatableComponent(key).getString();
        }
        String string = PotionHelper.getPotionPrefix(stack.getDamageValue());
        return new TranslatableComponent("arrow_" + string).getString();
    }

    @NotNull
    @Override
    public AbstractArrow createArrow(Level level, ItemStack stack, LivingEntity shooter) {
        Arrow arrow = new Arrow(level, shooter);
        this.setPotionEffect(stack, arrow);
        return arrow;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        PotionUtil.addPotionTooltip(stack, tooltip, 0.125F);
    }

    public int getCustomColor(ItemStack stack) {
        CompoundTag compoundTag = stack.getTag();
        return compoundTag != null ? compoundTag.getInt("Color") : -1;
    }

    @SuppressWarnings("unchecked")
    public void setPotionEffect(ItemStack stack, Arrow tippedArrow) {
        if (stack.getItem().getDefaultInstance().is(RegistryContents.TIPPED_ARROW_ITEM.get()))  {
            Collection<MobEffectInstance> collection = SpecialPotion.a_(stack);
            if (!collection.isEmpty()) {
                for (MobEffectInstance instance : collection) {
                    tippedArrow.effects.add(new MobEffectInstance(instance));
                }
            }

            int i = this.getCustomColor(stack);

            if (i == -1) {
                tippedArrow.updateColor();
            } else {
                tippedArrow.setFixedColor(i);
            }
        } else if (stack.getItem() == Items.ARROW) {
            tippedArrow.potion = Potions.EMPTY;
            tippedArrow.effects.clear();
            tippedArrow.getEntityData().set(Arrow.ID_EFFECT_COLOR, -1);
        }
    }

}