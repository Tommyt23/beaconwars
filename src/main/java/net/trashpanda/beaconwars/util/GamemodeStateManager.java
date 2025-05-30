package net.trashpanda.beaconwars.util;

// You might consider persistent storage later, but for now, static boolean is fine.
public class GamemodeStateManager {
    private static boolean beaconWarsGamemodeActive = false;

    public static boolean isBeaconWarsGamemodeActive() {
        return beaconWarsGamemodeActive;
    }

    public static void setBeaconWarsGamemodeActive(boolean active) {
        beaconWarsGamemodeActive = active;
        // You might want to log this state change
        System.out.println("DEBUG: [GamemodeStateManager] BeaconWars Gamemode set to: " + active);
    }
}