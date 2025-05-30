package net.trashpanda.beaconwars.events;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.trashpanda.beaconwars.BeaconWars;

@Mod.EventBusSubscriber(modid = BeaconWars.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CraftingRestrictionHandler {

    @SubscribeEvent
    public static void onPlayerCrafted(PlayerEvent.ItemCraftedEvent event) { // Changed back to ItemCraftedEvent
        System.out.println("DEBUG: [CraftingRestrictionHandler] PlayerEvent.ItemCraftedEvent fired for " + event.getEntity().getName().getString());
        System.out.println("DEBUG: [CraftingRestrictionHandler] Crafted item: " + event.getCrafting().getDisplayName().getString());

        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            System.out.println("DEBUG: [CraftingRestrictionHandler] Event is on server side for a ServerPlayer.");

            if (event.getCrafting().getItem() == Items.CRAFTING_TABLE) {
                System.out.println("DEBUG: [CraftingRestrictionHandler] Crafted item IS a CRAFTING_TABLE. Attempting to remove from inventory.");

                // Instead of canceling, remove the crafted item from the player's inventory
                // This will remove ONE crafting table that was just crafted.
                // It's reactive, not preventative, but it *will* remove it.
                event.getCrafting().setCount(0); // Set the count of the crafted stack to 0

                // You might also need to explicitly remove it from their actual inventory
                // For a single item crafted, this is often sufficient, but for multiple, it might need more work.
                serverPlayer.getInventory().removeItem(event.getCrafting()); // Attempts to remove the itemStack

                int planksUsed = 4;

                event.getCrafting().setCount(0);

                ItemStack planksToReturn = new ItemStack(Items.OAK_PLANKS, planksUsed); // Or whatever wood type was used
                serverPlayer.getInventory().add(planksToReturn);

                serverPlayer.sendSystemMessage(Component.literal("You are not allowed to craft crafting tables! (Item removed)"));
                System.out.println("DEBUG: [CraftingRestrictionHandler] Crafted item removed and player notified.");

                // Important: Update the client's inventory to reflect the change
                serverPlayer.containerMenu.broadcastChanges();
            } else {
                System.out.println("DEBUG: [CraftingRestrictionHandler] Crafted item is NOT a crafting table.");
            }
        } else {
            System.out.println("DEBUG: [CraftingRestrictionHandler] Event is for client-side or non-ServerPlayer. Skipping restriction.");
        }
    }
}