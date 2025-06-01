// src/main/java/net/trashpanda/beaconwars/events/GamemodeChangeHandler.java
package net.trashpanda.beaconwars.events;

import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.trashpanda.beaconwars.BeaconWars;
import net.trashpanda.beaconwars.util.PendingGamemodeChangeManager;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.trashpanda.beaconwars.block.ModBlocks;
import net.trashpanda.beaconwars.util.GamemodeStateManager;

// IMPORTS FOR SPAWN POINT MANAGEMENT AND TELEPORTATION
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.resources.ResourceLocation;
import net.trashpanda.beaconwars.ModItems.ModItems;

// NEW IMPORT FOR WORLD DATA:
import net.trashpanda.beaconwars.data.BeaconLocationData;


@Mod.EventBusSubscriber(modid = BeaconWars.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class GamemodeChangeHandler {

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            System.out.println("DEBUG: [RespawnHandler] PlayerRespawnEvent fired for " + serverPlayer.getName().getString());
            System.out.println("DEBUG: [RespawnHandler] CURRENT GamemodeStateManager state: " + GamemodeStateManager.isBeaconWarsGamemodeActive());

            BlockPos effectiveRespawnPos = null;
            ResourceKey<Level> effectiveRespawnDimension = null;

            if (GamemodeStateManager.isBeaconWarsGamemodeActive()) {
                // Get beacon data from your central data manager
                BeaconLocationData beaconData = BeaconWars.worldData.getPlayerBeacon(serverPlayer.getUUID());

                if (beaconData != null) {
                    effectiveRespawnPos = beaconData.toBlockPos();
                    effectiveRespawnDimension = beaconData.toDimensionResourceKey();
                    System.out.println("DEBUG: [RespawnHandler] Loaded persistent Base Beacon data from file: " + effectiveRespawnPos + " in " + effectiveRespawnDimension.location());
                } else {
                    // --- CRITICAL NEW LOGIC: NO BEACON FOUND, SET TO SPECTATOR ---
                    serverPlayer.sendSystemMessage(Component.literal("§cYou have no Base Beacon! Entering Spectator mode."));
                    System.out.println("DEBUG: [RespawnHandler] Beacon Wars Gamemode active but NO beacon found for player. Setting to SPECTATOR.");

                    // Set player's gamemode to SPECTATOR
                    serverPlayer.setGameMode(GameType.SPECTATOR);
                    // Optional: You might want to teleport them to world spawn or a specific spectator point
                    // serverPlayer.teleportTo(serverPlayer.getServer().overworld().getSharedSpawnPos().getX(),
                    //                       serverPlayer.getServer().overworld().getSharedSpawnPos().getY(),
                    //                       serverPlayer.getServer().overworld().getSharedSpawnPos().getZ());

                    return; // EXIT EARLY: Player is now in spectator mode, no further respawn/item logic for them
                    // --- END CRITICAL NEW LOGIC ---
                }
            } else {
                System.out.println("DEBUG: [RespawnHandler] Beacon Wars Gamemode is INACTIVE. Not checking for custom beacon respawn.");
                // If gamemode is inactive, ensure beacon data is removed from file (just in case)
                if (BeaconWars.worldData.hasPlayerBeacon(serverPlayer.getUUID())) {
                    BeaconWars.worldData.removePlayerBeacon(serverPlayer.getUUID());
                    serverPlayer.sendSystemMessage(Component.literal("DEBUG: [RespawnHandler] Removed Base Beacon data from file because gamemode is inactive."));
                }
                serverPlayer.sendSystemMessage(Component.literal("DEBUG: [RespawnHandler] Respawning at default Minecraft spawn."));
            }

            // Only proceed with custom teleportation if the effective respawn point was found
            // This entire block only runs if beaconData was NOT null above.
            if (effectiveRespawnPos != null && effectiveRespawnDimension != null) {
                System.out.println("DEBUG: [RespawnHandler] Attempting custom respawn to beacon.");

                var server = serverPlayer.getServer();

                if (server != null) {
                    ServerLevel targetLevel = server.getLevel(effectiveRespawnDimension);

                    if (targetLevel != null) {
                        // Check if the block at the respawn position in the target dimension is still a BASE_BEACON
                        if (targetLevel.getBlockState(effectiveRespawnPos).is(ModBlocks.BASE_BEACON.get())) {
                            System.out.println("DEBUG: [RespawnHandler] Valid Base Beacon found at " + effectiveRespawnPos + " in " + effectiveRespawnDimension.location());

                            if (serverPlayer.level().dimension() != effectiveRespawnDimension) {
                                System.out.println("DEBUG: [RespawnHandler] Changing dimension from " + serverPlayer.level().dimension().location() + " to " + effectiveRespawnDimension.location());
                                serverPlayer.changeDimension(targetLevel);
                            }
                            serverPlayer.teleportTo(effectiveRespawnPos.getX() + 0.5, effectiveRespawnPos.getY() + 1.0, effectiveRespawnPos.getZ() + 0.5);
                            serverPlayer.sendSystemMessage(Component.literal("§aYou respawned at your Base Beacon!"));
                            System.out.println("DEBUG: [RespawnHandler] Player " + serverPlayer.getName().getString() + " respawned at their Base Beacon at " + effectiveRespawnPos + " in " + effectiveRespawnDimension.location());
                        } else {
                            // Beacon is broken, revert to default spawn / spectator logic here
                            serverPlayer.sendSystemMessage(Component.literal("§cYour Base Beacon was destroyed! Entering Spectator mode."));
                            System.out.println("DEBUG: [RespawnHandler] Base Beacon at " + effectiveRespawnPos + " was destroyed. Setting to SPECTATOR.");
                            BeaconWars.worldData.removePlayerBeacon(serverPlayer.getUUID()); // Remove from file data
                            serverPlayer.sendSystemMessage(Component.literal("DEBUG: [RespawnHandler] Removed Base Beacon data from file."));
                            serverPlayer.setRespawnPosition(Level.OVERWORLD, null, 0.0F, false, false); // Clear vanilla respawn

                            // Set player's gamemode to SPECTATOR
                            serverPlayer.setGameMode(GameType.SPECTATOR);
                            // No further respawn/item logic for them, they are out of the game.
                            return;
                        }
                    } else {
                        serverPlayer.sendSystemMessage(Component.literal("DEBUG: [RespawnHandler] Target respawn dimension (" + effectiveRespawnDimension.location() + ") not found on server. Entering Spectator mode."));
                        BeaconWars.worldData.removePlayerBeacon(serverPlayer.getUUID()); // Remove from file data
                        serverPlayer.sendSystemMessage(Component.literal("DEBUG: [RespawnHandler] Removed Base Beacon data from file due to invalid dimension."));
                        serverPlayer.setRespawnPosition(Level.OVERWORLD, null, 0.0F, false, false);

                        // Set player's gamemode to SPECTATOR
                        serverPlayer.setGameMode(GameType.SPECTATOR);
                        return;
                    }
                } else {
                    serverPlayer.sendSystemMessage(Component.literal("DEBUG: [RespawnHandler] Server instance is null. Cannot manage respawn. Defaulting to vanilla respawn."));
                    // In this rare case, let vanilla handle it if server is null.
                }
            } else {
                // This branch should ideally not be hit if GamemodeStateManager.isBeaconWarsGamemodeActive() is true and beaconData is null,
                // because the 'return' statement above would have already taken effect.
                // It mostly applies if the gamemode is NOT active.
                serverPlayer.sendSystemMessage(Component.literal("DEBUG: [RespawnHandler] No custom beacon respawn point determined. Allowing vanilla respawn."));
            }

            // --- Existing Gamemode Change Logic (keep this as is) ---
            // This part will only execute if the player successfully respawned at a beacon or the gamemode was inactive.
            GameType targetGameType = PendingGamemodeChangeManager.getPendingChange(serverPlayer);

            if (targetGameType != null) {
                System.out.println("DEBUG: [RespawnHandler] Found pending gamemode change to " + targetGameType.getName() + " for player " + serverPlayer.getName().getString());
                if (serverPlayer.gameMode.getGameModeForPlayer() != targetGameType) {
                    serverPlayer.sendSystemMessage(Component.literal("DEBUG: [RespawnHandler] Changing gamemode for " + serverPlayer.getName().getString() + " to " + targetGameType.getName()));
                    serverPlayer.getServer().execute(() -> {
                        serverPlayer.setGameMode(targetGameType);
                        serverPlayer.sendSystemMessage(Component.literal("Your gamemode has been changed to " + targetGameType.getName() + "."));
                        serverPlayer.sendSystemMessage(Component.literal("DEBUG: [RespawnHandler] Gamemode changed inside scheduled task."));
                        PendingGamemodeChangeManager.removePendingChange(serverPlayer);
                    });
                }
                else {
                    serverPlayer.sendSystemMessage(Component.literal("DEBUG: [RespawnHandler] Player " + serverPlayer.getName().getString() + " is already in target gamemode (" + targetGameType.getName() + "), not changing."));
                    PendingGamemodeChangeManager.removePendingChange(serverPlayer);
                    serverPlayer.sendSystemMessage(Component.literal("DEBUG: [RespawnHandler] Removed pending change (already in target gamemode)."));
                }
            } else {
                serverPlayer.sendSystemMessage(Component.literal("DEBUG: [RespawnHandler] No pending gamemode found for " + serverPlayer.getName().getString()));
            }

            // Giving items logic also only applies if they are NOT in spectator mode
            if (GamemodeStateManager.isBeaconWarsGamemodeActive() && serverPlayer.gameMode.getGameModeForPlayer() != GameType.SPECTATOR) {
                System.out.println("DEBUG: [RespawnHandler] Beacon Wars Gamemode is ACTIVE. Giving items on respawn.");
                if (!serverPlayer.getInventory().contains(new ItemStack(ModItems.BASE_BEACON_ITEM.get()))) {
                    giveRespawnItems(serverPlayer);
                    serverPlayer.sendSystemMessage(Component.literal("§bYou received respawn items!"));
                }
            } else if (serverPlayer.gameMode.getGameModeForPlayer() == GameType.SPECTATOR) {
                System.out.println("DEBUG: [RespawnHandler] Player is in SPECTATOR mode, not giving items.");
            } else {
                System.out.println("DEBUG: [RespawnHandler] Beacon Wars Gamemode is INACTIVE. Not giving items on respawn.");
            }

        } else {
            System.out.println("DEBUG: [RespawnHandler] Event entity is not ServerPlayer (e.g., client-side or different entity type).");
        }
    }

    private static void giveRespawnItems(ServerPlayer player) {
        player.getInventory().add(new ItemStack(Items.OAK_PLANKS, 32));
        player.getInventory().add(new ItemStack(Items.IRON_INGOT, 24));
        player.getInventory().add(new ItemStack(Items.STICKY_PISTON, 32));
        player.getInventory().add(new ItemStack(Items.PISTON, 64));
        player.getInventory().add(new ItemStack(Items.BREAD, 32));
    }
}