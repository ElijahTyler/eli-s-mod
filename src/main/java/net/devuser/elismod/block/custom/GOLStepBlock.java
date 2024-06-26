package net.devuser.elismod.block.custom;

import net.devuser.elismod.ElisMod;
import net.devuser.elismod.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import java.util.HashSet;
import java.util.Set;

public class GOLStepBlock extends Block {
    private boolean stepIsPowered;

    public GOLStepBlock(Properties pProperties) {
        super(pProperties);

        stepIsPowered = false;
    }

    @Override
    public InteractionResult use(BlockState pState, Level level, BlockPos pPos,
                                 Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (!level.isClientSide()) {
            runStep(level);
        }

        return InteractionResult.SUCCESS;
    }

    public void runStep(Level level) {
        // get all cell blocks
        Set<BlockPos> currentCellBlocks = ElisMod.gol.getCells();

        // get all cell blocks' neighbors (not including cell blocks) *****
        Set<BlockPos> currentCellBlocksNeighbors = ElisMod.gol.getAllNonCellNeighborsOfCurrentCellBlocks(level);
        currentCellBlocksNeighbors.removeAll(currentCellBlocks);


        // initialize new set of cell blocks for next generation
        Set<BlockPos> nextGenerationCellBlocks = new HashSet<>();


        // what do we put in nextGenerationCellBlocks? we need to use the life/death rules of Conway's Game of Life


        // a. cell blocks stay alive if # of neighbors is in survival rules
        for (BlockPos cell_bp : currentCellBlocks) {
            int cbNumOfCellNeighbors = ElisMod.gol.getNumOfCellNeighbors(level, cell_bp);
            boolean staysAlive = false;

            for (int x : ElisMod.gol.survival_rules) {
                if (x == cbNumOfCellNeighbors) {
                    staysAlive = true;
                    break;
                }
            }
            if (staysAlive) {
                nextGenerationCellBlocks.add(cell_bp);
            }
        }

        // b. non-cell blocks become alive if # of neighbors is in birth rules
        for (BlockPos non_cell_bp : currentCellBlocksNeighbors) {
            int cbNumOfCellNeighbors = ElisMod.gol.getNumOfCellNeighbors(level, non_cell_bp);
            boolean isBorn = false;

            for (int x : ElisMod.gol.birth_rules) {
                if (x == cbNumOfCellNeighbors) {
                    isBorn = true;
                    break;
                }
            }
            if (isBorn) {
                nextGenerationCellBlocks.add(non_cell_bp);
            }
        }


        // what we have:
        // from our current cell blocks, and our current non-cell neighbors,
        // we have created our next generation of cell blocks

        // we need to do two things, here's the efficient approach:
        // we need to create two sets, currentlyCellBecomingAir and currentlyAirBecomingCell
        // where currentlyCellBecomingAir = cell blocks in currentCellBlocks and not in nextGenerationCellBlocks
        // and currentlyAirBecomingCell = cell blocks not in currentCellBlocks and in nextGenerationCellBlocks


        Set<BlockPos> currentlyCellBecomingAir = new HashSet<>();
        for (BlockPos bp : currentCellBlocks) {
            if (!nextGenerationCellBlocks.contains(bp)) {
                currentlyCellBecomingAir.add(bp);
            }
        }

        Set<BlockPos> currentlyAirBecomingCell = new HashSet<>();
        for (BlockPos bp : nextGenerationCellBlocks) {
            if (!currentCellBlocks.contains(bp)) {
                currentlyAirBecomingCell.add(bp);
            }
        }

        // now we only need to update our two new sets

        for (BlockPos bp : currentlyCellBecomingAir) {
            ElisMod.gol.removeCell(bp); // might be redundant, see CellBlock.onRemove
            level.removeBlock(bp, false);
        }

        for (BlockPos bp : currentlyAirBecomingCell) {
            ElisMod.gol.addCell(bp);
            level.setBlock(bp, ModBlocks.CELL_BLOCK.get().defaultBlockState(), 3);
        }


    }

    @Override
    public void neighborChanged(BlockState pState, Level pLevel, BlockPos pPos,
                                Block pNeighborBlock, BlockPos pNeighborPos, boolean pMovedByPiston) {
        super.neighborChanged(pState, pLevel, pPos, pNeighborBlock, pNeighborPos, pMovedByPiston);

        if (!pLevel.isClientSide()) {
            boolean receivingPower = pLevel.hasNeighborSignal(pPos);

            if (receivingPower && !stepIsPowered) {
                stepIsPowered = true;
                runStep(pLevel);
            } else if (!receivingPower && stepIsPowered) {
                stepIsPowered = false;
            }
        }
    }
}