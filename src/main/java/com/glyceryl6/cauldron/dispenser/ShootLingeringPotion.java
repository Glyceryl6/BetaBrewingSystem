package com.glyceryl6.cauldron.dispenser;

import net.minecraft.Util;
import net.minecraft.core.BlockSource;
import net.minecraft.core.Position;
import net.minecraft.core.dispenser.AbstractProjectileDispenseBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class ShootLingeringPotion implements DispenseItemBehavior {

    @NotNull
    @Override
    public ItemStack dispense(BlockSource source, ItemStack stack) {
        return (new AbstractProjectileDispenseBehavior() {
            @NotNull
            protected Projectile getProjectile(Level level, Position pos, ItemStack itemStack) {
                return Util.make(new ThrownPotion(level, pos.x(), pos.y(), pos.z()), (p_123515_) -> {
                    p_123515_.setItem(itemStack);
                });
            }

            protected float getUncertainty() {
                return super.getUncertainty() * 0.5F;
            }

            protected float getPower() {
                return super.getPower() * 1.25F;
            }
        }).dispense(source, stack);
    }

}
