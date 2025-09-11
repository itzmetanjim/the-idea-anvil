package org.tanjimkamal.the_idea_anvil;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.BlockState;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ToolComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.regex.Pattern;

public class The_idea_anvil implements ModInitializer {

    public static final String MOD_ID = "the_idea_anvil";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    @Override
    public void onInitialize() {
        ModItems.initialize();
        ModComponents.initialize();
    }

    public static class ModComponents {
        protected static void initialize() {
            The_idea_anvil.LOGGER.info("Registering {} components", The_idea_anvil.MOD_ID);
            // Technically this method can stay empty, but some developers like to notify
            // the console, that certain parts of the mod have been successfully initialized
        }
        public static final ComponentType<String> USE_CMD = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            Identifier.of(The_idea_anvil.MOD_ID, "use_cmd"),
            ComponentType.<String>builder().codec(Codec.STRING).build()
        );

        public static final ComponentType<String> POST_HIT_CMD = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            Identifier.of(The_idea_anvil.MOD_ID, "post_hit_cmd"),
            ComponentType.<String>builder().codec(Codec.STRING).build()
        );

        public static final ComponentType<String> POST_MINE_CMD = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            Identifier.of(The_idea_anvil.MOD_ID, "post_mine_cmd"),
            ComponentType.<String>builder().codec(Codec.STRING).build()
        );

        public static final ComponentType<String> INVENTORY_TICK_CMD = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            Identifier.of(The_idea_anvil.MOD_ID, "inventory_tick_cmd"),
            ComponentType.<String>builder().codec(Codec.STRING).build()
        );

        public static final ComponentType<String> USE_ON_BLOCK_CMD = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            Identifier.of(The_idea_anvil.MOD_ID, "use_on_block_cmd"),
            ComponentType.<String>builder().codec(Codec.STRING).build()
        );
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
        public static class CustomItem extends Item{
            public CustomItem(Settings settings) {
                super(settings);
            }
            public static String applyRegexes(String input, List<Pattern> patterns, List<String> replacements) {
                if (patterns.size() != replacements.size()) {
                    throw new IllegalArgumentException("Patterns and replacements must have the same size");
                }

                String result = input;
                for (int i = 0; i < patterns.size(); i++) {
                    result = patterns.get(i).matcher(result).replaceAll(replacements.get(i));
                }
                return result;
            }
            public String[] getCommands(String cmdc,String targetUUID, String selfUUID,String posX,String posY, String posZ){
                Pattern regexS=Pattern.compile("(?<!#)#s");
                Pattern regexT=Pattern.compile("(?<!#)#t");
                Pattern regexX=Pattern.compile("(?<!#)#x");
                Pattern regexY=Pattern.compile("(?<!#)#y");
                Pattern regexZ=Pattern.compile("(?<!#)#z");
                Pattern regexP=Pattern.compile("(?<!#)#p");
                List<Pattern> patterns = Arrays.asList(regexS,regexT,regexX,regexY,regexZ,regexP);
                List<String> replacements = Arrays.asList(selfUUID,targetUUID,posX,posY,posZ,posX+" "+posY+" "+posZ);
                String cmdc1 = applyRegexes(cmdc,patterns,replacements);
                return cmdc1.split("(?<!#)#/ *");
            }
            public String[] getCmdsToRun(String[] cmds, String selfUUID){
                String[] cmdsToRun = new String[cmds.length];
                for (int i = 0; i < cmds.length; i++) {
                    if(!Objects.equals(cmds[i].strip(), "")){
                        cmdsToRun[i] = "execute as " + selfUUID + " at @s run " + cmds[i];
                    }
                    else{
                        cmdsToRun[i]="function the_idea_anvil:do_nothing";
                    }
                }
                return cmdsToRun;
            }
            @Override
            public ActionResult use(World world, PlayerEntity user, Hand hand) {
                // This is to prevent desync.
                if (world.isClient) {
                    return ActionResult.PASS;
                }
                String userUUID = user.getUuidAsString();
                LOGGER.trace("Used item, called use");
                String cmdc=user.getStackInHand(hand).getOrDefault(ModComponents.USE_CMD,"");
                String[] cmds = getCommands(cmdc,"",userUUID,"","","");
                runCmds(user, userUUID, cmds);

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
                String cmdc=stack.getOrDefault(ModComponents.POST_HIT_CMD,"");
                String[] cmds = getCommands(cmdc,targetUUID,attackerUUID,"","","");
                runCmds(attacker, attackerUUID, cmds);
                LOGGER.trace("Used item.");
            }

            private void runCmds(LivingEntity attacker, String attackerUUID, String[] cmds) {
                String[] cmdsToRun = getCmdsToRun(cmds,attackerUUID);
                MinecraftServer server = attacker.getServer();
                if(server == null){
                    LOGGER.debug("Reached server==null");
                    return;
                }
                genericCmdRun(cmdsToRun, server);
            }

            private void genericCmdRun(String[] cmdsToRun, MinecraftServer server) {
                ServerCommandSource commandSource = server.getCommandSource();
                CommandDispatcher<ServerCommandSource> dispatcher = commandSource.getDispatcher();
                for(String cmd : cmdsToRun){
                    if(!Objects.equals(cmd.strip(), "function the_idea_anvil:do_nothing") && !Objects.equals(cmd.strip(), "")) {
                        try {
                            dispatcher.execute(cmd, commandSource);
                        } catch (CommandSyntaxException e) {
                            throw new RuntimeException(e);
                        }
                    }else return;
                }
            }

            private void runCmdsWithServer(MinecraftServer server, String attackerUUID, String[] cmds) {
                String[] cmdsToRun = getCmdsToRun(cmds,attackerUUID);
                genericCmdRun(cmdsToRun, server);
            }

            @Override
            public boolean postMine(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity miner) {
            ToolComponent toolComponent = stack.get(DataComponentTypes.TOOL);
                if (toolComponent != null) {
                    if (!world.isClient && state.getHardness(world, pos) != 0.0F && toolComponent.damagePerBlock() > 0) {
                        stack.damage(toolComponent.damagePerBlock(), miner, EquipmentSlot.MAINHAND);
                    }
                    return true;
                }
                if (!world.isClient){
                    LOGGER.trace("Mined using item");
                    String minerUUID = miner.getUuidAsString();
                    String blockX= String.valueOf(pos.getX());
                    String blockY= String.valueOf(pos.getY());
                    String blockZ= String.valueOf(pos.getZ());
                    String cmdc=stack.getOrDefault(ModComponents.POST_MINE_CMD,"");
                    String[] cmds = getCommands(cmdc,"",minerUUID,blockX,blockY,blockZ);
                    runCmds(miner, minerUUID, cmds);
                    return true;
            }
            return false;

        }
        @Override
        public void inventoryTick(ItemStack stack, ServerWorld world, Entity entity, @Nullable EquipmentSlot slot) {
                if (world.isClient){
                    return;
                }
                LOGGER.trace("Inventory tick.");
                String selfUUID= entity.getUuidAsString();
                String cmdc=stack.getOrDefault(ModComponents.INVENTORY_TICK_CMD,"");
                String[] cmds = getCommands(cmdc,"",selfUUID,"","","");
                runCmdsWithServer(world.getServer(), selfUUID, cmds);
        }
        @Override
        public ActionResult useOnBlock(ItemUsageContext context) {
            assert context.getPlayer() != null;
            String selfUUID= context.getPlayer().getUuidAsString();
            String blockX = String.valueOf(context.getBlockPos().getX());
            String blockY = String.valueOf(context.getBlockPos().getY());
            String blockZ = String.valueOf(context.getBlockPos().getZ());

            String cmdc= context.getStack().getOrDefault(ModComponents.USE_ON_BLOCK_CMD,"");
            if(Objects.equals(cmdc, "")){return ActionResult.PASS;}
            String[] cmds = getCommands(cmdc,"",selfUUID,blockX,blockY,blockZ);
            runCmdsWithServer(context.getPlayer().getServer(), selfUUID, cmds);
            return ActionResult.SUCCESS;
        }

        }

        public static final Item CUSTOM_ITEM = register("custom_item", CustomItem::new, new CustomItem.Settings());

    }

}
