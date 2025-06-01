// src/main/java/net/trashpanda/beaconwars/data/BeaconLocationData.java
package net.trashpanda.beaconwars.data;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

public class BeaconLocationData {
    private int x;
    private int y;
    private int z;
    private String dimension; // Store dimension as a string (ResourceLocation.toString())

    // Gson requires a no-argument constructor for deserialization
    public BeaconLocationData() {}

    public BeaconLocationData(BlockPos pos, ResourceKey<Level> dim) {
        this.x = pos.getX();
        this.y = pos.getY();
        this.z = pos.getZ();
        this.dimension = dim.location().toString();
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getZ() { return z; }
    public String getDimension() { return dimension; }

    // Helper method to convert back to Minecraft's BlockPos
    public BlockPos toBlockPos() { return new BlockPos(x, y, z); }

    // Helper method to convert back to Minecraft's ResourceKey<Level>
    public ResourceKey<Level> toDimensionResourceKey() {
        return ResourceKey.create(net.minecraft.core.registries.Registries.DIMENSION, new ResourceLocation(dimension));
    }

    @Override
    public String toString() {
        return "BeaconLocationData{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                ", dimension='" + dimension + '\'' +
                '}';
    }
}