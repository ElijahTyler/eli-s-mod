package net.devuser.elismod.item.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
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

        // alright literally what the frick is happening here

        // we gotta:
        // 0. generate map of air blocks with predecessors
        // 0.5. if none found, remove adjacent glass chains
        // 1. locate the green wool (maybe redundant?)
        // 2. see which air block neighboring the green wool is the closest to the red wool
        // (THROUGH THE AIR MAP, not through Manhattan distance)
        // 3. fill in those blocks with glass
        // 4. profit



        // 0. generate map of air blocks with predecessors
        HashMap<BlockPos, BlockPos> airMapWithPredecessors = getAirMapWithPredecessors(clickedPos, level);

        // 0.5. if none found, remove adjacent glass chains
        if (airMapWithPredecessors == null) {
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

        // 1. locate the green wool
        Set<BlockPos> blockPosSet = airMapWithPredecessors.keySet();
        BlockPos greenWoolPos = findGreenWool(blockPosSet, level);

        // we shouldn't set the green wool block to glass
        blockPosSet.remove(greenWoolPos);
        airMapWithPredecessors.remove(greenWoolPos);

        // 2. see which air block neighboring the green wool is the closest to the red wool

        // 2a. find all air adjacent to green wool and in the air map
        Set<BlockPos> airNeighboringGreenWool = getNeighbors(greenWoolPos, blockPosSet);

        // 2b. see which has minimal distance to red through predecessor chain
        int bestDistance = Integer.MAX_VALUE;
        BlockPos bestPos = null;
        for (BlockPos aNGW : airNeighboringGreenWool) {
            int bruh = getDistanceThroughHashMap(airMapWithPredecessors, aNGW);
            if (bruh < bestDistance) {
                bestDistance = bruh;
                bestPos = aNGW;
            }
        }

        // 3. fill in the blocks in the predecessor chain from bestPos with glass
        // bestPos essentially is the tail of our chain

        while (airMapWithPredecessors.get(bestPos) != null) {
            level.setBlock(bestPos, Blocks.GLASS.defaultBlockState(), 3);
            bestPos = airMapWithPredecessors.get(bestPos);
        }

        return InteractionResult.SUCCESS;
    }

    public HashMap<BlockPos, BlockPos> getAirMapWithPredecessors(BlockPos clickedPos, Level level) {
        // how the frick are we doing this literally
        // 1. create hash map airMapWithPredecessors with clickedPos as the "root" node of the tree
        // 2. bread first search until we find green wool, then we're done

        // 1.
        HashMap<BlockPos, BlockPos> airMapWithPredecessors = new HashMap<>();
        airMapWithPredecessors.put(clickedPos, null);

        // 2.

        // queue is for searching the air
        Queue<BlockPos> queue = new LinkedList<>();
        queue.add(clickedPos);

        // visited is for seeing where we've been
        Set<BlockPos> visited = new HashSet<>();

        BlockPos currentPos;

        while (!queue.isEmpty()) {
            currentPos = queue.remove();

            visited.add(currentPos);

            BlockPos[] adjPos = new BlockPos[]{
                    currentPos.north(), currentPos.south(),
                    currentPos.east(), currentPos.west()
            };

            // where can we go next?
            for (BlockPos inspectedPos : adjPos) {
                Block inspectedBlock = level.getBlockState(inspectedPos).getBlock();

                if (inspectedBlock.equals(Blocks.AIR) && !visited.contains(inspectedPos)) {

                    airMapWithPredecessors.put(inspectedPos, currentPos);
                    queue.add(inspectedPos);

                } else if (inspectedBlock.equals(Blocks.GREEN_WOOL)) {

                    airMapWithPredecessors.put(inspectedPos, currentPos);
                    return airMapWithPredecessors;

                }
            }
        }

        return null;
    }

    private int getDistanceThroughHashMap(HashMap<BlockPos, BlockPos> blockPosHashMap, BlockPos startPos) {
        BlockPos iterPos = startPos;
        int count = 0;

        while (iterPos != null) {
            iterPos = blockPosHashMap.get(iterPos);
            count++;
        }

        return count;
    }

    private Set<BlockPos> getNeighbors(BlockPos currentPos, Set<BlockPos> map) {
        Set<BlockPos> ret = new HashSet<>();

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

    private BlockPos findGreenWool(Set<BlockPos> blockPosSet, Level level) {
        for (BlockPos p : blockPosSet) {
            Block inspectedBlock = level.getBlockState(p).getBlock();
            if (inspectedBlock.equals(Blocks.GREEN_WOOL)) return p;
        }

        return null;
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

    public List<BlockPos> findGlass(BlockPos initPos, Level level) {
        // pack your bags
        Queue<BlockPos> bfsQueue = new PriorityQueue<>();
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