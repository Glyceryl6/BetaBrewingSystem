package com.glyceryl6.cauldron.item;

import com.glyceryl6.cauldron.potion.PotionHelper;
import com.glyceryl6.cauldron.util.PotionUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public class SpecialLingeringPotion extends SpecialPotion {

    public SpecialLingeringPotion(Properties properties) {
        super(properties);
    }

    public String getDescriptionId(ItemStack stack) {
        String key = "item.beta_brewing_system.emptyLingeringPotion";
        if (stack.getDamageValue() == 0) {
            return new TranslatableComponent(key).getString();
        }
        String string = PotionHelper.getPotionPrefix(stack.getDamageValue());
        return new TranslatableComponent("lingering_" + string).getString();
    }

    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        PotionUtil.addPotionTooltip(stack, tooltip, 0.25F);
    }

    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemInHand = player.getItemInHand(hand);
        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.LINGERING_POTION_THROW,
                SoundSource.PLAYERS, 0.5F, 0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F));
        if (!level.isClientSide) {
            ThrownPotion potion = new ThrownPotion(level, player);
            potion.setItem(itemInHand);
            potion.shootFromRotation(player, player.getXRot(), player.getYRot(), -20.0F, 0.5F, 1.0F);
            level.addFreshEntity(potion);
        }

        player.awardStat(Stats.ITEM_USED.get(this));
        if (!player.getAbilities().instabuild) {
            itemInHand.shrink(1);
        }

        return InteractionResultHolder.sidedSuccess(itemInHand, level.isClientSide());
    }

}
