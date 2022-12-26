package com.glyceryl6.cauldron.dispenser;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.dispenser.BehaviorProjectileDispense;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IPosition;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class DispenserShootLingeringPotion implements IBehaviorDispenseItem {

    @Override
    public ItemStack dispense(IBlockSource source, ItemStack stack) {
        return (new BehaviorProjectileDispense() {

            protected IProjectile getProjectileEntity(World world, IPosition position, ItemStack itemStack) {
                return new EntityPotion(world, position.getX(), position.getY(), position.getZ(), stack.copy());
            }

            protected float getProjectileInaccuracy() {
                return super.getProjectileInaccuracy() * 0.5F;
            }

            protected float getProjectileVelocity() {
                return super.getProjectileVelocity() * 1.25F;
            }

        }).dispense(source, stack);
    }

}
