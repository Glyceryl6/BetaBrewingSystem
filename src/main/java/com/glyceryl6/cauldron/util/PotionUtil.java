package com.glyceryl6.cauldron.util;

import com.glyceryl6.cauldron.item.ItemBrewingCauldronPotion;
import com.google.common.collect.Lists;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.Tuple;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;

import java.util.List;
import java.util.Map;

public class PotionUtil {

    @SuppressWarnings("deprecation")
    public static void addPotionInformation(ItemStack itemStack, List<String> lore, float durationFactor) {
        List<PotionEffect> list = ItemBrewingCauldronPotion.a_(itemStack);
        List<Tuple<String, AttributeModifier>> list1 = Lists.newArrayList();
        if (list != null && !list.isEmpty()) {
            for (PotionEffect potionEffect : list) {
                String s1 = I18n.translateToLocal(potionEffect.getEffectName()).trim();
                Potion potion = potionEffect.getPotion();
                Map<IAttribute, AttributeModifier> map = potion.getAttributeModifierMap();
                if (!map.isEmpty()) {
                    for (Map.Entry<IAttribute, AttributeModifier> entry : map.entrySet()) {
                        AttributeModifier attributeModifier = entry.getValue();
                        AttributeModifier attributeModifier1 = new AttributeModifier(attributeModifier.getName(),
                                potion.getAttributeModifierAmount(potionEffect.getAmplifier(), attributeModifier), attributeModifier.getOperation());
                        list1.add(new Tuple<>(entry.getKey().getName(), attributeModifier1));
                    }
                }

                if (potionEffect.getAmplifier() > 0) {
                    s1 = s1 + " " + I18n.translateToLocal("potion.potency." + potionEffect.getAmplifier()).trim();
                }

                if (potionEffect.getDuration() > 20) {
                    s1 = s1 + " (" + Potion.getPotionDurationString(potionEffect, durationFactor) + ")";
                }

                if (potion.isBadEffect()) {
                    lore.add(TextFormatting.RED + s1);
                } else {
                    lore.add(TextFormatting.BLUE + s1);
                }
            }
        }

        if (!list1.isEmpty()) {
            lore.add("");
            lore.add(TextFormatting.DARK_PURPLE + I18n.translateToLocal("potion.whenDrank"));
            for (Tuple<String, AttributeModifier> tuple : list1) {
                AttributeModifier attributeModifier2 = tuple.getSecond();
                double d0 = attributeModifier2.getAmount();
                double d1;

                if (attributeModifier2.getOperation() != 1 && attributeModifier2.getOperation() != 2) {
                    d1 = attributeModifier2.getAmount();
                } else {
                    d1 = attributeModifier2.getAmount() * 100.0D;
                }

                if (d0 > 0.0D) {
                    lore.add(TextFormatting.BLUE + I18n.translateToLocalFormatted("attribute.modifier.plus." + attributeModifier2.getOperation(),
                            ItemStack.DECIMALFORMAT.format(d1), I18n.translateToLocal("attribute.name." + tuple.getFirst())));
                } else if (d0 < 0.0D) {
                    d1 = d1 * -1.0D;
                    lore.add(TextFormatting.RED + I18n.translateToLocalFormatted("attribute.modifier.take." + attributeModifier2.getOperation(),
                            ItemStack.DECIMALFORMAT.format(d1), I18n.translateToLocal("attribute.name." + tuple.getFirst())));
                }
            }
        }
    }

}
