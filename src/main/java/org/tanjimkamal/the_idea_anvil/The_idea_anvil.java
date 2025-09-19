package org.tanjimkamal.the_idea_anvil;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
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
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Pattern;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Stack;
import java.util.regex.Matcher;

import net.minecraft.component.ComponentMap;
import net.minecraft.server.network.ServerPlayerEntity;
import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.DataResult;


public class The_idea_anvil implements ModInitializer {
    public static final String MOD_ID = "the_idea_anvil";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

private static final String BASE_URL = "https://ai.hackclub.com/chat/completions";
private static String sysprompt;


private void loadSystemPrompt() {
    try {
        InputStream inputStream = getClass().getResourceAsStream("/sysprompt.md");
        if (inputStream == null) {
            throw new RuntimeException("sysprompt.md not found in resources");
        }
        sysprompt = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        inputStream.close();
    } catch (IOException e) {
        throw new RuntimeException("Failed to load sysprompt.md", e);
    }
}

/**
 * Extract JSON from a model response and return the parsed JsonObject.
 * Strategy:
 * 1) Prefer a ```json fenced code block (case-insensitive).
 * 2) Fallback to any fenced code block.
 * 3) Find balanced JSON (handles strings/escapes)
 *
 * @param text The response text to parse
 * @param returnText If true, returns the raw JSON string instead of parsed JsonObject
 * @return JsonObject or String depending on returnText parameter
 * @throws IllegalArgumentException if no valid JSON is found
 */
@SuppressWarnings("SameParameterValue")
private Object extractJsonFromResponse(String text, boolean returnText) {
    // look for a ```json fenced block
    Pattern jsonPattern = Pattern.compile("```json\\s*\\n([\\s\\S]*?)\\n```", Pattern.CASE_INSENSITIVE);
    Matcher matcher = jsonPattern.matcher(text);
    if (matcher.find()) {
        String candidate = matcher.group(1).strip();
        try {
            return returnText ? candidate : JsonParser.parseString(candidate).getAsJsonObject();
        } catch (JsonSyntaxException e) {
            // continue
        }
    }

    // other fenced code block
    Pattern codePattern = Pattern.compile("```[\\w+-]*\\n([\\s\\S]*?)\\n```");
    matcher = codePattern.matcher(text);
    if (matcher.find()) {
        String candidate = matcher.group(1).strip();
        try {
            return returnText ? candidate : JsonParser.parseString(candidate).getAsJsonObject();
        } catch (JsonSyntaxException e) {
            // continue to fallbacks
        }
    }

    // find balanced {} / []
    for (int idx = 0; idx < text.length(); idx++) {
        char ch = text.charAt(idx);
        if (ch == '{' || ch == '[') {
            String candidate = extractBalancedFrom(text, idx);
            if (candidate != null) {
                try {
                    return returnText ? candidate : JsonParser.parseString(candidate).getAsJsonObject();
                } catch (JsonSyntaxException e) {
                    // maybe malformed
                }
            }
        }
    }

    throw new IllegalArgumentException("No valid JSON found in the provided text.");
}

private Object extractJsonFromResponse(String text) {
    return extractJsonFromResponse(text, false);
}

private String extractBalancedFrom(String text, int start) {
    Stack<Character> stack = new Stack<>();
    boolean inStr = false;
    boolean esc = false;

    for (int i = start; i < text.length(); i++) {
        char ch = text.charAt(i);

        if (esc) {
            esc = false;
            continue;
        }

        if (ch == '\\') {
            esc = true;
            continue;
        }

        if (ch == '"') {
            inStr = !inStr;
            continue;
        }

        if (inStr) {
            continue;
        }

        if (ch == '{') {
            stack.push('}');
        } else if (ch == '[') {
            stack.push(']');
        } else if (ch == '}' || ch == ']') {
            if (stack.isEmpty() || ch != stack.peek()) {
                return null;
            }
            stack.pop();
            if (stack.isEmpty()) {
                return text.substring(start, i + 1);
            }
        }
    }

    return null;
}

public JsonObject getItem(String itemDesc) {
    try {
        JsonObject requestBody = getRequestBody(itemDesc);
        String content;
        try(HttpClient client = HttpClient.newHttpClient()) {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            JsonObject responseJson = JsonParser.parseString(response.body()).getAsJsonObject();
            System.out.println(responseJson.toString().substring(0, Math.min(100, responseJson.toString().length())));

            content = responseJson.getAsJsonArray("choices")
                    .get(0).getAsJsonObject()
                    .getAsJsonObject("message")
                    .get("content").getAsString();

            System.out.println(content);
        }
        return (JsonObject) extractJsonFromResponse(content);

    } catch (IOException | InterruptedException e) {
        throw new RuntimeException("Failed to get item", e);
    }
}

