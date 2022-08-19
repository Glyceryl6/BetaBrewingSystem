package com.glyceryl6.cauldron.event;

import com.glyceryl6.cauldron.Cauldron;
import com.glyceryl6.cauldron.item.ItemBrewingCauldronPotion;
import net.minecraft.entity.EntityAreaEffectCloud;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.List;

public class PotionImpactEntityEvent {

    @SubscribeEvent
    public void onPotionImpactEntity(ProjectileImpactEvent.Throwable event) {
        EntityThrowable throwable = event.getThrowable();
        if (throwable instanceof EntityPotion && !throwable.world.isRemote) {
            RayTraceResult result = event.getRayTraceResult();
            EntityPotion entityPotion = (EntityPotion) throwable;
            ItemStack itemStack = this.getPotion(entityPotion);
            List<PotionEffect> potionEffects = ItemBrewingCauldronPotion.a_(itemStack);
            if (result.typeOfHit == RayTraceResult.Type.BLOCK) {
                BlockPos blockpos = result.getBlockPos().offset(result.sideHit);
                this.extinguishFires(entityPotion, entityPotion.getPosition(), result.sideHit);
                for (EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL) {
                    this.extinguishFires(entityPotion, blockpos.offset(enumfacing), enumfacing);
                }
            }
            if (potionEffects != null && !potionEffects.isEmpty()) {
                this.applyWater(entityPotion);
                if (this.getPotion(entityPotion).getItem() == Cauldron.LINGERING_POTION_ITEM) {
                    this.makeAreaOfEffectCloud(entityPotion, itemStack);
                } else {
                    this.applySplash(entityPotion, result, potionEffects);
                }
            }
            entityPotion.setDead();
        }
    }

    public ItemStack getPotion(EntityPotion potion) {
        ItemStack itemstack = potion.getDataManager().get(EntityPotion.ITEM);
        if (itemstack.getItem() != Cauldron.SPLASH_POTION_ITEM &&
            itemstack.getItem() != Cauldron.LINGERING_POTION_ITEM) {
            return new ItemStack(Cauldron.SPLASH_POTION_ITEM);
        } else {
            return itemstack;
        }
    }

    private void applyWater(EntityPotion potion) {
        AxisAlignedBB axisalignedbb = potion.getEntityBoundingBox().grow(4.0D, 2.0D, 4.0D);
        List<EntityLivingBase> list = potion.world.getEntitiesWithinAABB(EntityLivingBase.class, axisalignedbb, EntityPotion.WATER_SENSITIVE);
        if (!list.isEmpty()) {
            for (EntityLivingBase entitylivingbase : list) {
                double d0 = potion.getDistanceSq(entitylivingbase);
                if (d0 < 16.0D && isWaterSensitiveEntity(entitylivingbase)) {
                    entitylivingbase.attackEntityFrom(DamageSource.DROWN, 1.0F);
                }
            }
        }
    }

    private void extinguishFires(EntityPotion potion, BlockPos pos, EnumFacing facing) {
        if (potion.world.getBlockState(pos).getBlock() == Blocks.FIRE) {
            potion.world.extinguishFire(null, pos.offset(facing), facing.getOpposite());
        }
    }

    private void applySplash(EntityPotion entityPotion, RayTraceResult result, List<PotionEffect> effects) {
        AxisAlignedBB axisalignedbb = entityPotion.getEntityBoundingBox().grow(4.0D, 2.0D, 4.0D);
        List<EntityLivingBase> list = entityPotion.world.getEntitiesWithinAABB(EntityLivingBase.class, axisalignedbb);
        if (!list.isEmpty()) {
            for (EntityLivingBase entitylivingbase : list) {
                if (entitylivingbase.canBeHitWithPotion()) {
                    double d0 = entityPotion.getDistanceSq(entitylivingbase);
                    if (d0 < 16.0D) {
                        double d1 = 1.0D - Math.sqrt(d0) / 4.0D;
                        if (entitylivingbase == result.entityHit) {
                            d1 = 1.0D;
                        }

                        for (PotionEffect potioneffect : effects) {
                            Potion potion = potioneffect.getPotion();
                            if (potion.isInstant()) {
                                potion.affectEntity(entityPotion, entityPotion.getThrower(),
                                        entitylivingbase, potioneffect.getAmplifier(), d1);
                            }
                            else {
                                int i = (int)(d1 * (double)potioneffect.getDuration() + 0.5D);
                                if (i > 20) {
                                    entitylivingbase.addPotionEffect(new PotionEffect(potion, i,
                                            potioneffect.getAmplifier(),
                                            potioneffect.getIsAmbient(),
                                            potioneffect.doesShowParticles()));
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void makeAreaOfEffectCloud(EntityPotion potion, ItemStack itemStack) {
        EntityAreaEffectCloud effectCloud = new EntityAreaEffectCloud(potion.world, potion.posX, potion.posY, potion.posZ);
        effectCloud.setOwner(potion.getThrower());
        effectCloud.setRadius(3.0F);
        effectCloud.setRadiusOnUse(-0.5F);
        effectCloud.setWaitTime(10);
        effectCloud.setRadiusPerTick(-effectCloud.getRadius() / (float)effectCloud.getDuration());
        for (Object potionEffect : ItemBrewingCauldronPotion.a_(itemStack)) {
            effectCloud.addEffect(new PotionEffect((PotionEffect) potionEffect));
        }

        NBTTagCompound compound = itemStack.getTagCompound();
        if (compound != null && compound.hasKey("Color", 99)) {
            effectCloud.setColor(compound.getInteger("Color"));
        }

        potion.world.spawnEntity(effectCloud);
    }

    private static boolean isWaterSensitiveEntity(EntityLivingBase entity) {
        return entity instanceof EntityEnderman || entity instanceof EntityBlaze;
    }

}