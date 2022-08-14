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
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
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

    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return FULL_BLOCK_AABB;
    }

    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer entityplayer, EnumHand hand, EnumFacing facing, float float1, float float2, float float3) {
        if (world.isRemote) {
            world.notifyBlockUpdate(pos, state, state, 3);
        }
        ItemStack heldItemStack = entityplayer.inventory.getCurrentItem();
        TileEntity tileentity = world.getTileEntity(pos);
        if (!(tileentity instanceof EntityBrewingCauldron))
            return true;
        EntityBrewingCauldron entityBrewingCauldron = (EntityBrewingCauldron)tileentity;
        int cauldronMetadata = entityBrewingCauldron.getLiquidData();
        if (heldItemStack.getItem() == Items.WATER_BUCKET) {
            if (entityBrewingCauldron.fillCauldronWithWaterBucket()) {
                entityplayer.inventory.setInventorySlotContents(entityplayer.inventory.currentItem, new ItemStack(Items.BUCKET));
                if (!entityBrewingCauldron.isCauldronDataZero() && cauldronMetadata != entityBrewingCauldron.getLiquidData()) {
                    world.notifyBlockUpdate(pos, state, state, 3);
                }
            }
            return true;
        }
        if (heldItemStack.getItem() == Items.GLASS_BOTTLE) {
            if (!entityBrewingCauldron.isCauldronEmpty()) {
                ItemStack potionItemStack = new ItemStack(Cauldron.POTION_ITEM, 1, entityBrewingCauldron.getLiquidData());
                if (!entityplayer.inventory.addItemStackToInventory(potionItemStack)) {
                    world.spawnEntity(new EntityItem(world, pos.getX() + 0.5D, pos.getY() + 1.5D, pos.getZ() + 0.5D, potionItemStack));
                }
                heldItemStack.stackSize--;
                if (heldItemStack.stackSize <= 0) {
                    entityplayer.inventory.setInventorySlotContents(entityplayer.inventory.currentItem, ItemStack.EMPTY);
                }
                entityBrewingCauldron.decrementLiquidLevel();
                world.notifyBlockUpdate(pos, state, state, 3);
            }
        } else if (entityBrewingCauldron.applyIngredient(heldItemStack)) {
            if (heldItemStack.getItem() == Cauldron.POTION_ITEM) {
                heldItemStack.stackSize--;
                if (heldItemStack.stackSize <= 0) {
                    entityplayer.inventory.setInventorySlotContents(entityplayer.inventory.currentItem, ItemStack.EMPTY);
                }
                entityplayer.inventory.addItemStackToInventory(new ItemStack(Items.GLASS_BOTTLE));
            } else {
                heldItemStack.stackSize--;
                if (heldItemStack.stackSize <= 0) {
                    entityplayer.inventory.setInventorySlotContents(entityplayer.inventory.currentItem, ItemStack.EMPTY);
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

    public EnumBlockRenderType getRenderType(IBlockState p_149645_1_) {
        return EnumBlockRenderType.MODEL;
    }

    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return Cauldron.BREWING_CAULDRON;
    }

    public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
        return new ItemStack(Cauldron.BREWING_CAULDRON);
    }
}