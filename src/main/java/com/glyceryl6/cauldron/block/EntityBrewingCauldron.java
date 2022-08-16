package com.glyceryl6.cauldron.block;

import com.glyceryl6.cauldron.Cauldron;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class EntityBrewingCauldron extends TileEntity {

    private int liquidLevel;
    private int liquidData;

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
            this.liquidData = PotionHelperCauldron.applyIngredient(this.liquidData, "-1-3-5-7-9-11-13");
            this.liquidLevel = 3;
            return true;
        }
        return false;
    }

    public boolean applyIngredient(ItemStack itemstack) {
        if (itemstack.getItem() == Cauldron.POTION_ITEM ||
                (itemstack.getItem() == Items.POTIONITEM && itemstack.getItemDamage() == 0 &&
                        (isCauldronDataZero() || isCauldronEmpty()))) {
            if (isCauldronEmpty()) {
                this.liquidLevel = 1;
                this.liquidData = itemstack.getItemDamage();
                return true;
            }
            if (this.liquidData == itemstack.getItemDamage() && this.liquidLevel < 3) {
                this.liquidLevel++;
                return true;
            }
            return false;
        }
        if (!isCauldronEmpty() && itemstack.getItem() == Items.NETHER_WART) {
            int i = PotionHelperCauldron.applyNetherWart(this.liquidData);
            if (i != this.liquidData) {
                this.liquidData = i;
                return true;
            }
            return false;
        }
        if (!isCauldronEmpty() && PotionHelperCauldron.isPotionIngredient(Item.getIdFromItem(itemstack.getItem()))) {
            String potionEffect = PotionHelperCauldron.getPotionEffect(Item.getIdFromItem(itemstack.getItem()));
            int i = PotionHelperCauldron.applyIngredient(this.liquidData, potionEffect);
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

    public void setLiquidLevel(int liquidLevel) {
        this.liquidLevel = liquidLevel;
    }

    public int getLiquidLevel() {
        return this.liquidLevel;
    }

    public int getLiquidData() {
        return this.liquidData;
    }

    public int getPotionColor() {
        return PotionHelperCauldron.getPotionColor(this.liquidData);
    }

    @Nullable
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(this.pos, 3, this.getUpdateTag());
    }

    public NBTTagCompound getUpdateTag() {
        return this.writeToNBT(new NBTTagCompound());
    }

    public void onDataPacket(NetworkManager netManager, SPacketUpdateTileEntity packet) {
        readFromNBT(packet.getNbtCompound());
    }

    public void readFromNBT(NBTTagCompound nbttagcompound) {
        super.readFromNBT(nbttagcompound);
        this.liquidLevel = nbttagcompound.getInteger("liquidLevel");
        this.liquidData = nbttagcompound.getInteger("liquidData");
    }

    public NBTTagCompound writeToNBT(NBTTagCompound nbttagcompound) {
        super.writeToNBT(nbttagcompound);
        nbttagcompound.setInteger("liquidLevel", this.liquidLevel);
        nbttagcompound.setInteger("liquidData", this.liquidData);
        return nbttagcompound;
    }

}