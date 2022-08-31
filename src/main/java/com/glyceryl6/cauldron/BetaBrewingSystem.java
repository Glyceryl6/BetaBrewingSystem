package com.glyceryl6.cauldron;

import com.glyceryl6.cauldron.block.BrewingCauldronEntity;
import com.glyceryl6.cauldron.dispenser.ShootLingeringPotion;
import com.glyceryl6.cauldron.dispenser.ShootSplashPotion;
import com.glyceryl6.cauldron.event.PotionImpactEntityEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(BetaBrewingSystem.MOD_ID)
public class BetaBrewingSystem {

    public static final String MOD_ID = "beta_brewing_system";
    public IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();

    public BetaBrewingSystem() {
        RegistryContents.register(eventBus);
        eventBus.addListener(this::registerColors);
        eventBus.addListener(this::registerDispenserBehaviors);
        MinecraftForge.EVENT_BUS.register(new PotionImpactEntityEvent());
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void registerColors(FMLClientSetupEvent event) {
        Minecraft minecraft = Minecraft.getInstance();
        ItemColors itemColors = minecraft.getItemColors();
        BlockColors blockColors = minecraft.getBlockColors();
        itemColors.register((stack, tintIndex) -> {
            CompoundTag tag = stack.getTag();
            return tag != null ? (tintIndex > 0 ? tag.getInt("Color") : -1) : -1;
        }, RegistryContents.POTION_ITEM.get(),
                RegistryContents.SPLASH_POTION_ITEM.get(),
                RegistryContents.LINGERING_POTION_ITEM.get(),
                RegistryContents.TIPPED_ARROW_ITEM.get());
        blockColors.register((state, level, pos, tintIndex) -> {
            int color = -1;
            if (level != null && pos != null && tintIndex == 0) {
                BlockEntity blockEntity = level.getBlockEntity(pos);
                if (blockEntity instanceof BrewingCauldronEntity brewingCauldron) {
                    color = Mth.abs(brewingCauldron.getLiquidColor());
                } else {
                    color = BiomeColors.getAverageWaterColor(level, pos);
                }
            }
            return color;
        }, RegistryContents.BREWING_CAULDRON_BLOCK.get());
    }

    private void registerDispenserBehaviors(FMLCommonSetupEvent event) {
        DispenserBlock.registerBehavior(RegistryContents.SPLASH_POTION_ITEM.get(), new ShootSplashPotion());
        DispenserBlock.registerBehavior(RegistryContents.SPLASH_POTION_ITEM.get(), new ShootLingeringPotion());
    }

}