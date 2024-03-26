package net.devuser.elismod.datagen;

import net.devuser.elismod.ElisMod;
import net.devuser.elismod.block.ModBlocks;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockStateProvider extends BlockStateProvider {

    public ModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, ElisMod.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
//        blockWithItem(ModBlocks.STATE_BLOCK);

//        simpleBlockWithItem(ModBlocks.NEW_ENTITY.get(),
//                new ModelFile.UncheckedModelFile(modLoc("block/new_entity")));
    }

    private void blockWithItem(RegistryObject<Block> blockRegistryObject){
        simpleBlockWithItem(blockRegistryObject.get(), cubeAll(blockRegistryObject.get()));
    }
}
