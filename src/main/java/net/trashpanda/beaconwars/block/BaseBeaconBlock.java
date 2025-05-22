package net.trashpanda.beaconwars.block;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;

public class BaseBeaconBlock extends Block {
    public BaseBeaconBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit){
        if (!pLevel.isClientSide) {
            MenuProvider menuProvider = new SimpleMenuProvider(
                    (id, playerInventory, playerEntity) -> new CraftingMenu(id, playerInventory, ContainerLevelAccess.NULL),
                    Component.translatable("container.crafting"));

            pPlayer.openMenu(menuProvider);
        }
        return InteractionResult.SUCCESS;
    }
}
