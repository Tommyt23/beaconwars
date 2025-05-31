// src/main/java/net/trashpanda/beaconwars/block/BaseBeaconBlock.java
package net.trashpanda.beaconwars.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks; // For accessing the vanilla crafting table block
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.CraftingMenu;

import net.minecraft.world.level.GameType; // If you still need this
import net.minecraft.resources.ResourceKey; // If you still need this
import net.trashpanda.beaconwars.util.PendingGamemodeChangeManager; // If you still need this
import net.trashpanda.beaconwars.util.GamemodeStateManager; // If you still need this

// (Other imports as needed)

public class BaseBeaconBlock extends Block {
    public BaseBeaconBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (player instanceof ServerPlayer serverPlayer) {
            System.out.println("DEBUG: [BeaconBlock] playerWillDestroy called for block at " + pos.toString() + " by player " + serverPlayer.getName().getString());

            if (GamemodeStateManager.isBeaconWarsGamemodeActive()) {
                serverPlayer.sendSystemMessage(Component.literal("DEBUG: [BeaconBlock] Beacon Wars Gamemode is ACTIVE. Checking spawn point."));

                if (!level.isClientSide) {
                    serverPlayer.sendSystemMessage(Component.literal("DEBUG: [BeaconBlock] Server-side code executing."));

                    ResourceKey<Level> respawnDim = serverPlayer.getRespawnDimension();
                    BlockPos respawnPos = serverPlayer.getRespawnPosition();

                    if (respawnDim != null && respawnDim == level.dimension() &&
                            respawnPos != null && respawnPos.equals(pos)) {

                        serverPlayer.sendSystemMessage(Component.literal("DEBUG: [BeaconBlock] Block being destroyed IS player's current respawn point."));
                        serverPlayer.setRespawnPosition(null, null, 0.0F, false, false);
                        serverPlayer.displayClientMessage(Component.translatable("block.minecraft.clear_spawn"), false);

                        PendingGamemodeChangeManager.addPendingChange(serverPlayer, GameType.SURVIVAL);
                        serverPlayer.sendSystemMessage(Component.literal("DEBUG: [BeaconBlock] Called addPendingChange."));
                    } else {
                        serverPlayer.sendSystemMessage(Component.literal("DEBUG: [BeaconBlock] Block being destroyed is NOT player's current respawn point or is missing info."));
                        serverPlayer.sendSystemMessage(Component.literal("DEBUG: [BeaconBlock] RespawnDim: " + (respawnDim != null ? respawnDim.location() : "N/A") + ", CurrentDim: " + level.dimension().location()));
                        serverPlayer.sendSystemMessage(Component.literal("DEBUG: [BeaconBlock] RespawnPos: " + (respawnPos != null ? respawnPos.toString() : "N/A") + ", CurrentPos: " + pos.toString()));
                    }
                } else {
                    serverPlayer.sendSystemMessage(Component.literal("DEBUG: [BeaconBlock] Client-side code (not processing spawn logic)."));
                }
            } else {
                serverPlayer.sendSystemMessage(Component.literal("DEBUG: [BeaconBlock] Beacon Wars Gamemode is INACTIVE. Skipping special logic."));
            }
        }
        super.playerWillDestroy(level, pos, state, player);
    }

    // --- NEW METHOD TO HANDLE RIGHT-CLICK INTERACTION ---
    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, net.minecraft.world.phys.BlockHitResult pHit) {
        if (!pLevel.isClientSide) { // Server-side logic only
            if (pPlayer instanceof ServerPlayer serverPlayer) {
                MenuProvider containerProvider = new SimpleMenuProvider((id, inventory, player) -> {
                    // This creates a standard 3x3 crafting grid menu
                    // ContainerLevelAccess.create(pLevel, pPos) links it to the block's position
                    return new CraftingMenu(id, inventory, ContainerLevelAccess.create(pLevel, pPos));
                }, Component.translatable("container.crafting")); // Title for the crafting menu

                serverPlayer.openMenu(containerProvider);
                serverPlayer.sendSystemMessage(Component.literal("DEBUG: [BaseBeaconBlock] Opened crafting menu."));
            }
        }
        return InteractionResult.SUCCESS; // Indicate that the interaction was handled
    }
}
