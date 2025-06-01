// src/main/java/net/trashpanda/beaconwars/events/BlockEventHandler.java
package net.trashpanda.beaconwars.events;

import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.trashpanda.beaconwars.BeaconWars;
import net.trashpanda.beaconwars.block.ModBlocks;

import net.minecraft.core.BlockPos;
// NEW IMPORT: Import your data class
import net.trashpanda.beaconwars.data.BeaconLocationData;


@Mod.EventBusSubscriber(modid = BeaconWars.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class BlockEventHandler {

    @SubscribeEvent
    public static void onBlockPlaced(BlockEvent.EntityPlaceEvent event) {
        // Only proceed if a block was placed and it's your specific block
        if (event.getPlacedBlock().is(ModBlocks.BASE_BEACON.get())) {
            // Check if the entity placing it is a player, and it's server-side
            if (event.getEntity() instanceof ServerPlayer serverPlayer && !event.getLevel().isClientSide()) {
                Level level = (Level) event.getLevel();

                // Set the respawn position (this is good for immediate session, but not persistent
                // across death/respawn lifecycle without explicit saving, handled by our file system now)
                serverPlayer.setRespawnPosition(level.dimension(), event.getPos(), 0.0F, false, false);
                serverPlayer.sendSystemMessage(Component.literal("§aYour respawn point has been set to the Beacon Block!"));
                serverPlayer.sendSystemMessage(Component.literal("DEBUG: [BlockEventHandler] Respawn point set on Place at " + event.getPos() + " in " + level.dimension().location()));

                // --- CRITICAL CHANGE: Use BeaconWarsWorldData to store the beacon location ---
                // Create a BeaconLocationData object from the placed block's position and dimension
                BeaconLocationData beaconData = new BeaconLocationData(event.getPos(), level.dimension());

                // Store it in your world data manager using the player's UUID
                // This call will internally save the data to the JSON file immediately.
                BeaconWars.worldData.setPlayerBeacon(serverPlayer.getUUID(), beaconData);

                serverPlayer.sendSystemMessage(Component.literal("DEBUG: [BlockEventHandler] Saved Base Beacon to external file for: " + serverPlayer.getName().getString() + " at " + event.getPos() + " in " + level.dimension().location()));
                // --- END CRITICAL CHANGE ---

                // REMOVED ALL OLD NBT LOGIC:
                // CompoundTag persistentData = serverPlayer.getPersistentData();
                // CompoundTag customBeaconData = persistentData.getCompound("BeaconWarsTemp");
                // customBeaconData.putLong("BaseBeaconX", event.getPos().getX());
                // customBeaconData.putLong("BaseBeaconY", event.getPos().getY());
                // customBeaconData.putLong("BaseBeaconZ", event.getPos().getZ());
                // customBeaconData.putString("BaseBeaconDimension", level.dimension().location().toString());
                // persistentData.put("BeaconWarsTemp", customBeaconData);
                // serverPlayer.sendSystemMessage(Component.literal("DEBUG: [BlockEventHandler] Stored Base Beacon to player persistent data (temp)."));
            }
        }
    }
}