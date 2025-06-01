// src/main/java/net/trashpanda/beaconwars/BeaconWars.java
package net.trashpanda.beaconwars;

import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import net.trashpanda.beaconwars.ModItems.ModItems;
import net.trashpanda.beaconwars.block.ModBlocks;
import net.trashpanda.beaconwars.events.BlockEventHandler;
import net.trashpanda.beaconwars.events.CraftingRestrictionHandler;
import net.trashpanda.beaconwars.events.GamemodeChangeHandler;
import net.trashpanda.beaconwars.modTab.ModCreativeModTabs;
import org.slf4j.Logger;

// NEW IMPORTS FOR COMMAND REGISTRATION:
import net.minecraftforge.event.RegisterCommandsEvent;
import net.trashpanda.beaconwars.commands.BeaconWarsCommand;

// NEW IMPORTS FOR WORLD DATA MANAGEMENT:
import net.minecraftforge.event.server.ServerStoppingEvent; // For server stopping event
import net.trashpanda.beaconwars.data.BeaconWarsWorldData; // Import your new data manager class


@Mod(BeaconWars.MOD_ID)
public class BeaconWars {
    public static final String MOD_ID = "beaconwars";
    public static final Logger LOGGER = LogUtils.getLogger();

    // NEW: Static instance of your world data manager
    public static BeaconWarsWorldData worldData;

    public BeaconWars() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        modEventBus.addListener(this::commonSetup);

        ModBlocks.register(modEventBus);
        ModItems.register(modEventBus);
        ModCreativeModTabs.register(modEventBus);

        // Register the event handlers
        MinecraftForge.EVENT_BUS.register(this); // For ServerStartingEvent and ServerStoppingEvent
        MinecraftForge.EVENT_BUS.register(new GamemodeChangeHandler());
        MinecraftForge.EVENT_BUS.register(new CraftingRestrictionHandler());
        MinecraftForge.EVENT_BUS.register(new BlockEventHandler());
        // REMOVE THIS LINE: MinecraftForge.EVENT_BUS.register(new PlayerNBTHandler()); // THIS FILE IS DELETED

        modEventBus.addListener(this::addCreative);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        // Some common setup code, e.g., network packet registration if you had any
    }

    // Add the example block item to the building blocks tab
    private void addCreative(BuildCreativeModeTabContentsEvent event){
        // Your existing creative tab code
        if (event.getTabKey() == ModCreativeModTabs.BEACON_WARS_TAB.getKey()) {
            event.accept(ModItems.BASE_BEACON_ITEM.get());
        }
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // NEW: Initialize your world data manager when the server starts
        LOGGER.info("DEBUG: [BeaconWars] Server starting. Initializing BeaconWarsWorldData.");
        worldData = new BeaconWarsWorldData(event.getServer());
        // Your existing onServerStarting code for commands (if any)
    }

    @SubscribeEvent
    public void onServerStopping(ServerStoppingEvent event) {
        // NEW: Save data when the server is stopping
        // Note: saveData() is also called on set/remove, but this is a good final flush
        if (worldData != null) {
            LOGGER.info("DEBUG: [BeaconWars] Server stopping. Saving BeaconWarsWorldData.");
            worldData.saveData();
        }
    }

    // NEW: Command registration event listener
    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        BeaconWarsCommand.register(event.getDispatcher());
        LOGGER.info("DEBUG: [BeaconWars] Registered Beacon Wars commands.");
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            LOGGER.info("HELLO FROM CLIENT SETUP");
            LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
        }
    }
}