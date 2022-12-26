package com.glyceryl6.cauldron.block;

import com.glyceryl6.cauldron.Cauldron;
import com.glyceryl6.cauldron.util.ColorUtil;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

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

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
        return oldState.getBlock() != newState.getBlock();
    }

    public boolean applyIngredient(ItemStack itemstack) {
        if (itemstack.getItem() == Cauldron.POTION_ITEM || (itemstack.getItem() == Items.GLASS_BOTTLE &&
                        itemstack.getItemDamage() == 0 && (isCauldronDataZero() || isCauldronEmpty()))) {
            //由于Bug，暂时先注释掉该段代码。
         /* if (isCauldronEmpty()) {
                this.liquidLevel = 1;
                this.liquidData = itemStack.getItemDamage();
                return true;
            } */
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
                if (this.liquidData >= 32767) {
                    this.liquidData = 32766;
                }
                System.out.println(this.liquidData);
                return true;
            }
            return false;
        }
        if (!isCauldronEmpty() && PotionHelperCauldron.isPotionIngredient(Item.getIdFromItem(itemstack.getItem()))) {
            String potionEffect = PotionHelperCauldron.getPotionEffect(Item.getIdFromItem(itemstack.getItem()));
            int i = PotionHelperCauldron.applyIngredient(this.liquidData, potionEffect);
            if (i != this.liquidData) {
                this.liquidData = i;
                if (this.liquidData >= 32767) {
                    this.liquidData = 32766;
                }
                System.out.println(this.liquidData);
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

    public int getLiquidColor() {
        int potionColor = this.getPotionColor();
        float f1 = (potionColor >> 16 & 0xFF) / 255.0F;
        float f2 = (potionColor >> 8 & 0xFF) / 255.0F;
        float f3 = (potionColor & 0xFF) / 255.0F;
        return ColorUtil.setColorOpaque_F(f1, f2, f3);
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