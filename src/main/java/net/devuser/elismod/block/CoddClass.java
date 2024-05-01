package net.devuser.elismod.block;

import net.devuser.elismod.block.custom.GOLCellBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashSet;
import java.util.Set;

import static java.lang.Math.abs;

public class CoddClass {
    private Set<BlockPos> coddCellBlocks;
    public CoddClass(){
        coddCellBlocks = new HashSet<>();
    }

    public void addCell(BlockPos b) {
        coddCellBlocks.add(b);
    }

    public void removeCell(BlockPos b) {
        coddCellBlocks.remove(b);
    }

    public Set<BlockPos> getCells() {
        return coddCellBlocks;
    }

    public Set<BlockPos> getAllNonCellNeighborsOfCurrentCellBlocks(Level level) {
        // gets all non-cell neighbors from all cell blocks
        Set<BlockPos> neighbors = new HashSet<>();

        for (BlockPos cell_bp : coddCellBlocks) {
            neighbors.addAll( getAllNonCellNeighbors(cell_bp, level) );
        }

        return neighbors;
    }

    public Set<BlockPos> getAllNonCellNeighbors(BlockPos pos, Level level) { // von Neumann neighborhood
        Set<BlockPos> neighboringCellBlocks = new HashSet<>();
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();

        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                if ((dx == 0 && dz == 0) || (abs(abs(dx) + abs(dz)) != 1)) { // does not check diagonal neighbors
                    continue;
                }

                mutablePos.set(pos.getX() + dx, pos.getY(), pos.getZ() + dz);

                Block inspectedBlock = level.getBlockState(mutablePos).getBlock();

                // add any non-cell neighbors
                if (inspectedBlock instanceof AirBlock) {
                    neighboringCellBlocks.add(mutablePos.immutable());
                }
            }
        }

        return neighboringCellBlocks;
    }

    public int getNumOfCellNeighbors(Level world, BlockPos pos) {
        Set<BlockPos> neighboringCellBlocks = new HashSet<>();
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();

        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                if ((dx == 0 && dz == 0) || (abs(abs(dx) + abs(dz)) != 1)) { // does not check diagonal neighbors
                    continue;
                }

                mutablePos.set(pos.getX() + dx, pos.getY(), pos.getZ() + dz);
                BlockState neighborState = world.getBlockState(mutablePos);
                Block neighborBlock = neighborState.getBlock();

                if (neighborBlock instanceof GOLCellBlock) {
                    neighboringCellBlocks.add(mutablePos.immutable());
                }
            }
        }

        return neighboringCellBlocks.size();
    }
}