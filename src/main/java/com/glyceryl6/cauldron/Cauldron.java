package com.glyceryl6.cauldron;

import com.glyceryl6.cauldron.block.BlockBrewingCauldron;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlockSpecial;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod(modid = Cauldron.MODID, name = Cauldron.NAME, version = Cauldron.VERSION)
public class Cauldron {

    public static final String MODID = "beta_brewing_system";
    public static final String NAME = "Beta Brewing System";
    public static final String VERSION = "1.1.0";

    @ObjectHolder("beta_brewing_system:brewing_cauldron")
    public static Item BREWING_CAULDRON;
    @ObjectHolder("beta_brewing_system:brewing_cauldron_block")
    public static BlockBrewingCauldron BLOCK_BREWING_CAULDRON;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {

    }

    @SubscribeEvent
    public void registerBlock(RegistryEvent.Register<Block> event) {
        BLOCK_BREWING_CAULDRON = new BlockBrewingCauldron();
        BLOCK_BREWING_CAULDRON.setHardness(2.0F);
        BLOCK_BREWING_CAULDRON.setUnlocalizedName("brewing_cauldron_block");
        BLOCK_BREWING_CAULDRON.setRegistryName(MODID, "brewing_cauldron_block");
        event.getRegistry().register(BLOCK_BREWING_CAULDRON);
    }

    @SubscribeEvent
    public void registerItem(RegistryEvent.Register<Item> event) {
        BREWING_CAULDRON = new ItemBlockSpecial(BLOCK_BREWING_CAULDRON);
        BREWING_CAULDRON.setCreativeTab(CreativeTabs.BREWING);
        BREWING_CAULDRON.setUnlocalizedName("brewing_cauldron");
        BREWING_CAULDRON.setRegistryName(MODID, "brewing_cauldron");
        event.getRegistry().register(BREWING_CAULDRON);
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void registerModel(ModelRegistryEvent event) {
        ModelLoader.setCustomModelResourceLocation(BREWING_CAULDRON, 0, new ModelResourceLocation(BREWING_CAULDRON.getRegistryName(), "inventory"));
    }

}