package com.glyceryl6.cauldron;

import com.glyceryl6.cauldron.block.BlockBrewingCauldron;
import com.glyceryl6.cauldron.block.EntityBrewingCauldron;
import com.glyceryl6.cauldron.event.PotionImpactEntityEvent;
import com.glyceryl6.cauldron.item.ItemBrewingCauldronLingeringPotion;
import com.glyceryl6.cauldron.item.ItemBrewingCauldronPotion;
import com.glyceryl6.cauldron.item.ItemBrewingCauldronSplashPotion;
import com.glyceryl6.cauldron.item.ItemBrewingCauldronTippedArrow;
import com.glyceryl6.cauldron.render.PotionRenderExtend;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlockSpecial;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.BiomeColorHelper;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@Mod(modid = Cauldron.MODID, name = Cauldron.NAME, version = Cauldron.VERSION)
public class Cauldron {

    public static final String MODID = "beta_brewing_system";
    public static final String NAME = "Beta Brewing System";
    public static final String VERSION = "1.2.0";

    public static Item BREWING_CAULDRON, SPLASH_GLASS_BOTTLE, LINGERING_GLASS_BOTTLE;
    public static ItemBrewingCauldronPotion POTION_ITEM;
    public static ItemBrewingCauldronSplashPotion SPLASH_POTION_ITEM;
    public static ItemBrewingCauldronLingeringPotion LINGERING_POTION_ITEM;
    public static ItemBrewingCauldronTippedArrow TIPPED_ARROW_ITEM;
    public static BlockBrewingCauldron BREWING_CAULDRON_BLOCK;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new PotionImpactEntityEvent());
        MinecraftForge.EVENT_BUS.register(this);
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        String path = "brewing_cauldron_block_entity";
        ResourceLocation resource = new ResourceLocation(MODID, path);
        GameRegistry.registerTileEntity(EntityBrewingCauldron.class, resource);
        this.registerPotionRender();
        this.registerColors();
    }

    @SideOnly(Side.CLIENT)
    public void registerPotionRender() {
        Minecraft minecraft = Minecraft.getMinecraft();
        RenderItem renderItem = minecraft.getRenderItem();
        RenderManager renderManager = minecraft.getRenderManager();
        renderManager.entityRenderMap.keySet().remove(EntityPotion.class);
        renderManager.entityRenderMap.put(EntityPotion.class,
                new PotionRenderExtend(renderManager, renderItem));
    }

    @SideOnly(Side.CLIENT)
    public void registerColors() {
        Minecraft minecraft = Minecraft.getMinecraft();
        ItemColors itemColors = minecraft.getItemColors();
        BlockColors blockColors = minecraft.getBlockColors();
        itemColors.registerItemColorHandler((stack, tintIndex) -> {
            NBTTagCompound compound = stack.getTagCompound();
            return compound != null ? (tintIndex > 0 ? compound.getInteger("Color") : -1) : -1;
        }, POTION_ITEM, SPLASH_POTION_ITEM, LINGERING_POTION_ITEM, TIPPED_ARROW_ITEM);
        blockColors.registerBlockColorHandler((state, world, pos, tintIndex) -> {
            int color = -1;
            if (world != null && pos != null && tintIndex == 0) {
                TileEntity tileEntity = world.getTileEntity(pos);
                color = BiomeColorHelper.getWaterColorAtPos(world, pos);
                EntityBrewingCauldron entityBrewingCauldron = (EntityBrewingCauldron)tileEntity;
                if (entityBrewingCauldron != null) {
                    if (entityBrewingCauldron.getLiquidData() != 0) {
                        color = Math.abs(entityBrewingCauldron.getLiquidColor());
                    } else {
                        color = BiomeColorHelper.getWaterColorAtPos(world, pos);
                    }
                }
            }
            return color;
        }, BREWING_CAULDRON_BLOCK);
    }

    @SubscribeEvent
    public void registerBlock(RegistryEvent.Register<Block> event) {
        BREWING_CAULDRON_BLOCK = new BlockBrewingCauldron();
        BREWING_CAULDRON_BLOCK.setHardness(2.0F);
        BREWING_CAULDRON_BLOCK.setUnlocalizedName("brewing_cauldron_block");
        BREWING_CAULDRON_BLOCK.setRegistryName(MODID, "brewing_cauldron_block");
        event.getRegistry().registerAll(BREWING_CAULDRON_BLOCK);
    }

    @SubscribeEvent
    public void registerItem(RegistryEvent.Register<Item> event) {
        BREWING_CAULDRON = (new ItemBlockSpecial(BREWING_CAULDRON_BLOCK)).setCreativeTab(CreativeTabs.BREWING).setUnlocalizedName("brewing_cauldron").setRegistryName(MODID, "brewing_cauldron");
        SPLASH_GLASS_BOTTLE = (new Item()).setCreativeTab(CreativeTabs.BREWING).setUnlocalizedName("splash_glass_bottle").setRegistryName("minecraft", "splash_glass_bottle");
        LINGERING_GLASS_BOTTLE = (new Item()).setCreativeTab(CreativeTabs.BREWING).setUnlocalizedName("lingering_glass_bottle").setRegistryName("minecraft", "lingering_glass_bottle");
        POTION_ITEM = (ItemBrewingCauldronPotion) (new ItemBrewingCauldronPotion()).setUnlocalizedName("potion_cauldron").setRegistryName(MODID, "potion_cauldron");
        SPLASH_POTION_ITEM = (ItemBrewingCauldronSplashPotion) (new ItemBrewingCauldronSplashPotion()).setUnlocalizedName("splash_potion_cauldron").setRegistryName(MODID, "splash_potion_cauldron");
        LINGERING_POTION_ITEM = (ItemBrewingCauldronLingeringPotion) (new ItemBrewingCauldronLingeringPotion()).setUnlocalizedName("lingering_potion_cauldron").setRegistryName(MODID, "lingering_potion_cauldron");
        TIPPED_ARROW_ITEM = (ItemBrewingCauldronTippedArrow) (new ItemBrewingCauldronTippedArrow()).setUnlocalizedName("potion_arrow").setRegistryName(MODID, "potion_arrow");
        event.getRegistry().registerAll(BREWING_CAULDRON, SPLASH_GLASS_BOTTLE, LINGERING_GLASS_BOTTLE, POTION_ITEM, SPLASH_POTION_ITEM, LINGERING_POTION_ITEM, TIPPED_ARROW_ITEM);
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void registerModel(ModelRegistryEvent event) {
        ModelLoader.setCustomModelResourceLocation(BREWING_CAULDRON, 0, new ModelResourceLocation(BREWING_CAULDRON.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(SPLASH_GLASS_BOTTLE, 0, new ModelResourceLocation(SPLASH_GLASS_BOTTLE.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(LINGERING_GLASS_BOTTLE, 0, new ModelResourceLocation(LINGERING_GLASS_BOTTLE.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(POTION_ITEM, 0, new ModelResourceLocation(POTION_ITEM.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(SPLASH_POTION_ITEM, 0, new ModelResourceLocation(SPLASH_POTION_ITEM.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(LINGERING_POTION_ITEM, 0, new ModelResourceLocation(LINGERING_POTION_ITEM.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(TIPPED_ARROW_ITEM, 0, new ModelResourceLocation(TIPPED_ARROW_ITEM.getRegistryName(), "inventory"));
    }

}