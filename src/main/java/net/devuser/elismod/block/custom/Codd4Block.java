package net.devuser.elismod.block.custom;

import net.devuser.elismod.ElisMod;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class Codd4Block extends Block {
    BlockPos ownPos;
    public Codd4Block(Properties pProperties) {
        super(pProperties);
    }

    public void setPlacedBy(Level world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        if (!world.isClientSide) {
            ElisMod.codd.addCell(pos);
            ownPos = pos;
        }
    }

    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        if (!pLevel.isClientSide) {
            ElisMod.codd.removeCell(pPos);
        }
    }
}