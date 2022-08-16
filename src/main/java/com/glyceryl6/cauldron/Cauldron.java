package com.glyceryl6.cauldron;

import com.glyceryl6.cauldron.block.BlockBrewingCauldron;
import com.glyceryl6.cauldron.block.EntityBrewingCauldron;
import com.glyceryl6.cauldron.block.ModFluidClassic;
import com.glyceryl6.cauldron.item.ItemBrewingCauldronPotion;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlockSpecial;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod(modid = Cauldron.MODID, name = Cauldron.NAME, version = Cauldron.VERSION)
public class Cauldron {

    public static final String MODID = "beta_brewing_system";
    public static final String NAME = "Beta Brewing System";
    public static final String VERSION = "1.1.0";

    public static final Fluid POTION_FLUID = new Fluid("potion_fluid",
            new ResourceLocation(MODID, "potion_fluid_still"),
            new ResourceLocation(MODID, "potion_fluid_flow"));

    public static Item BREWING_CAULDRON;
    public static Block POTION_FLUID_BLOCK;
    public static ItemBrewingCauldronPotion POTION_ITEM;
    public static BlockBrewingCauldron BREWING_CAULDRON_BLOCK;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        POTION_FLUID.setUnlocalizedName("potion_fluid");
        FluidRegistry.registerFluid(POTION_FLUID);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        String path = "brewing_cauldron_block_entity";
        ResourceLocation resource = new ResourceLocation(MODID, path);
        GameRegistry.registerTileEntity(EntityBrewingCauldron.class, resource);
        this.registerColors();
    }

    @SideOnly(Side.CLIENT)
    public void registerColors() {
        Minecraft minecraft = Minecraft.getMinecraft();
        ItemColors itemColors = minecraft.getItemColors();
        itemColors.registerItemColorHandler((stack, tintIndex) -> {
            NBTTagCompound compound = stack.getTagCompound();
            return compound != null ? (tintIndex > 0 ? compound.getInteger("Color") : -1) : -1;
            }, POTION_ITEM);
    }

    @SubscribeEvent
    public void registerBlock(RegistryEvent.Register<Block> event) {
        POTION_FLUID_BLOCK = new ModFluidClassic(POTION_FLUID, Material.WATER);
        POTION_FLUID_BLOCK.setHardness(100.0F);
        POTION_FLUID_BLOCK.setLightOpacity(3).disableStats();
        POTION_FLUID_BLOCK.setUnlocalizedName("potion_fluid");
        POTION_FLUID_BLOCK.setRegistryName(MODID, "potion_fluid");
        BREWING_CAULDRON_BLOCK = new BlockBrewingCauldron();
        BREWING_CAULDRON_BLOCK.setHardness(2.0F);
        BREWING_CAULDRON_BLOCK.setUnlocalizedName("brewing_cauldron_block");
        BREWING_CAULDRON_BLOCK.setRegistryName(MODID, "brewing_cauldron_block");
        event.getRegistry().registerAll(POTION_FLUID_BLOCK, BREWING_CAULDRON_BLOCK);
    }

    @SubscribeEvent
    public void registerItem(RegistryEvent.Register<Item> event) {
        POTION_ITEM = (ItemBrewingCauldronPotion)
                (new ItemBrewingCauldronPotion())
                .setUnlocalizedName("potion_cauldron")
                .setRegistryName(MODID, "potion_cauldron");
        BREWING_CAULDRON = (new ItemBlockSpecial(BREWING_CAULDRON_BLOCK))
                .setCreativeTab(CreativeTabs.BREWING)
                .setUnlocalizedName("brewing_cauldron")
                .setRegistryName(MODID, "brewing_cauldron");
        event.getRegistry().registerAll(BREWING_CAULDRON, POTION_ITEM);
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void registerModel(ModelRegistryEvent event) {
        ModelLoader.setCustomModelResourceLocation(BREWING_CAULDRON, 0, new ModelResourceLocation(BREWING_CAULDRON.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(POTION_ITEM, 0, new ModelResourceLocation(POTION_ITEM.getRegistryName(), "inventory"));
    }

}