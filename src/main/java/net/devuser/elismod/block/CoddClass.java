package net.devuser.elismod.block;

import net.minecraft.core.BlockPos;

import java.util.HashSet;
import java.util.Set;

public class CoddClass {
    private Set<BlockPos> langtonCellBlocks;
    public CoddClass(){
        langtonCellBlocks = new HashSet<>();
    }
}