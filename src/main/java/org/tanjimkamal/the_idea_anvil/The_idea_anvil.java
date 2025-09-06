package org.tanjimkamal.the_idea_anvil;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;

public class The_idea_anvil implements ModInitializer {

    public static final String MOD_ID = "the_idea_anvil";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        ModItems.initialize();
    }

    public static class ModItems {
        public static void initialize() {
            ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS)
		    .register((itemGroup) -> itemGroup.add(ModItems.CUSTOM_ITEM));
        }
        public static Item register(String name, Function<Item.Settings, Item> itemFactory, Item.Settings settings) {
            // Create the item key.
            RegistryKey<Item> itemKey = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(The_idea_anvil.MOD_ID, name));

            // Create the item instance.
            Item item = itemFactory.apply(settings.registryKey(itemKey));

            // Register the item.
            Registry.register(Registries.ITEM, itemKey, item);

            return item;
        }
        public static final Item CUSTOM_ITEM = register("custom_item", Item::new, new Item.Settings());

    }
}
