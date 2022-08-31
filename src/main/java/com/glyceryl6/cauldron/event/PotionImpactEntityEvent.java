package com.glyceryl6.cauldron.event;

import com.glyceryl6.cauldron.RegistryContents;
import com.glyceryl6.cauldron.item.SpecialPotion;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.List;

public class PotionImpactEntityEvent {

    @SuppressWarnings("unchecked")
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPotionImpactEntity(ProjectileImpactEvent event) {
        Projectile projectile = event.getProjectile();
        if (projectile instanceof ThrownPotion potion && !projectile.level.isClientSide) {
            if (this.isSpecialPotion(potion.getItem())) {
                HitResult hitResult = event.getRayTraceResult();
                ItemStack itemStack = potion.getItem();
                CompoundTag compoundTag = itemStack.getTag();
                List<MobEffectInstance> potionEffects = SpecialPotion.a_(itemStack);
                if (potionEffects != null && !potionEffects.isEmpty()) {
                    potion.applyWater();
                    if (itemStack.is(RegistryContents.LINGERING_POTION_ITEM.get())) {
                        this.makeAreaOfEffectCloud(potion, itemStack);
                    } else {
                        potion.applySplash(potionEffects, hitResult.getType() == HitResult.Type.ENTITY ? ((EntityHitResult)hitResult).getEntity() : null);
                    }
                }
                int color = compoundTag != null ? compoundTag.getInt("Color") : 16253176;
                potion.level.levelEvent(2007, potion.blockPosition(), color);
                potion.discard();
                event.setCanceled(true);
            }
        }
    }

    private boolean isSpecialPotion(ItemStack itemStack) {
        Item item = itemStack.getItem();
        return item == RegistryContents.SPLASH_POTION_ITEM.get() ||
                item == RegistryContents.LINGERING_POTION_ITEM.get();
    }

    private void makeAreaOfEffectCloud(ThrownPotion potion, ItemStack stack) {
        AreaEffectCloud areaEffectCloud = new AreaEffectCloud(potion.level, potion.getX(), potion.getY(), potion.getZ());
        Entity entity = potion.getOwner();
        if (entity instanceof LivingEntity) {
            areaEffectCloud.setOwner((LivingEntity)entity);
        }

        areaEffectCloud.setRadius(3.0F);
        areaEffectCloud.setRadiusOnUse(-0.5F);
        areaEffectCloud.setWaitTime(10);
        areaEffectCloud.setRadiusPerTick(-areaEffectCloud.getRadius() / (float)areaEffectCloud.getDuration());

        for(Object effect : SpecialPotion.a_(stack)) {
            areaEffectCloud.addEffect(new MobEffectInstance((MobEffectInstance)effect));
        }

        CompoundTag compoundTag = stack.getTag();
        if (compoundTag != null && compoundTag.contains("Color", 99)) {
            areaEffectCloud.setFixedColor(compoundTag.getInt("Color"));
        }

        potion.level.addFreshEntity(areaEffectCloud);
    }

}
