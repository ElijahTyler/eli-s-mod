package net.devuser.elismod.block.custom;

import net.devuser.elismod.ElisMod;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashSet;
import java.util.Set;

public class CellBlock extends Block {
    BlockPos ownPos;
    public CellBlock(Properties pProperties) {
        super(pProperties);
    }

    public void setPlacedBy(Level world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        if (!world.isClientSide) {
            ElisMod.gol.addCell(pos);
            ownPos = pos;
        }
    }

    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        if (!pLevel.isClientSide) {
            ElisMod.gol.removeCell(pPos);
        }
    }
}