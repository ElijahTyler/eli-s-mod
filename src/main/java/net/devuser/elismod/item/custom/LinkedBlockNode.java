package net.devuser.elismod.item.custom;

import net.minecraft.core.BlockPos;

public class LinkedBlockNode {
    BlockPos blockPos;
    LinkedBlockNode previousNode;

    public LinkedBlockNode(BlockPos bp, LinkedBlockNode prev) {
        this.blockPos = bp;
        this.previousNode = prev;
    }

    public int getDistanceFromOrigin() {
        if (this.previousNode == null) {
            return 0;
        } else {
            return 1 + this.previousNode.getDistanceFromOrigin();
        }
    }
}
