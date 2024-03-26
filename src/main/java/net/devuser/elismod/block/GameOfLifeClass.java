package net.devuser.elismod.block;

import net.devuser.elismod.block.custom.CellBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashSet;
import java.util.Set;

public class GameOfLifeClass {
    private Set<BlockPos> cellBlocks;
    public GameOfLifeClass() {
        cellBlocks = new HashSet<>();
    }

    public void addCell(BlockPos b) {
        cellBlocks.add(b);
    }

    public void removeCell(BlockPos b) {
        cellBlocks.remove(b);
    }

    public int getNumOfCells() {
        return cellBlocks.size();
    }

    public void setAllCellBlocks(Set<BlockPos> new_cb) {
        cellBlocks = new_cb;
    }

    public Set<BlockPos> getCells() {
        return cellBlocks;
    }

    public Set<BlockPos> getAllNonCellNeighborsOfCurrentCellBlocks(Level level) {
        // gets all non-cell neighbors from all cell blocks
        Set<BlockPos> neighbors = new HashSet<>();

        for (BlockPos cell_bp : cellBlocks) {
            neighbors.addAll( getAllNonCellNeighbors(cell_bp, level) );
        }

        return neighbors;
    }

    public Set<BlockPos> getAllNonCellNeighbors(BlockPos pos, Level level) {
        Set<BlockPos> neighboringCellBlocks = new HashSet<>();
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();

        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                if (dx == 0 && dz == 0) {
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
                if (dx == 0 && dz == 0) {
                    continue;
                }

                mutablePos.set(pos.getX() + dx, pos.getY(), pos.getZ() + dz);
                BlockState neighborState = world.getBlockState(mutablePos);
                Block neighborBlock = neighborState.getBlock();

                if (neighborBlock instanceof CellBlock) {
                    neighboringCellBlocks.add(mutablePos.immutable());
                }
            }
        }

        return neighboringCellBlocks.size();
    }
}