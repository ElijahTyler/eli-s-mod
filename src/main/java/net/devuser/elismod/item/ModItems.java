package net.devuser.elismod.item;

import net.devuser.elismod.ElisMod;
import net.devuser.elismod.item.custom.OreFinderItem;
import net.devuser.elismod.item.custom.PathfinderItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, ElisMod.MOD_ID);

    public static final RegistryObject<Item> ORE_FINDER = ITEMS.register("ore_finder",
            () -> new OreFinderItem(new Item.Properties()));

    public static final RegistryObject<Item> PATHFINDER = ITEMS.register("pathfinder",
            () -> new PathfinderItem(new Item.Properties()));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }


}
