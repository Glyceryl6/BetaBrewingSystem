package com.glyceryl6.cauldron.block;

import com.glyceryl6.cauldron.Cauldron;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Random;

@ParametersAreNonnullByDefault
@SuppressWarnings("deprecation")
public class BlockBrewingCauldron extends BlockContainer {

    private static final AxisAlignedBB AABB_LEGS = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.3125D, 1.0D);
    private static final AxisAlignedBB AABB_WALL_NORTH = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 0.125D);
    private static final AxisAlignedBB AABB_WALL_SOUTH = new AxisAlignedBB(0.0D, 0.0D, 0.875D, 1.0D, 1.0D, 1.0D);
    private static final AxisAlignedBB AABB_WALL_EAST = new AxisAlignedBB(0.875D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
    private static final AxisAlignedBB AABB_WALL_WEST = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.125D, 1.0D, 1.0D);

    public BlockBrewingCauldron() {
        super(Material.IRON, MapColor.STONE);
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new EntityBrewingCauldron();
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    public void addCollisionBoxToList(IBlockState state, World world, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entity, boolean isActualState) {
        addCollisionBoxToList(pos, entityBox, collidingBoxes, AABB_LEGS);
        addCollisionBoxToList(pos, entityBox, collidingBoxes, AABB_WALL_WEST);
        addCollisionBoxToList(pos, entityBox, collidingBoxes, AABB_WALL_NORTH);
        addCollisionBoxToList(pos, entityBox, collidingBoxes, AABB_WALL_EAST);
        addCollisionBoxToList(pos, entityBox, collidingBoxes, AABB_WALL_SOUTH);
    }

    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float f1, float f2, float f3) {
        if (world.isRemote) {
            world.notifyBlockUpdate(pos, state, state, 3);
        }
        ItemStack heldItemStack = player.inventory.getCurrentItem();
        TileEntity tileentity = world.getTileEntity(pos);
        if (!(tileentity instanceof EntityBrewingCauldron)) return true;
        EntityBrewingCauldron entityBrewingCauldron = (EntityBrewingCauldron)tileentity;
        int cauldronMetadata = entityBrewingCauldron.getLiquidData();
        if (heldItemStack.getItem() == Items.WATER_BUCKET) {
            if (entityBrewingCauldron.fillCauldronWithWaterBucket()) {
                if (!player.capabilities.isCreativeMode) {
                    player.inventory.setInventorySlotContents(player.inventory.currentItem, new ItemStack(Items.BUCKET));
                }
                if (!entityBrewingCauldron.isCauldronDataZero() && cauldronMetadata != entityBrewingCauldron.getLiquidData()) {
                    world.notifyBlockUpdate(pos, state, state, 3);
                }
                world.playSound(null, pos, SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);
            }
            return true;
        }
        if (heldItemStack.getItem() == Items.GLASS_BOTTLE) {
            if (!entityBrewingCauldron.isCauldronEmpty()) {
                ItemStack potionItemStack = new ItemStack(Cauldron.POTION_ITEM, 1, entityBrewingCauldron.getLiquidData());
                if (!player.inventory.addItemStackToInventory(potionItemStack)) {
                    world.spawnEntity(new EntityItem(world, pos.getX() + 0.5D, pos.getY() + 1.5D, pos.getZ() + 0.5D, potionItemStack));
                }
                if (!player.capabilities.isCreativeMode) {
                    heldItemStack.stackSize--;
                }
                if (heldItemStack.stackSize <= 0) {
                    player.inventory.setInventorySlotContents(player.inventory.currentItem, ItemStack.EMPTY);
                }
                entityBrewingCauldron.decrementLiquidLevel();
                world.notifyBlockUpdate(pos, state, state, 3);
                world.playSound(null, pos, SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);
            }
        } else if (entityBrewingCauldron.applyIngredient(heldItemStack)) {
            if (heldItemStack.getItem() == Cauldron.POTION_ITEM) {
                if (!player.capabilities.isCreativeMode) {
                    heldItemStack.stackSize--;
                }
                if (heldItemStack.stackSize <= 0) {
                    player.inventory.setInventorySlotContents(player.inventory.currentItem, ItemStack.EMPTY);
                }
                player.inventory.addItemStackToInventory(new ItemStack(Items.GLASS_BOTTLE));
            } else {
                if (!player.capabilities.isCreativeMode) {
                    heldItemStack.stackSize--;
                }
                if (heldItemStack.stackSize <= 0) {
                    player.inventory.setInventorySlotContents(player.inventory.currentItem, ItemStack.EMPTY);
                }
            }
            world.notifyBlockUpdate(pos, state, state, 3);
            return true;
        }
        return true;
    }

    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    public boolean isFullCube(IBlockState state) {
        return false;
    }

    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return Cauldron.BREWING_CAULDRON;
    }

    public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
        return new ItemStack(Cauldron.BREWING_CAULDRON);
    }

    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

}