package net.trashpanda.beaconwars.events;

import net.minecraft.world.level.GameType;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.trashpanda.beaconwars.BeaconWars; // Your main mod class
import net.trashpanda.beaconwars.util.PendingGamemodeChangeManager;

@Mod.EventBusSubscriber(modid = BeaconWars.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class GamemodeChangeHandler {

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            GameType targetGameType = PendingGamemodeChangeManager.getPendingChange(serverPlayer);

            if (targetGameType != null && serverPlayer.gameMode.getGameModeForPlayer() != targetGameType) {
                serverPlayer.setGameMode(targetGameType);
                serverPlayer.sendSystemMessage(net.minecraft.network.chat.Component.literal("Your gamemode has been changed to " + targetGameType.getName() + "."));
                PendingGamemodeChangeManager.removePendingChange(serverPlayer); // Remove the pending change
            }
        }
    }
}