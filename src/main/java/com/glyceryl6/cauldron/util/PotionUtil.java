package com.glyceryl6.cauldron.util;

import com.glyceryl6.cauldron.item.SpecialPotion;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Map;

public class PotionUtil {

    @SuppressWarnings("unchecked")
    public static void addPotionTooltip(ItemStack stack, List<Component> lore, float durationFactor) {
        List<MobEffectInstance> list = SpecialPotion.a_(stack);
        List<Pair<Attribute, AttributeModifier>> list1 = Lists.newArrayList();
        if (list != null && !list.isEmpty()) {
            for(MobEffectInstance instance : list) {
                MutableComponent mutableComponent = new TranslatableComponent(instance.getDescriptionId());
                MobEffect mobeffect = instance.getEffect();
                Map<Attribute, AttributeModifier> map = mobeffect.getAttributeModifiers();
                if (!map.isEmpty()) {
                    for(Map.Entry<Attribute, AttributeModifier> entry : map.entrySet()) {
                        AttributeModifier attributeModifier = entry.getValue();
                        AttributeModifier attributeModifier1 = new AttributeModifier(attributeModifier.getName(), mobeffect.getAttributeModifierValue(instance.getAmplifier(), attributeModifier), attributeModifier.getOperation());
                        list1.add(new Pair<>(entry.getKey(), attributeModifier1));
                    }
                }

                if (instance.getAmplifier() > 0) {
                    mutableComponent = new TranslatableComponent("potion.withAmplifier", mutableComponent, new TranslatableComponent("potion.potency." + instance.getAmplifier()));
                }

                if (instance.getDuration() > 20) {
                    mutableComponent = new TranslatableComponent("potion.withDuration", mutableComponent, MobEffectUtil.formatDuration(instance, durationFactor));
                }

                lore.add(mutableComponent.withStyle(mobeffect.getCategory().getTooltipFormatting()));
            }
        }

        if (!list1.isEmpty()) {
            lore.add(TextComponent.EMPTY);
            lore.add((new TranslatableComponent("potion.whenDrank")).withStyle(ChatFormatting.DARK_PURPLE));
            for(Pair<Attribute, AttributeModifier> pair : list1) {
                AttributeModifier attributeModifier2 = pair.getSecond();
                double d0 = attributeModifier2.getAmount();
                double d1;
                if (attributeModifier2.getOperation() != AttributeModifier.Operation.MULTIPLY_BASE && attributeModifier2.getOperation() != AttributeModifier.Operation.MULTIPLY_TOTAL) {
                    d1 = attributeModifier2.getAmount();
                } else {
                    d1 = attributeModifier2.getAmount() * 100.0D;
                }

                if (d0 > 0.0D) {
                    lore.add((new TranslatableComponent("attribute.modifier.plus." + attributeModifier2.getOperation().toValue(), ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(d1), new TranslatableComponent(pair.getFirst().getDescriptionId()))).withStyle(ChatFormatting.BLUE));
                } else if (d0 < 0.0D) {
                    d1 *= -1.0D;
                    lore.add((new TranslatableComponent("attribute.modifier.take." + attributeModifier2.getOperation().toValue(), ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(d1), new TranslatableComponent(pair.getFirst().getDescriptionId()))).withStyle(ChatFormatting.RED));
                }
            }
        }

    }

}
