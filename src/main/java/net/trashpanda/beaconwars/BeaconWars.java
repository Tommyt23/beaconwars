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
import net.trashpanda.beaconwars.modTab.ModCreativeModTabs;
import org.slf4j.Logger;

// NEW IMPORTS FOR COMMAND REGISTRATION:
import net.minecraftforge.event.RegisterCommandsEvent; // <--- ADD THIS IMPORT
import net.trashpanda.beaconwars.commands.BeaconWarsCommand; // <--- ADD THIS IMPORT


// The value here should match an entry in the META-INF/mods.toml file
@Mod(BeaconWars.MOD_ID)
public class BeaconWars {
    // Define mod id in a common place for everything to reference
    public static final String MOD_ID = "beaconwars";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();

    public BeaconWars(FMLJavaModLoadingContext context)
    {
        IEventBus modEventBus = context.getModEventBus();

        ModCreativeModTabs.register(modEventBus);

        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);

        modEventBus.addListener(this::commonSetup);

        // Register to the main MinecraftForge.EVENT_BUS for events like RegisterCommandsEvent
        MinecraftForge.EVENT_BUS.register(this); // This line is correct for its purpose
        MinecraftForge.EVENT_BUS.register(new net.trashpanda.beaconwars.events.BlockEventHandler());

        modEventBus.addListener(this::addCreative);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {

    }

    // Add the example block item to the building blocks tab
    private void addCreative(BuildCreativeModeTabContentsEvent event){
        // Your existing creative tab code
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
        // Your existing onServerStarting code
    }

    // NEW: Command registration event listener
    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        BeaconWarsCommand.register(event.getDispatcher());
        LOGGER.info("DEBUG: [BeaconWars] Registered Beacon Wars commands."); // Using LOGGER for better logging
    }


    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            // Some client setup code
            LOGGER.info("HELLO FROM CLIENT SETUP");
            LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
        }
    }
}