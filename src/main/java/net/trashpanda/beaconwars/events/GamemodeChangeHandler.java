// src/main/java/net/trashpanda/beaconwars/events/GamemodeChangeHandler.java
package net.trashpanda.beaconwars.events;

import net.minecraft.world.level.GameType;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.trashpanda.beaconwars.BeaconWars;
import net.trashpanda.beaconwars.util.PendingGamemodeChangeManager;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.trashpanda.beaconwars.block.ModBlocks;
import net.trashpanda.beaconwars.util.GamemodeStateManager;

@Mod.EventBusSubscriber(modid = BeaconWars.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class GamemodeChangeHandler {

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            System.out.println("DEBUG: [RespawnHandler] PlayerRespawnEvent fired for " + serverPlayer.getName().getString());
            // REMOVED THE PROBLEM LINE: System.out.println("DEBUG: [RespawnHandler] Respawned with new position? " + event.isNewSpawn());

            // --- NEW LOGIC: Give items on respawn if gamemode is active ---
            if (GamemodeStateManager.isBeaconWarsGamemodeActive()) {
                System.out.println("DEBUG: [RespawnHandler] Beacon Wars Gamemode is ACTIVE. Checking items for respawn.");

                // Example: Give the player 5 wood planks
                ItemStack woodPlanks = new ItemStack(Items.OAK_PLANKS, 32);
                serverPlayer.getInventory().add(woodPlanks);
                ItemStack ironIngots = new ItemStack(Items.IRON_INGOT, 24);
                serverPlayer.getInventory().add(ironIngots);
                ItemStack stickyPiston = new ItemStack(Items.STICKY_PISTON, 32);
                serverPlayer.getInventory().add(stickyPiston);
                ItemStack piston = new ItemStack(Items.PISTON, 64);
                serverPlayer.getInventory().add(piston);
                ItemStack bread = new ItemStack(Items.BREAD, 32);
                serverPlayer.getInventory().add(bread);
                serverPlayer.sendSystemMessage(Component.literal("You received 5 oak planks on respawn!"));
                System.out.println("DEBUG: [RespawnHandler] Gave 5 oak planks to " + serverPlayer.getName().getString());

                // Example: Give the player a custom Base Beacon
//                ItemStack baseBeaconItem = new ItemStack(ModBlocks.BASE_BEACON.get().asItem(), 1);
//                serverPlayer.getInventory().add(baseBeaconItem);
//                serverPlayer.sendSystemMessage(Component.literal("You received a Base Beacon on respawn!"));
//                System.out.println("DEBUG: [RespawnHandler] Gave 1 Base Beacon to " + serverPlayer.getName().getString());

                // You can add more items here
                // ItemStack stone = new ItemStack(Items.STONE, 10);
                // serverPlayer.getInventory().add(stone);
                // serverPlayer.sendSystemMessage(Component.literal("You received 10 stone on respawn!"));

                // Make sure the client's inventory GUI updates
                if (serverPlayer.containerMenu != null) {
                    serverPlayer.containerMenu.broadcastChanges();
                }
            } else {
                System.out.println("DEBUG: [RespawnHandler] Beacon Wars Gamemode is INACTIVE. Not giving items on respawn.");
            }
            // --- END NEW LOGIC ---


            // --- Existing Gamemode Change Logic ---
            GameType targetGameType = PendingGamemodeChangeManager.getPendingChange(serverPlayer);

            if (targetGameType != null) {
                serverPlayer.sendSystemMessage(Component.literal("DEBUG: [RespawnHandler] Found pending gamemode for " + serverPlayer.getName().getString() + ": " + targetGameType.getName()));
                GameType currentPlayerGamemode = serverPlayer.gameMode.getGameModeForPlayer();
                serverPlayer.sendSystemMessage(Component.literal("DEBUG: [RespawnHandler] Current gamemode of " + serverPlayer.getName().getString() + ": " + currentPlayerGamemode.getName()));

                if (currentPlayerGamemode != targetGameType) {
                    serverPlayer.sendSystemMessage(Component.literal("DEBUG: [RespawnHandler] Changing gamemode for " + serverPlayer.getName().getString() + " to " + targetGameType.getName()));
                    serverPlayer.setGameMode(targetGameType);
                    serverPlayer.sendSystemMessage(Component.literal("Your gamemode has been changed to " + targetGameType.getName() + "."));
                    PendingGamemodeChangeManager.removePendingChange(serverPlayer);
                    serverPlayer.sendSystemMessage(Component.literal("DEBUG: [RespawnHandler] Gamemode changed and removed pending change."));
                } else {
                    serverPlayer.sendSystemMessage(Component.literal("DEBUG: [RespawnHandler] Player " + serverPlayer.getName().getString() + " is already in target gamemode (" + targetGameType.getName() + "), not changing."));
                    PendingGamemodeChangeManager.removePendingChange(serverPlayer);
                    serverPlayer.sendSystemMessage(Component.literal("DEBUG: [RespawnHandler] Removed pending change (already in target gamemode)."));
                }
            } else {
                serverPlayer.sendSystemMessage(Component.literal("DEBUG: [RespawnHandler] No pending gamemode found for " + serverPlayer.getName().getString()));
            }
            // --- End Existing Gamemode Change Logic ---

        } else {
            System.out.println("DEBUG: [RespawnHandler] Event entity is not ServerPlayer (e.g., client-side or different entity type).");
        }
    }
}