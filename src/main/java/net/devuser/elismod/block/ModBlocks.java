package net.devuser.elismod.block;

import net.devuser.elismod.ElisMod;
import net.devuser.elismod.block.custom.GOLCellBlock;
import net.devuser.elismod.block.custom.GOLStepBlock;
import net.devuser.elismod.item.ModItems;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, ElisMod.MOD_ID);

//    public static final RegistryObject<Block> ORE_MINER_BLOCK = registerBlock("ore_miner_block",
//            () -> new OreMinerBlock(BlockBehaviour.Properties.copy(Blocks.STONE)));

    public static final RegistryObject<Block> CELL_BLOCK = registerBlock("cell_block",
            () -> new GOLCellBlock(BlockBehaviour.Properties.copy(Blocks.CLAY)));

    public static final RegistryObject<Block> STEP_BLOCK = registerBlock("step_block",
            () -> new GOLStepBlock(BlockBehaviour.Properties.copy(Blocks.CLAY)));



    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block){
        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block>RegistryObject<Item> registerBlockItem(String name, RegistryObject<T> block){
        return ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static void register(IEventBus eventBus){
        BLOCKS.register(eventBus);
    }
}
