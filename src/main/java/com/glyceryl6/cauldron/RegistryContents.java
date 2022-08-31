package com.glyceryl6.cauldron;

import com.glyceryl6.cauldron.block.BrewingCauldronBlock;
import com.glyceryl6.cauldron.block.BrewingCauldronEntity;
import com.glyceryl6.cauldron.item.*;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.*;

public class RegistryContents {

    private static final DeferredRegister<Item> ITEMS = create(ForgeRegistries.ITEMS);
    private static final DeferredRegister<Block> BLOCKS = create(ForgeRegistries.BLOCKS);
    private static final DeferredRegister<BlockEntityType<?>> CONTAINER = create(ForgeRegistries.BLOCK_ENTITIES);
    //由于添加的内容比较少，故将物品、方块等一同放在一个类中进行注册。
    public static final RegistryObject<Block> BREWING_CAULDRON_BLOCK = BLOCKS.register("brewing_cauldron_block", () -> new BrewingCauldronBlock(BlockBehaviour.Properties.copy(Blocks.CAULDRON)));
    public static final RegistryObject<Item> BREWING_CAULDRON = ITEMS.register("brewing_cauldron", () -> new ItemNameBlockItem(BREWING_CAULDRON_BLOCK.get(), new Item.Properties().tab(CreativeModeTab.TAB_BREWING)));
    public static final RegistryObject<Item> SPLASH_GLASS_BOTTLE = ITEMS.register("splash_glass_bottle", () -> new Item(new Item.Properties().tab(CreativeModeTab.TAB_BREWING)));
    public static final RegistryObject<Item> LINGERING_GLASS_BOTTLE = ITEMS.register("lingering_glass_bottle", () -> new Item(new Item.Properties().tab(CreativeModeTab.TAB_BREWING)));
    public static final RegistryObject<Item> POTION_ITEM = ITEMS.register("potion_cauldron", () -> new SpecialPotion(new Item.Properties()));
    public static final RegistryObject<Item> SPLASH_POTION_ITEM = ITEMS.register("splash_potion_cauldron", () -> new SpecialSplashPotion(new Item.Properties()));
    public static final RegistryObject<Item> LINGERING_POTION_ITEM = ITEMS.register("lingering_potion_cauldron", () -> new SpecialLingeringPotion(new Item.Properties()));
    public static final RegistryObject<Item> TIPPED_ARROW_ITEM = ITEMS.register("potion_arrow", () -> new SpecialTippedArrow(new Item.Properties()));
    public static final RegistryObject<BlockEntityType<BrewingCauldronEntity>> BREWING_CAULDRON_ENTITY = CONTAINER.register("brewing_cauldron_entity",
            () -> BlockEntityType.Builder.of(BrewingCauldronEntity::new, BREWING_CAULDRON_BLOCK.get()).build(null));

    private static <B extends IForgeRegistryEntry<B>> DeferredRegister<B> create(IForgeRegistry<B> reg) {
        return DeferredRegister.create(reg, BetaBrewingSystem.MOD_ID);
    }

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
        BLOCKS.register(eventBus);
        CONTAINER.register(eventBus);
    }

}