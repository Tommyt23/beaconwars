// src/main/java/net/trashpanda/beaconwars.commands/BeaconWarsCommand.java
package net.trashpanda.beaconwars.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.trashpanda.beaconwars.util.GamemodeStateManager;
import net.trashpanda.beaconwars.block.ModBlocks;

// NEW IMPORTS FOR MOB EFFECTS:
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

public class BeaconWarsCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("beaconwars")
                        .requires(source -> source.hasPermission(2))
                        .then(Commands.literal("start")
                                .executes(context -> {
                                    GamemodeStateManager.setBeaconWarsGamemodeActive(true);
                                    context.getSource().sendSuccess(() -> Component.literal("Beacon Wars gamemode has been activated!"), true);

                                    // --- NEW LOGIC: Apply Invisibility and Invulnerability ---
                                    int durationTicks = 10 * 60 * 20; // 10 minutes * 60 seconds/minute * 20 ticks/second

                                    // Get the server instance
                                    var server = context.getSource().getServer();

                                    // Iterate through all connected players
                                    for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                                        // Give Base Beacon (existing logic)
                                        ItemStack beaconStack = new ItemStack(ModBlocks.BASE_BEACON.get().asItem());
                                        player.getInventory().add(beaconStack);
                                        player.sendSystemMessage(Component.literal("You received a Base Beacon!"));
                                        System.out.println("DEBUG: [BeaconWarsCommand] Gave Base Beacon to " + player.getName().getString());

                                        // Apply Invisibility effect
                                        // MobEffectInstance(MobEffect effect, int duration, int amplifier, boolean ambient, boolean showParticles, boolean showIcon)
                                        MobEffectInstance invisibilityEffect = new MobEffectInstance(
                                                MobEffects.INVISIBILITY,
                                                durationTicks,
                                                0, // Amplifier: 0 for level I
                                                false, // Ambient: false for non-ambient
                                                false, // Show Particles: false for true invisibility
                                                false   // Show Icon: true to show in HUD
                                        );
                                        player.addEffect(invisibilityEffect);
                                        player.sendSystemMessage(Component.literal("You are now invisible for 10 minutes!"));
                                        System.out.println("DEBUG: [BeaconWarsCommand] Applied Invisibility to " + player.getName().getString());

                                        // Apply Damage Resistance effect (for invulnerability)
                                        MobEffectInstance resistanceEffect = new MobEffectInstance(
                                                MobEffects.DAMAGE_RESISTANCE,
                                                durationTicks,
                                                4, // Amplifier: 4 for level V (makes player almost invulnerable)
                                                false, // Ambient
                                                false,  // Show Particles: true (can be set to false if desired, but less visible)
                                                false   // Show Icon
                                        );
                                        player.addEffect(resistanceEffect);
                                        player.sendSystemMessage(Component.literal("You are now invulnerable for 10 minutes!"));
                                        System.out.println("DEBUG: [BeaconWarsCommand] Applied Damage Resistance to " + player.getName().getString());

                                        MobEffectInstance slowFalling = new MobEffectInstance(
                                                MobEffects.SLOW_FALLING,
                                                600,
                                                3,
                                                false,
                                                false,
                                                false
                                        );
                                        player.addEffect(slowFalling);
                                    }
                                    // --- END NEW LOGIC ---

                                    return 1; // Success
                                }))
                        .then(Commands.literal("stop")
                                .executes(context -> {
                                    GamemodeStateManager.setBeaconWarsGamemodeActive(false);
                                    context.getSource().sendSuccess(() -> Component.literal("Beacon Wars gamemode has been deactivated!"), true);

                                    // Optional: Remove effects when stopping gamemode
                                    var server = context.getSource().getServer();
                                    for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                                        if (player.hasEffect(MobEffects.INVISIBILITY)) {
                                            player.removeEffect(MobEffects.INVISIBILITY);
                                            player.sendSystemMessage(Component.literal("Invisibility removed."));
                                        }
                                        if (player.hasEffect(MobEffects.DAMAGE_RESISTANCE)) {
                                            player.removeEffect(MobEffects.DAMAGE_RESISTANCE);
                                            player.sendSystemMessage(Component.literal("Invulnerability removed."));
                                        }
                                    }

                                    return 1; // Success
                                }))
        );
    }
}