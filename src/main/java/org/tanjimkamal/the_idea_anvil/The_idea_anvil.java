package org.tanjimkamal.the_idea_anvil;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.BlockState;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ToolComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
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
        public class CustomItem extends Item{
            public CustomItem(Settings settings) {
                super(settings);
            }
            @Override
            public ActionResult use(World world, PlayerEntity user, Hand hand) {
                // This is to prevent desync.
                if (world.isClient) {
                    return ActionResult.PASS;
                }
                String userUUID = user.getUuidAsString();
                LOGGER.info("Used item, called use");
                return ActionResult.SUCCESS;
            }

            @Override
            public void postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
                // This is to prevent desync.
                if (target.getWorld().isClient()) {
                    return;
                }
                String targetUUID = target.getUuidAsString(); // example 8f5f2803-2c9b-4c6c-90d4-6d53828d245b
                String attackerUUID = attacker.getUuidAsString();

                LOGGER.info("Used item.");
            }
            @Override
            public boolean postMine(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity miner) {
            ToolComponent toolComponent = (ToolComponent)stack.get(DataComponentTypes.TOOL);
            if (toolComponent == null) {
                return false;
            } else {
                if (!world.isClient && state.getHardness(world, pos) != 0.0F && toolComponent.damagePerBlock() > 0) {
                    stack.damage(toolComponent.damagePerBlock(), miner, EquipmentSlot.MAINHAND);
                }
                if (!world.isClient){
                    LOGGER.info("Mined using item");
                    String minerUUID = miner.getUuidAsString();
                }

                return true;
            }
        }

        }
        public static final Item CUSTOM_ITEM = register("custom_item", Item::new, new Item.Settings());

    }
    public class ModComponents {
        protected static void initialize() {
            The_idea_anvil.LOGGER.info("Registering {} components", The_idea_anvil.MOD_ID);
            // Technically this method can stay empty, but some developers like to notify
            // the console, that certain parts of the mod have been successfully initialized
        }
        public static final ComponentType<String[]> USE_CMD = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            Identifier.of(The_idea_anvil.MOD_ID, "use_cmd"),
            ComponentType.<String[]>builder().codec(null).build()
        );

        public static final ComponentType<String[]> POST_HIT_CMD = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            Identifier.of(The_idea_anvil.MOD_ID, "postHit_cmd"),
            ComponentType.<String[]>builder().codec(null).build()
        );

        public static final ComponentType<String[]> POST_MINE_CMD = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            Identifier.of(The_idea_anvil.MOD_ID, "postMine_cmd"),
            ComponentType.<String[]>builder().codec(null).build()
        );

        public static final ComponentType<String[]> INVENTORY_TICK_CMD = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            Identifier.of(The_idea_anvil.MOD_ID, "inventoryTick_cmd"),
            ComponentType.<String[]>builder().codec(null).build()
        );

        public static final ComponentType<String[]> USE_ON_BLOCK_CMD = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            Identifier.of(The_idea_anvil.MOD_ID, "useOnBlock_cmd"),
            ComponentType.<String[]>builder().codec(null).build()
        );
    }
}
