package net.trashpanda.beaconwars.modTab;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.trashpanda.beaconwars.BeaconWars;
import net.trashpanda.beaconwars.block.ModBlocks;
import net.trashpanda.beaconwars.ModItems.ModItems; // IMPORT YOUR NEW MODITEMS CLASS!

public class ModCreativeModTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, BeaconWars.MOD_ID);

    public static final RegistryObject<CreativeModeTab> BEACON_WARS_TAB = CREATIVE_MODE_TABS.register("beacon_wars_tab",
            () -> CreativeModeTab.builder()
                    // Use the BlockItem for the icon as well
                    .icon(() -> new ItemStack(ModItems.BASE_BEACON_ITEM.get()))
                    .title(Component.translatable("creativetab.beacon_wars_tab"))
                    .displayItems(((pParameters, pOutput) -> {
                        // NOW you pass the registered BlockItem, which is a proper Item
                        pOutput.accept(ModItems.BASE_BEACON_ITEM.get());
                    }))
                    .build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
