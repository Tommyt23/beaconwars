// src/main/java/net/trashpanda/beaconwars/data/BeaconWarsWorldData.java
package net.trashpanda.beaconwars.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken; // Import for TypeToken
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelResource; // For getting world path
import net.trashpanda.beaconwars.BeaconWars; // Your main mod class for LOGGER

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type; // Import for Type
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap; // Good for thread-safe map

public class BeaconWarsWorldData {

    private static final String FILE_NAME = "player_beacons.json"; // Name of your data file
    // Use GsonBuilder for pretty printing, makes the JSON file readable
    private final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private final File dataFile; // The actual file object

    // Map to store UUID to BeaconLocationData. Use ConcurrentHashMap for thread safety.
    private Map<UUID, BeaconLocationData> playerBeacons = new ConcurrentHashMap<>();

    public BeaconWarsWorldData(MinecraftServer server) {
        // Get the base directory for the current world save
        // This is usually like .minecraft/saves/YourWorldName/
        File worldDir = server.getWorldPath(LevelResource.ROOT).toFile();

        // Create a sub-directory specifically for your mod's data within the world save
        // e.g., YourWorldName/data/beaconwars/
        File modDataDir = new File(worldDir, "data" + File.separator + BeaconWars.MOD_ID);
        if (!modDataDir.exists()) {
            modDataDir.mkdirs(); // Create all necessary parent directories
        }

        this.dataFile = new File(modDataDir, FILE_NAME); // The full path to your JSON file
        BeaconWars.LOGGER.info("DEBUG: [BeaconWarsWorldData] Data file path: " + dataFile.getAbsolutePath());

        loadData(); // Attempt to load data when this manager is initialized
    }

    // --- Public API for interacting with the data ---

    public void setPlayerBeacon(UUID playerUUID, BeaconLocationData locationData) {
        playerBeacons.put(playerUUID, locationData);
        BeaconWars.LOGGER.info("DEBUG: [BeaconWarsWorldData] Set beacon for " + playerUUID + ": " + locationData);
        saveData(); // Save immediately after a change
    }

    public BeaconLocationData getPlayerBeacon(UUID playerUUID) {
        return playerBeacons.get(playerUUID);
    }

    public void removePlayerBeacon(UUID playerUUID) {
        playerBeacons.remove(playerUUID);
        BeaconWars.LOGGER.info("DEBUG: [BeaconWarsWorldData] Removed beacon for " + playerUUID);
        saveData(); // Save immediately after a change
    }

    public boolean hasPlayerBeacon(UUID playerUUID) {
        return playerBeacons.containsKey(playerUUID);
    }

    // --- Internal Save/Load Logic ---

    private void loadData() {
        if (dataFile.exists()) {
            try (FileReader reader = new FileReader(dataFile)) {
                // Use TypeToken to correctly deserialize generic types like Map
                Type type = new TypeToken<ConcurrentHashMap<UUID, BeaconLocationData>>() {}.getType();
                Map<UUID, BeaconLocationData> loadedBeacons = GSON.fromJson(reader, type);

                if (loadedBeacons != null) {
                    playerBeacons.clear(); // Clear existing map to ensure fresh load
                    playerBeacons.putAll(loadedBeacons);
                } else {
                    playerBeacons = new ConcurrentHashMap<>(); // Ensure it's never null
                }
                BeaconWars.LOGGER.info("DEBUG: [BeaconWarsWorldData] Loaded " + playerBeacons.size() + " beacon entries from file.");
            } catch (IOException e) {
                BeaconWars.LOGGER.error("ERROR: [BeaconWarsWorldData] Failed to load data from " + dataFile.getName(), e);
                playerBeacons = new ConcurrentHashMap<>(); // Fallback to empty map on error
            }
        } else {
            BeaconWars.LOGGER.info("DEBUG: [BeaconWarsWorldData] Data file not found, starting with empty beacon data.");
            playerBeacons = new ConcurrentHashMap<>();
        }
    }

    public void saveData() {
        try (FileWriter writer = new FileWriter(dataFile)) {
            GSON.toJson(playerBeacons, writer); // Serialize the map to JSON
            BeaconWars.LOGGER.info("DEBUG: [BeaconWarsWorldData] Saved " + playerBeacons.size() + " beacon entries to file.");
        } catch (IOException e) {
            BeaconWars.LOGGER.error("ERROR: [BeaconWarsWorldData] Failed to save data to " + dataFile.getName(), e);
        }
    }
}