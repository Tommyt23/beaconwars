package net.trashpanda.beaconwars.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.trashpanda.beaconwars.util.PendingGamemodeChangeManager;
import org.jetbrains.annotations.Nullable;

// NEW IMPORT: ResourceKey
import net.minecraft.resources.ResourceKey;

import java.util.Optional;

public class BaseBeaconBlock extends Block {

    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty OCCUPIED = BlockStateProperties.OCCUPIED;

    public BaseBeaconBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState() // <-- CORRECT
                .setValue(FACING, Direction.NORTH)
                .setValue(OCCUPIED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, OCCUPIED);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide) {
            MenuProvider menuProvider = new SimpleMenuProvider(
                    (id, playerInventory, playerEntity) -> new CraftingMenu(id, playerInventory, ContainerLevelAccess.create(level, pos)),
                    Component.translatable("container.crafting")
            );
            player.openMenu(menuProvider);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (!level.isClientSide && placer instanceof Player player) {
            if (player instanceof ServerPlayer serverPlayer) {
                // FIX: Add level.dimension() as the first argument
                serverPlayer.setRespawnPosition(level.dimension(), pos, 0.0F, false, true); // Corrected signature
                serverPlayer.displayClientMessage(Component.translatable("block.minecraft.set_spawn"), false);

                level.setBlock(pos, state.setValue(OCCUPIED, true), 3);
            }
        }
    }

    //@Override
    public boolean isBed(BlockState state, LevelReader level, BlockPos pos, @Nullable Entity entity) {
        return true;
    }

    //@Override
    public boolean sleepsIn(BlockState state, LevelReader level, BlockPos pos, @Nullable Entity entity) {
        return false;
    }

    @Nullable
    @Override
    public Optional<Vec3> getRespawnPosition(BlockState state, EntityType<?> type, LevelReader level, BlockPos pos, float orientation, @Nullable LivingEntity entity) {
        // Return your desired position wrapped in Optional.of()
        return Optional.of(new Vec3(pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D));
    }

    @Override
    public void setBedOccupied(BlockState state, Level level, BlockPos pos, LivingEntity sleeper, boolean occupied) {
        if (!level.isClientSide) {
            level.setBlock(pos, state.setValue(OCCUPIED, occupied), 3);
        }
    }

    //@Override
    public void onRespawn(BlockState state, ServerLevel level, BlockPos pos, LivingEntity sleeper) {
        // Optional: Add custom logic here
    }

    @Override
    public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        // Only send messages if it's a ServerPlayer
        if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.sendSystemMessage(Component.literal("DEBUG: [BeaconBlock] playerWillDestroy called for block at " + pos.toString() + " by player " + serverPlayer.getName().getString()));

            // FIX: Removed serverPlayer.isSleepingLongEnough() as it's not relevant for custom spawn setting
            if (!level.isClientSide) {
                serverPlayer.sendSystemMessage(Component.literal("DEBUG: [BeaconBlock] Server-side code executing."));

                // Get the player's current respawn dimension and position
                ResourceKey<Level> respawnDim = serverPlayer.getRespawnDimension();
                BlockPos respawnPos = serverPlayer.getRespawnPosition();

                // Now, check if both are present AND if they match the current block's dimension and position
                if (respawnDim != null && respawnDim == level.dimension() &&
                        respawnPos != null && respawnPos.equals(pos)) {

                    serverPlayer.sendSystemMessage(Component.literal("DEBUG: [BeaconBlock] Block being destroyed IS player's current respawn point."));
                    serverPlayer.setRespawnPosition(null, null, 0.0F, false, false);
                    serverPlayer.displayClientMessage(Component.translatable("block.minecraft.clear_spawn"), false);

                    PendingGamemodeChangeManager.addPendingChange(serverPlayer, GameType.SPECTATOR); // Change to desired gamemode
                    serverPlayer.sendSystemMessage(Component.literal("DEBUG: [BeaconBlock] Called addPendingChange."));
                } else {
                    serverPlayer.sendSystemMessage(Component.literal("DEBUG: [BeaconBlock] Block being destroyed is NOT player's current respawn point or is missing info."));
                    serverPlayer.sendSystemMessage(Component.literal("DEBUG: [BeaconBlock] RespawnDim: " + (respawnDim != null ? respawnDim.location() : "N/A") + ", CurrentDim: " + level.dimension().location()));
                    serverPlayer.sendSystemMessage(Component.literal("DEBUG: [BeaconBlock] RespawnPos: " + (respawnPos != null ? respawnPos.toString() : "N/A") + ", CurrentPos: " + pos.toString()));
                }
            } else {
                serverPlayer.sendSystemMessage(Component.literal("DEBUG: [BeaconBlock] Client-side code (not processing spawn logic)."));
            }
        }
        super.playerWillDestroy(level, pos, state, player);
    }
}
