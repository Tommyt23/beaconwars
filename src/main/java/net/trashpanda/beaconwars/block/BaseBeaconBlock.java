// src/main/java/net/trashpanda/beaconwars/block/BaseBeaconBlock.java
package net.trashpanda.beaconwars.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.network.chat.Component;

import javax.annotation.Nullable;

public class BaseBeaconBlock extends Block {

    public BaseBeaconBlock(Properties props) {
        super(props);
    }

//    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable Player placer, @Nullable net.minecraft.world.item.ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);

        if (!level.isClientSide && placer instanceof ServerPlayer serverPlayer) {
            BlockPos respawnPos = pos.above(); // ensure player spawns on top

            serverPlayer.setRespawnPosition(
                    serverPlayer.level().dimension(),
                    respawnPos,
                    0.0f,
                    true,  // force
                    false  // don't notify (we can send custom msg)
            );

            serverPlayer.sendSystemMessage(Component.literal("Your respawn point has been set to the Beacon Block!"));
            System.out.println("DEBUG: Respawn point set at " + respawnPos + " for " + serverPlayer.getName().getString());
        }
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide) {
            player.sendSystemMessage(Component.literal("This block acts like a crafting table!"));
            // TODO: open your custom GUI or crafting screen here
        }
        return InteractionResult.SUCCESS;
    }

//    @Override
    public boolean isPossibleToRespawnInThis(BlockState state, Level world, BlockPos pos) {
        return true; // This tells Minecraft "Yes, you can respawn here"
    }

//    @Override
    public boolean isBed(BlockState state, Level world, BlockPos pos, @Nullable Entity entity) {
        return true;
    }
}
