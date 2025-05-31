// src/main/java/net/trashpanda/beaconwars/menu/AlwaysValidCraftingMenu.java
package net.trashpanda.beaconwars.menu;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.CraftingMenu;

public class AlwaysValidCraftingMenu extends CraftingMenu {
    public AlwaysValidCraftingMenu(int id, Inventory inventory, ContainerLevelAccess access) {
        super(id, inventory, access);
    }

    @Override
    public boolean stillValid(Player player) {
        return true; // Prevents the menu from closing
    }
}
