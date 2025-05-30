package net.trashpanda.beaconwars.events;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.trashpanda.beaconwars.BeaconWars;
import net.trashpanda.beaconwars.util.GamemodeStateManager; // Import the state manager

@Mod.EventBusSubscriber(modid = BeaconWars.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CraftingRestrictionHandler {

    @SubscribeEvent
    public static void onPlayerCrafted(PlayerEvent.ItemCraftedEvent event) {
        System.out.println("DEBUG: [CraftingRestrictionHandler] PlayerEvent.ItemCraftedEvent fired for " + event.getEntity().getName().getString());
        System.out.println("DEBUG: [CraftingRestrictionHandler] Crafted item: " + event.getCrafting().getDisplayName().getString());

        // Only apply crafting restriction if Beacon Wars gamemode is active
        if (GamemodeStateManager.isBeaconWarsGamemodeActive()) {
            System.out.println("DEBUG: [CraftingRestrictionHandler] Beacon Wars Gamemode is ACTIVE. Checking crafted item.");

            if (event.getEntity() instanceof ServerPlayer serverPlayer) {
                System.out.println("DEBUG: [CraftingRestrictionHandler] Event is on server side for a ServerPlayer.");

                if (event.getCrafting().getItem() == Items.CRAFTING_TABLE) {
                    System.out.println("DEBUG: [CraftingRestrictionHandler] Crafted item IS a CRAFTING_TABLE. Attempting to remove and return planks.");

                    int planksUsed = 4; // Assuming 4 planks for a crafting table

                    event.getCrafting().setCount(0); // Remove the crafted crafting table
                    ItemStack planksToReturn = new ItemStack(Items.OAK_PLANKS, planksUsed);
                    serverPlayer.getInventory().add(planksToReturn);
                    System.out.println("DEBUG: [CraftingRestrictionHandler] Returned " + planksUsed + " oak planks to player.");

                    serverPlayer.sendSystemMessage(Component.literal("You are not allowed to craft crafting tables! Planks returned."));
                    System.out.println("DEBUG: [CraftingRestrictionHandler] Crafted item removed and player notified.");

                    if (serverPlayer.containerMenu != null) {
                        serverPlayer.containerMenu.broadcastChanges();
                    }
                } else {
                    System.out.println("DEBUG: [CraftingRestrictionHandler] Crafted item is NOT a crafting table.");
                }
            } else {
                System.out.println("DEBUG: [CraftingRestrictionHandler] Event is for client-side or non-ServerPlayer. Skipping restriction.");
            }
        } else {
            System.out.println("DEBUG: [CraftingRestrictionHandler] Beacon Wars Gamemode is INACTIVE. Skipping crafting restriction.");
        }
    }
}