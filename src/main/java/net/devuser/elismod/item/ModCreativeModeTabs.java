package net.devuser.elismod.item;

import net.devuser.elismod.ElisMod;
import net.devuser.elismod.block.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ElisMod.MOD_ID);

    public static final RegistryObject<CreativeModeTab> ELISMOD_TAB = CREATIVE_MODE_TABS.register("elismod_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.PATHFINDER.get())).title(Component.translatable("creativetab.elismod_tab"))
                    .displayItems((pParameters, pOutput) -> {

                        pOutput.accept(ModItems.ORE_FINDER.get());

                        pOutput.accept(ModItems.PATHFINDER.get());

                        pOutput.accept(ModBlocks.CELL_BLOCK.get());

                        pOutput.accept(ModBlocks.STEP_BLOCK.get());

                    })
                    .build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
