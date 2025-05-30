// src/main/java/net/trashpanda/beaconwars/util/PendingGamemodeChangeManager.java
package net.trashpanda.beaconwars.util;

import net.minecraft.network.chat.Component; // Import Component
import net.minecraft.world.level.GameType;
import net.minecraft.world.entity.player.Player;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.Nullable;

public class PendingGamemodeChangeManager {
    private static final Map<UUID, GameType> pendingChanges = new ConcurrentHashMap<>();

    public static void addPendingChange(Player player, GameType targetGameType) {
        pendingChanges.put(player.getUUID(), targetGameType);
        // --- CHANGED DEBUGGING TO IN-GAME MESSAGE ---
        player.sendSystemMessage(Component.literal("DEBUG: [Manager] Added pending gamemode change for player " + player.getName().getString() + " (UUID: " + player.getUUID() + ") to " + targetGameType.getName()));
        // --- END DEBUGGING ---
        player.sendSystemMessage(Component.literal("Your gamemode will change to " + targetGameType.getName() + " on next respawn."));
    }

    @Nullable
    public static GameType getPendingChange(Player player) {
        GameType type = pendingChanges.get(player.getUUID());
        // --- CHANGED DEBUGGING TO IN-GAME MESSAGE ---
        player.sendSystemMessage(Component.literal("DEBUG: [Manager] Checking pending gamemode for player " + player.getName().getString() + " (UUID: " + player.getUUID() + "). Found: " + (type != null ? type.getName() : "none")));
        // --- END DEBUGGING ---
        return type;
    }

    public static void removePendingChange(Player player) {
        pendingChanges.remove(player.getUUID());
        // --- CHANGED DEBUGGING TO IN-GAME MESSAGE ---
        player.sendSystemMessage(Component.literal("DEBUG: [Manager] Removed pending gamemode change for player " + player.getName().getString() + " (UUID: " + player.getUUID() + ")"));
        // --- END DEBUGGING ---
    }
}