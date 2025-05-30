// src/main/java/net/trashpanda/beaconwars/commands/BeaconWarsCommand.java
package net.trashpanda.beaconwars.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer; // Import ServerPlayer
import net.minecraft.world.item.ItemStack; // Import ItemStack
import net.minecraft.world.level.GameType; // If you still need this for other logic, keep it.
import net.trashpanda.beaconwars.util.GamemodeStateManager;
import net.trashpanda.beaconwars.block.ModBlocks; // Import your ModBlocks class

public class BeaconWarsCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("beaconwars")
                        .requires(source -> source.hasPermission(2)) // Requires Op level 2 (modify world)
                        .then(Commands.literal("start")
                                .executes(context -> {
                                    GamemodeStateManager.setBeaconWarsGamemodeActive(true);
                                    context.getSource().sendSuccess(() -> Component.literal("Beacon Wars gamemode has been activated!"), true);

                                    // --- NEW LOGIC TO GIVE BEACONS ---
                                    // Get the server instance from the command source
                                    var server = context.getSource().getServer();
                                    // Iterate through all connected players
                                    for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                                        ItemStack beaconStack = new ItemStack(ModBlocks.BASE_BEACON.get().asItem()); // Create an ItemStack of your base beacon
                                        player.getInventory().add(beaconStack); // Add to player's inventory
                                        player.sendSystemMessage(Component.literal("You received a Base Beacon!"));
                                        System.out.println("DEBUG: [BeaconWarsCommand] Gave Base Beacon to " + player.getName().getString());
                                    }
                                    // --- END NEW LOGIC ---

                                    return 1; // Success
                                }))
                        .then(Commands.literal("stop")
                                .executes(context -> {
                                    GamemodeStateManager.setBeaconWarsGamemodeActive(false);
                                    context.getSource().sendSuccess(() -> Component.literal("Beacon Wars gamemode has been deactivated!"), true);
                                    return 1; // Success
                                }))
        );
    }
}