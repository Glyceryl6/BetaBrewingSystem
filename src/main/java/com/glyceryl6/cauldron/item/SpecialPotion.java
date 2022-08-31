package com.glyceryl6.cauldron.item;

import com.glyceryl6.cauldron.potion.PotionHelper;
import com.glyceryl6.cauldron.util.PotionUtil;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashMap;
import java.util.List;

@ParametersAreNonnullByDefault
public class SpecialPotion extends Item {

    private static final HashMap<Object, Object> a = new HashMap<>();

    public SpecialPotion(Properties properties) {
        super(properties);
    }

    public static List a_(ItemStack itemStack) {
        int i = itemStack.getDamageValue();
        List list = (List)a.get(i);
        if (list == null) {
            list = PotionHelper.getPotionEffects(i);
            a.put(i, list);
        }
        return list;
    }

    @NotNull
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity livingEntity) {
        Player player = livingEntity instanceof Player ? (Player)livingEntity : null;
        if (player instanceof ServerPlayer) {
            CriteriaTriggers.CONSUME_ITEM.trigger((ServerPlayer)player, stack);
        }

        if (!level.isClientSide) {
            List<?> list = a_(stack);
            if (list != null) {
                for (Object effect : list) {
                    if (player != null) {
                        player.addEffect(new MobEffectInstance((MobEffectInstance) effect));
                    }
                }
            }
        }

        if (player != null) {
            player.awardStat(Stats.ITEM_USED.get(this));
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
        }

        if (player == null || !player.getAbilities().instabuild) {
            if (stack.isEmpty()) {
                return new ItemStack(Items.GLASS_BOTTLE);
            }

            if (player != null) {
                player.getInventory().add(new ItemStack(Items.GLASS_BOTTLE));
            }
        }

        level.gameEvent(livingEntity, GameEvent.DRINKING_FINISH, livingEntity.eyeBlockPosition());
        return stack;
    }

    public int getUseDuration(ItemStack pStack) {
        return 32;
    }

    @NotNull
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.DRINK;
    }

    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        return ItemUtils.startUsingInstantly(level, player, hand);
    }

    public String getDescriptionId(ItemStack stack) {
        String key = "item.beta_brewing_system.emptyPotion";
        if (stack.getDamageValue() == 0) {
            return new TranslatableComponent(key).getString();
        }
        String string = PotionHelper.getPotionPrefix(stack.getDamageValue());
        return new TranslatableComponent(string).getString();
    }

    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        PotionUtil.addPotionTooltip(stack, tooltip, 1.0F);
    }

    public boolean isFoil(ItemStack stack) {
        return super.isFoil(stack) || stack.getDamageValue() != 0;
    }

}
