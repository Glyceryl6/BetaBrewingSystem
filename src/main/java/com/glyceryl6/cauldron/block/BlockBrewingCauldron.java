package com.glyceryl6.cauldron.block;

import com.glyceryl6.cauldron.Cauldron;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Random;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
@SuppressWarnings("deprecation")
public class BlockBrewingCauldron extends BlockContainer {

    private static final PropertyInteger LEVEL = PropertyInteger.create("level", 0, 3);
    private static final AxisAlignedBB AABB_LEGS = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.3125D, 1.0D);
    private static final AxisAlignedBB AABB_WALL_EAST = new AxisAlignedBB(0.875D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
    private static final AxisAlignedBB AABB_WALL_WEST = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.125D, 1.0D, 1.0D);
    private static final AxisAlignedBB AABB_WALL_SOUTH = new AxisAlignedBB(0.0D, 0.0D, 0.875D, 1.0D, 1.0D, 1.0D);
    private static final AxisAlignedBB AABB_WALL_NORTH = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 0.125D);

    public BlockBrewingCauldron() {
        super(Material.IRON, MapColor.STONE);
        this.setHarvestLevel("pickaxe", 0);
        this.setDefaultState(this.blockState.getBaseState().withProperty(LEVEL, 0));
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new EntityBrewingCauldron();
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public void addCollisionBoxToList(IBlockState state, World world, BlockPos pos, AxisAlignedBB entityBox,
                                      List<AxisAlignedBB> collidingBoxes, @Nullable Entity entity, boolean isActualState) {
        addCollisionBoxToList(pos, entityBox, collidingBoxes, AABB_LEGS);
        addCollisionBoxToList(pos, entityBox, collidingBoxes, AABB_WALL_WEST);
        addCollisionBoxToList(pos, entityBox, collidingBoxes, AABB_WALL_NORTH);
        addCollisionBoxToList(pos, entityBox, collidingBoxes, AABB_WALL_EAST);
        addCollisionBoxToList(pos, entityBox, collidingBoxes, AABB_WALL_SOUTH);
    }

    public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity entity) {
        int i = state.getValue(LEVEL);
        float f = (float)pos.getY() + (6.0F + (float)(3 * i)) / 16.0F;
        if (!world.isRemote && entity.isBurning() && i > 0 && entity.getEntityBoundingBox().minY <= (double)f) {
            entity.extinguish();
        }
    }

    public void setWaterLevel(World world, BlockPos pos, IBlockState state, int level) {
        IBlockState newState = state.withProperty(LEVEL, MathHelper.clamp(level, 0, 3));
        world.setBlockState(pos, newState);
        world.notifyBlockUpdate(pos, state, state, 3);
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player,
                                    EnumHand hand, EnumFacing facing, float f1, float f2, float f3) {
        if (world.isRemote) world.notifyBlockUpdate(pos, state, state, 3);
        ItemStack heldItemStack = player.inventory.getCurrentItem();
        TileEntity tileEntity = world.getTileEntity(pos);
        if (!(tileEntity instanceof EntityBrewingCauldron)) return true;
        EntityBrewingCauldron entityBrewingCauldron = (EntityBrewingCauldron)tileEntity;
        int cauldronMetadata = entityBrewingCauldron.getLiquidData();
        if (heldItemStack.getItem() == Items.WATER_BUCKET) {
            setWaterLevel(world, pos, state, 3);
            if (entityBrewingCauldron.fillCauldronWithWaterBucket()) {
                if (!player.capabilities.isCreativeMode) {
                    heldItemStack.shrink(1);
                    player.inventory.addItemStackToInventory(new ItemStack(Items.BUCKET));
                }
                if (!entityBrewingCauldron.isCauldronDataZero() && cauldronMetadata != entityBrewingCauldron.getLiquidData()) {
                    world.notifyBlockUpdate(pos, state, state, 3);
                }
                world.playSound(null, pos, SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);
            }
            return true;
        }
        if (heldItemStack.getItem() == Items.GLASS_BOTTLE ||
                heldItemStack.getItem() == Cauldron.SPLASH_GLASS_BOTTLE ||
                heldItemStack.getItem() == Cauldron.LINGERING_GLASS_BOTTLE) {
            if (!entityBrewingCauldron.isCauldronEmpty()) {
                Item potionItem;
                if (heldItemStack.getItem() == Items.GLASS_BOTTLE) {
                    potionItem = Cauldron.POTION_ITEM;
                } else if (heldItemStack.getItem() == Cauldron.SPLASH_GLASS_BOTTLE) {
                    potionItem = Cauldron.SPLASH_POTION_ITEM;
                } else {
                    potionItem = Cauldron.LINGERING_POTION_ITEM;
                }
                ItemStack potionItemStack = new ItemStack(potionItem, 1, entityBrewingCauldron.getLiquidData());
                NBTTagCompound compoundTag = new NBTTagCompound();
                if (!potionItemStack.hasTagCompound()) {
                    potionItemStack.setTagCompound(compoundTag);
                }
                compoundTag.setBoolean("Unbreakable", true);
                compoundTag.setInteger("HideFlags", 4);
                compoundTag.setInteger("Color", Math.abs(entityBrewingCauldron.getLiquidColor()));
                if (!player.inventory.addItemStackToInventory(potionItemStack)) {
                    world.spawnEntity(new EntityItem(world, pos.getX() + 0.5D, pos.getY() + 1.5D, pos.getZ() + 0.5D, potionItemStack));
                }
                if (!player.capabilities.isCreativeMode) {
                    heldItemStack.stackSize--;
                    entityBrewingCauldron.decrementLiquidLevel();
                    setWaterLevel(world, pos, state, state.getValue(LEVEL) - 1);
                }
                if (heldItemStack.stackSize <= 0) {
                    player.inventory.setInventorySlotContents(player.inventory.currentItem, ItemStack.EMPTY);
                }
                world.playSound(null, pos, SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.BLOCKS, 1.0F, 1.0F);
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
            world.playSound(null, pos, SoundEvents.ITEM_BUCKET_FILL, SoundCategory.BLOCKS, 1.0F, 1.0F);
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

    public ItemStack getItem(World world, BlockPos pos, IBlockState state) {
        return new ItemStack(Cauldron.BREWING_CAULDRON);
    }

    public int getMetaFromState(IBlockState state) {
        return state.getValue(LEVEL);
    }

    public boolean isPassable(IBlockAccess worldIn, BlockPos pos) {
        return true;
    }

    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(LEVEL, meta);
    }

    public BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, LEVEL);
    }

    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        if (face == EnumFacing.UP) {
            return BlockFaceShape.BOWL;
        } else {
            return face == EnumFacing.DOWN ? BlockFaceShape.UNDEFINED : BlockFaceShape.SOLID;
        }
    }

}