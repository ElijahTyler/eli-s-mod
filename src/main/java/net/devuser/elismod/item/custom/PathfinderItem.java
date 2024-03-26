package net.devuser.elismod.item.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.*;

public class PathfinderItem extends Item {
    public PathfinderItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        BlockPos clickedPos = pContext.getClickedPos();
        Level level = pContext.getLevel();
        Player player = pContext.getPlayer();

        if (!level.isClientSide()) {
            if (!level.getBlockState(clickedPos).getBlock().equals(Blocks.RED_WOOL)) {
                // return, can't start maze
                player.sendSystemMessage(Component.literal(
                        "Click on red wool to start pathfinding to green wool"));
                return InteractionResult.SUCCESS;
            }

            // otherwise, let the maze begin!
            List<BlockPos> pathBlocks = findPathBlocks(clickedPos, level, player);

            if (pathBlocks == null) {
                // see if neighboring glass
                boolean neighbors = isNeighboringGlass(clickedPos, level);
                if (neighbors) {
                    List<BlockPos> glassBlocks = findGlass(clickedPos, level);
                    glassBlocks.remove(clickedPos);

                    // remove found glass blocks
                    for (BlockPos currentPos : glassBlocks) {
                        level.setBlock(currentPos, Blocks.AIR.defaultBlockState(), 3);
                    }
                }

                return InteractionResult.SUCCESS;
            }

            // fill pathfound blocks with glass
            for (BlockPos currentPos : pathBlocks) {
                level.setBlock(currentPos, Blocks.GLASS.defaultBlockState(), 3);
            }

            System.out.println(pathBlocks.size());
        }

        return InteractionResult.SUCCESS;
    }

    private boolean isNeighboringGlass(BlockPos currentPos, Level level) {
        BlockPos[] adjPos = new BlockPos[]{
                currentPos.north(), currentPos.south(),
                currentPos.east(), currentPos.west()
        };

        for (BlockPos inspectedPos : adjPos) {
            Block inspectedBlock = level.getBlockState(inspectedPos).getBlock();
            if (inspectedBlock.equals(Blocks.GLASS)) return true;
        }

        return false;
    }

    public List<BlockPos> findPathBlocks(BlockPos redWoolPos, Level level, Player player) {
        // should return all blocks BETWEEN red wool and green wool exclusive

        List<BlockPos> airMap = findGreenWool(redWoolPos, level);
        if (airMap == null) return null;
        BlockPos greenWoolPos = airMap.get(airMap.size() - 1);

        // find predecessors for each block to create minimal path
        HashMap<BlockPos, BlockPos> posPredecessors = findPredecessors(level, airMap, redWoolPos);

        List<BlockPos> finalPath = createFinalPath(posPredecessors, greenWoolPos, redWoolPos);

        return finalPath;
    }

    private List<BlockPos> createFinalPath(HashMap<BlockPos, BlockPos> posPredecessors, BlockPos greenWoolPos, BlockPos redWoolPos) {
        List<BlockPos> finalPath = new LinkedList<>();

        BlockPos currentPos = posPredecessors.get(greenWoolPos);

        while (currentPos != redWoolPos) {
            finalPath.add(currentPos);
            currentPos = posPredecessors.get(currentPos);
        }

        return finalPath;
    }

    private HashMap<BlockPos, BlockPos> findPredecessors(Level level, List<BlockPos> map, BlockPos redWoolPos) {
        HashMap<BlockPos, BlockPos> predecessorMap = new HashMap<>();
        predecessorMap.put(redWoolPos, redWoolPos);
        Queue<BlockPos> queue = new LinkedList<>();
        queue.add(redWoolPos);
        BlockPos currentPos;

        while (!queue.isEmpty()) {
            currentPos = queue.remove();
            List<BlockPos> neighbors = getNeighbors(currentPos, map);
            for (BlockPos inspectedPos : neighbors) {
                if (predecessorMap.get(inspectedPos) == null) {
                    predecessorMap.put(inspectedPos, currentPos);
                    queue.add(inspectedPos);
                }
            }
        }

        return predecessorMap;
    }

    private List<BlockPos> getNeighbors(BlockPos currentPos, List<BlockPos> map) {
        List<BlockPos> ret = new LinkedList<>();

        for (BlockPos inspectedPos : map) {
            if (posAreAdjacent(currentPos, inspectedPos)) {
                ret.add(inspectedPos);
            }
        }

        return ret;
    }

    public boolean posAreAdjacent(BlockPos a, BlockPos b) {
        boolean xx = (1 == (Math.abs(a.getX() - b.getX()))) && (0 == (Math.abs(a.getZ() - b.getZ())));
        boolean zz = (0 == (Math.abs(a.getX() - b.getX()))) && (1 == (Math.abs(a.getZ() - b.getZ())));
        return xor(xx, zz);
    }

    public boolean xor(boolean a, boolean b) {
        return ((a||b)&&!(a&&b)); // teehee
    }

    public List<BlockPos> findGreenWool(BlockPos initPos, Level level) {
        // create comparator through lambda :D
        Comparator<BlockPos> manhattanComparator = (p1, p2) -> Double.compare(manhattanDistance(initPos, p1), manhattanDistance(initPos, p2));

        // pack your bags
        Queue<BlockPos> bfsQueue = new PriorityQueue<>(manhattanComparator);
        bfsQueue.add(initPos);
        List<BlockPos> visited = new ArrayList<>();

        while (!bfsQueue.isEmpty()) {
            BlockPos currentPos = bfsQueue.remove();

            // ok, we just got here
            visited.add(currentPos);

            // what do we see?
            BlockPos[] adjPos = new BlockPos[]{
                    currentPos.north(), currentPos.south(),
                    currentPos.east(), currentPos.west()
            };

            // where can we go next?
            for (BlockPos inspectedPos : adjPos) {
                Block inspectedBlock = level.getBlockState(inspectedPos).getBlock();
                if (inspectedBlock.equals(Blocks.AIR) && !visited.contains(inspectedPos)) {
                    bfsQueue.add(inspectedPos);
                } else if (inspectedBlock.equals(Blocks.GREEN_WOOL)) {
                    visited.add(inspectedPos);
                    return visited;
                }
            }
        }

        return null;
    }

    private int manhattanDistance(BlockPos initPos, BlockPos targetPos) {
        int xx = Math.abs(initPos.getX() - targetPos.getX());
        int zz = Math.abs(initPos.getZ() - targetPos.getZ());
        return xx+zz;
    }

    public List<BlockPos> findGlass(BlockPos initPos, Level level) {
        // create comparator through lambda :D
        Comparator<BlockPos> manhattanComparator = (p1, p2) -> Double.compare(manhattanDistance(initPos, p1), manhattanDistance(initPos, p2));

        // pack your bags
        Queue<BlockPos> bfsQueue = new PriorityQueue<>(manhattanComparator);
        bfsQueue.add(initPos);
        List<BlockPos> visited = new ArrayList<>();

        while (!bfsQueue.isEmpty()) {
            BlockPos currentPos = bfsQueue.remove();

            // ok, we just got here
            visited.add(currentPos);

            // what do we see?
            BlockPos[] adjPos = new BlockPos[]{
                    currentPos.north(), currentPos.south(),
                    currentPos.east(), currentPos.west()
            };

            // where can we go next?
            for (BlockPos inspectedPos : adjPos) {
                Block inspectedBlock = level.getBlockState(inspectedPos).getBlock();
                if (inspectedBlock.equals(Blocks.GLASS) && !visited.contains(inspectedPos)) {
                    bfsQueue.add(inspectedPos);
                }
            }
        }

        return visited;
    }
}