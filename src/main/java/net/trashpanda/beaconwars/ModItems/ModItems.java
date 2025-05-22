package net.trashpanda.beaconwars.ModItems;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.trashpanda.beaconwars.BeaconWars;
import net.trashpanda.beaconwars.block.ModBlocks; // Import your ModBlocks class

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, BeaconWars.MOD_ID);

    // Register a BlockItem for your BASE_BEACON
    // The Item.Properties() are crucial here.
    public static final RegistryObject<Item> BASE_BEACON_ITEM = ITEMS.register("base_beacon",
            () -> new BlockItem(ModBlocks.BASE_BEACON.get(), new Item.Properties()));


    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}