    private static @NotNull JsonObject getRequestBody(String itemDesc) {
        JsonObject requestBody = new JsonObject();
        JsonArray messages = new JsonArray();

        JsonObject systemMessage = new JsonObject();
        systemMessage.addProperty("role", "system");
        systemMessage.addProperty("content", sysprompt);
        messages.add(systemMessage);

        JsonObject userMessage = new JsonObject();
        userMessage.addProperty("role", "user");
        userMessage.addProperty("content", itemDesc);
        messages.add(userMessage);

        requestBody.add("messages", messages);
        requestBody.addProperty("model", "moonshotai/kimi-k2-instruct-0905");
        requestBody.addProperty("temperature", 0.1);
        return requestBody;
    }
    public void giveItemToPlayer(ServerPlayerEntity player, String itemId, JsonElement components) {
    try {
        Identifier itemIdentifier = Identifier.of(itemId);
        Item item = Registries.ITEM.get(itemIdentifier);

        ItemStack itemStack = new ItemStack(item);

        if (components != null && components.isJsonObject()) {
            DataResult<ComponentMap> result = ComponentMap.CODEC.parse(JsonOps.INSTANCE, components);

            if (result.result().isPresent()) {
                ComponentMap componentMap = result.result().get();
                itemStack.applyComponentsFrom(componentMap);
            } else {
                LOGGER.warn("Failed to parse components: {}", result.error().map(Object::toString).orElse("Unknown error"));
                return;
            }
        }

        boolean success = player.getInventory().insertStack(itemStack);

        if (!success) {
            player.dropItem(itemStack, false);
        }

    } catch (Exception e) {
        LOGGER.warn(e.toString());
    }
}

