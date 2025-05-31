// src/main/java/net/trashpanda/beaconwars/event/BlockEventHandler.java
package net.trashpanda.beaconwars.events;

import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;

import net.minecraftforge.event.level.BlockEvent; // Import for BlockEvent
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import net.trashpanda.beaconwars.BeaconWars;
import net.trashpanda.beaconwars.block.ModBlocks;

@Mod.EventBusSubscriber(modid = BeaconWars.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class BlockEventHandler {

    @SubscribeEvent
    public static void onBlockPlaced(BlockEvent.EntityPlaceEvent event) {
        // Only proceed if a block was placed and it's your specific block
        if (event.getPlacedBlock().is(ModBlocks.BASE_BEACON.get())) {
            // Check if the entity placing it is a player, and it's server-side
            if (event.getEntity() instanceof ServerPlayer serverPlayer && !event.getLevel().isClientSide()) {
                Level level = (Level) event.getLevel(); // Cast IWorldReader to Level
                serverPlayer.setRespawnPosition(level.dimension(), event.getPos(), 0.0F, false, false);
                serverPlayer.sendSystemMessage(Component.literal("§aYour respawn point has been set to the Beacon Block!"));
                serverPlayer.sendSystemMessage(Component.literal("DEBUG: [BlockEventHandler] Respawn point set on Place at " + event.getPos().toString() + " in " + level.dimension().location()));
            }
        }
    }
}