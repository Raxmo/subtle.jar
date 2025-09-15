package io.liska.subtle;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.level.saveddata.SavedData;
import net.neoforged.neoforge.event.entity.EntityEvent;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.neoforged.neoforge.event.entity.player.PlayerContainerEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class InventoryMess
{
    private static final Random RANDOM = new Random();

    @SubscribeEvent
    public static void onInventoryClose(PlayerContainerEvent.Close event) {
        AbstractContainerMenu menu = event.getContainer();

        Container storage = getStorageContainer(menu);

        if (storage != null) { // Swap two random slots
            swapRandomSlots(storage);
        }
    }

    /**
     * Returns the first storage-like container in the given menu, or null if none.
     * Excludes player inventory and small functional containers.
     */
    private static Container getStorageContainer(AbstractContainerMenu menu) {
        if (menu == null) return null;

        for (Slot slot : menu.slots) {
            Container container = slot.container;

            // Skip player inventory slots
            if (container instanceof Inventory) continue;

            // Treat as storage-like if size >= 9
            if (container.getContainerSize() >= 9) {
                return container;
            }
        }

        return null;
    }


    private static void swapRandomSlots(Container container) {
        if (container == null) return;

        int size = container.getContainerSize();
        if (size < 2) return; // need at least two slots

        // Collect indices of slots that have items
        List<Integer> filledSlots = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            if (!container.getItem(i).isEmpty()) {
                filledSlots.add(i);
            }
        }

        if (filledSlots.size() < 2) return; // not enough items to swap

        // Pick two random, distinct slots
        int index1 = filledSlots.get(RANDOM.nextInt(filledSlots.size()));
        int index2;
        do {
            index2 = filledSlots.get(RANDOM.nextInt(filledSlots.size()));
        } while (index2 == index1);

        // Swap the items
        ItemStack temp = container.getItem(index1).copy();
        container.setItem(index1, container.getItem(index2));
        container.setItem(index2, temp);
    }
}