    @Override
    public void onInitialize() {
        ModItems.initialize();
        ModComponents.initialize();
        loadSystemPrompt();
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(CommandManager.literal("idea")
                .then(CommandManager.argument("value", StringArgumentType.greedyString())
                .executes(this::ideaCommand))));
        /* if any problems near here then try this
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal("idea")
                    .then(CommandManager.argument("value", StringArgumentType.greedyString())
                    .executes(this::ideaCommand)));
        });
        */
    }

    private int ideaCommand(CommandContext<ServerCommandSource> context) {
        context.getSource().sendFeedback(() -> Text.literal("Getting item..."), true);
        String value = StringArgumentType.getString(context, "value");
        JsonObject response = getItem(value);
        if (!response.get("accepted").getAsBoolean()) {
            context.getSource().sendFeedback(() -> Text.literal("Sorry, the AI determined your item is too OP or didn't understand you. "+value), true);
            return 0;
        }
        ServerPlayerEntity player = context.getSource().getPlayer();
        JsonObject components = response.get("components").getAsJsonObject();
        giveItemToPlayer(player,"the_idea_anvil:custom_item",components);
        context.getSource().sendFeedback(() -> Text.literal("Enjoy your item! "+value), true);
        return 1;
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
            @Override
            public ActionResult use(World world, PlayerEntity user, Hand hand) {
                if (world.isClient) {
                    return ActionResult.PASS;
                }
                String userUUID = user.getUuidAsString();
                String cmdc = user.getStackInHand(hand).getOrDefault(ModComponents.USE_CMD, "");
                if (cmdc.isEmpty()) {
                    return ActionResult.PASS;
                }

                String[] cmds = getCommands(cmdc, "", userUUID, "", "", "");
                executeCommands(cmds, user);

                return ActionResult.SUCCESS;
            }

            @Override
            public void postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
                if (target.getWorld().isClient()) {
                    return;
                }
                String targetUUID = target.getUuidAsString();
                String attackerUUID = attacker.getUuidAsString();
                String cmdc = stack.getOrDefault(ModComponents.POST_HIT_CMD, "");
                if (cmdc.isEmpty()) {
                    return;
                }

                String[] cmds = getCommands(cmdc, targetUUID, attackerUUID, "", "", "");
                executeCommands(cmds, attacker);
            }

            private void executeCommands(String[] commands, Entity executor) {
                if (executor == null || executor.getWorld().isClient()) {
                    return;
                }

                MinecraftServer server = executor.getServer();
                if (server == null) {
                    LOGGER.warn("Could not execute commands for entity {} because its server instance was null.", executor.getUuidAsString());
                    return;
                }

                if (!(executor.getWorld() instanceof ServerWorld serverWorld)) {
                    return;
                }

                ServerCommandSource source = executor.getCommandSource(serverWorld).withLevel(2);

                CommandDispatcher<ServerCommandSource> dispatcher = server.getCommandManager().getDispatcher();

                for (String cmd : commands) {
                    String strippedCmd = cmd.strip();
                    if (strippedCmd.isEmpty()) continue;

                    try {
                        dispatcher.execute(strippedCmd, source);
                    } catch (CommandSyntaxException e) {
                        source.sendError(Text.literal("Invalid command syntax."));
                        LOGGER.warn("CommandSyntaxException for command '{}': {}", strippedCmd, e.getMessage());
                    } catch (Exception e) {
                        source.sendError(Text.literal("An error occurred while running a command."));
                        LOGGER.error("An unexpected error occurred executing command '{}' for entity {}", strippedCmd, executor.getUuidAsString(), e);
                    }
                }
            }


            @Override
            public boolean postMine(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity miner) {
                ToolComponent toolComponent = stack.get(DataComponentTypes.TOOL);
                if (toolComponent != null) {
                    if (!world.isClient && state.getHardness(world, pos) != 0.0F && toolComponent.damagePerBlock() > 0) {
                        stack.damage(toolComponent.damagePerBlock(), miner, EquipmentSlot.MAINHAND);
                    }
                }

                if (!world.isClient) {
                    String minerUUID = miner.getUuidAsString();
                    String blockX = String.valueOf(pos.getX());
                    String blockY = String.valueOf(pos.getY());
                    String blockZ = String.valueOf(pos.getZ());
                    String cmdc = stack.getOrDefault(ModComponents.POST_MINE_CMD, "");

                    if (!cmdc.isEmpty()) {
                        String[] cmds = getCommands(cmdc, "", minerUUID, blockX, blockY, blockZ);
                        executeCommands(cmds, miner);
                    }
                }
                return true;
}
        @Override
        public void inventoryTick(ItemStack stack, ServerWorld world, Entity entity, @Nullable EquipmentSlot slot) {
            String selfUUID = entity.getUuidAsString();
            String cmdc = stack.getOrDefault(ModComponents.INVENTORY_TICK_CMD, "");
            if (cmdc.isEmpty()) {
                return;
            }

            String[] cmds = getCommands(cmdc, "", selfUUID, "", "", "");
            executeCommands(cmds, entity);
        }
        @Override
        public ActionResult useOnBlock(ItemUsageContext context) {
            PlayerEntity player = context.getPlayer();
            if (player == null || player.getWorld().isClient) {
                return ActionResult.PASS;
            }

            String selfUUID = player.getUuidAsString();
            String blockX = String.valueOf(context.getBlockPos().getX());
            String blockY = String.valueOf(context.getBlockPos().getY());
            String blockZ = String.valueOf(context.getBlockPos().getZ());

            String cmdc = context.getStack().getOrDefault(ModComponents.USE_ON_BLOCK_CMD, "");
            if (cmdc.isEmpty()) {
                return ActionResult.PASS;
            }

            String[] cmds = getCommands(cmdc, "", selfUUID, blockX, blockY, blockZ);
            executeCommands(cmds, player);
            return ActionResult.SUCCESS;
        }

        }

        public static final Item CUSTOM_ITEM = register("custom_item", CustomItem::new, new CustomItem.Settings());

    }

}
