package net.devuser.elismod.block;

import net.minecraft.core.BlockPos;

import java.util.HashSet;
import java.util.Set;

public class LangtonLoopClass {
    private Set<BlockPos> langtonCellBlocks;
    public LangtonLoopClass(){
        langtonCellBlocks = new HashSet<>();
    }
}