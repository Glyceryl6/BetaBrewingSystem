package com.glyceryl6.cauldron.block;

import com.glyceryl6.cauldron.RegistryContents;
import com.glyceryl6.cauldron.potion.PotionHelper;
import com.glyceryl6.cauldron.util.ColorUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.*;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class BrewingCauldronEntity extends BlockEntity {

    private int liquidLevel;
    private int liquidData;

    public BrewingCauldronEntity(BlockPos worldPosition, BlockState blockState) {
        super(RegistryContents.BREWING_CAULDRON_ENTITY.get(), worldPosition, blockState);
    }

    public boolean isCauldronEmpty() {
        return (this.liquidLevel <= 0);
    }

    public boolean isCauldronDataZero() {
        return (this.liquidData == 0);
    }

    public boolean fillCauldronWithWaterBucket() {
        if (this.liquidLevel == 0) {
            this.liquidLevel = 3;
            this.liquidData = 0;
            return true;
        }
        if (this.liquidLevel < 3) {
            this.liquidData = PotionHelper.applyIngredient(this.liquidData, "-1-3-5-7-9-11-13");
            this.liquidLevel = 3;
            return true;
        }
        return false;
    }

    public boolean applyIngredient(ItemStack itemstack) {
        if (itemstack.getItem() == RegistryContents.POTION_ITEM.get() ||
                (itemstack.getItem() == Items.POTION &&
                        itemstack.getDamageValue() == 0 &&
                        (isCauldronDataZero() || isCauldronEmpty()))) {
            if (isCauldronEmpty()) {
                this.liquidLevel = 1;
                this.liquidData = itemstack.getDamageValue();
                return true;
            }
            if (this.liquidData == itemstack.getDamageValue() && this.liquidLevel < 3) {
                this.liquidLevel++;
                return true;
            }
            return false;
        }
        if (!isCauldronEmpty() && itemstack.getItem() == Items.NETHER_WART) {
            int i = PotionHelper.applyNetherWart(this.liquidData);
            if (i != this.liquidData) {
                this.liquidData = i;
                return true;
            }
            return false;
        }
        if (!isCauldronEmpty() && PotionHelper.isPotionIngredient(Item.getId(itemstack.getItem()))) {
            String potionEffect = PotionHelper.getPotionEffect(Item.getId(itemstack.getItem()));
            int i = PotionHelper.applyIngredient(this.liquidData, potionEffect);
            if (i != this.liquidData) {
                this.liquidData = i;
                return true;
            }
            return false;
        }
        return false;
    }

    public void decrementLiquidLevel() {
        this.liquidLevel--;
    }

    public int getLiquidLevel() {
        return this.liquidLevel;
    }

    public int getLiquidData() {
        return this.liquidData;
    }

    public void setLiquidLevel(int liquidLevel) {
        this.liquidLevel = liquidLevel;
    }

    public void setLiquidData(int liquidData) {
        this.liquidData = liquidData;
    }

    public int getLiquidColor() {
        int potionColor = this.getPotionColor();
        float f1 = (potionColor >> 16 & 0xFF) / 255.0F;
        float f2 = (potionColor >> 8 & 0xFF) / 255.0F;
        float f3 = (potionColor & 0xFF) / 255.0F;
        return ColorUtil.setColorOpaque_F(f1, f2, f3);
    }

    public int getPotionColor() {
        return PotionHelper.getPotionColor(this.liquidData);
    }

    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        this.liquidLevel = tag.getInt("liquidLevel");
        this.liquidData = tag.getInt("liquidData");
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("liquidLevel", this.liquidLevel);
        tag.putInt("liquidData", this.liquidData);
    }

}