package net.devuser.elismod.item.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.Tags;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

public class OreFinderItem extends Item {
    public OreFinderItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        BlockPos posClicked = pContext.getClickedPos();
        Level level = pContext.getLevel();

        TagKey<Block> searchCondition = Tags.Blocks.ORES;

        if (!pContext.getLevel().isClientSide() && level.getBlockState(posClicked).is(searchCondition)) {
            Player player = pContext.getPlayer();

            Set<BlockPos> blocksFound = dfs(posClicked, level, player, searchCondition);

            String endMessage = (blocksFound.size() != 1) ? "s" : "";
            player.sendSystemMessage(Component.literal("Found " + blocksFound.size() + " block" + endMessage));

            pContext.getItemInHand().hurtAndBreak(1, player, player1 -> player.broadcastBreakEvent(player.getUsedItemHand()));

            return InteractionResult.SUCCESS;
        } else {
            return InteractionResult.FAIL;
        }
    }

    public Set<BlockPos> dfs(BlockPos initPos, Level pLevel, Player pPlayer, TagKey<Block> searchCondition) {
        Stack<BlockPos> stack = new Stack<>();
        Set<BlockPos> visited = new HashSet<>();
        Set<BlockPos> blocksFound = new HashSet<>();

        // push current pos in prep for depth-first search
        stack.push(initPos);
        visited.add(initPos);
        blocksFound.add(initPos);

        while (!stack.isEmpty()) {
            // pop from top of stack
            BlockPos currentPos = stack.pop();
            visited.add(currentPos);

            // get all adjacent blocks
            BlockPos[] adjPos = new BlockPos[]{
                    currentPos.north(), currentPos.south(),
                    currentPos.east(), currentPos.west(),
                    currentPos.above(), currentPos.below()
            };

            for (BlockPos inspectedPos : adjPos) {
                BlockState inspectedBlock = pLevel.getBlockState(inspectedPos);
                if (inspectedBlock.is(searchCondition) && !visited.contains(inspectedPos)) {
                    stack.push(inspectedPos);

                    blocksFound.add(inspectedPos);
                }
            }
        }

        return blocksFound;
    }
}