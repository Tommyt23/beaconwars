package net.trashpanda.beaconwars.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor; // Import MapColor for visual distinction
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.trashpanda.beaconwars.BeaconWars; // Your main mod class

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, BeaconWars.MOD_ID);

    // Register your BaseBeaconBlock using the new class
    public static final RegistryObject<Block> BASE_BEACON = BLOCKS.register("base_beacon",
            () -> new BaseBeaconBlock(BlockBehaviour.Properties.copy(Blocks.STONE)
                    .strength(6.0f) // Set its hardness/strength
                    .requiresCorrectToolForDrops() // Make it require the correct tool to drop items
                    .noLootTable() // Keep this if you want to handle drops via data generation

            ));

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
