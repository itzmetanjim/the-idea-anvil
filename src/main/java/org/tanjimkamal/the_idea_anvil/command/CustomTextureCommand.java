package org.tanjimkamal.the_idea_anvil.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.tanjimkamal.the_idea_anvil.The_idea_anvil;
import org.tanjimkamal.the_idea_anvil.item.CustomTextureItem;

public class CustomTextureCommand {
    
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(CommandManager.literal("customtexture")
            .requires(source -> source.hasPermissionLevel(2))            .then(CommandManager.literal("set")
                .then(CommandManager.argument("texture", StringArgumentType.greedyString())
                    .executes(CustomTextureCommand::setTexture)
                    .then(CommandManager.argument("hueshift", IntegerArgumentType.integer(0, 360))
                        .executes(CustomTextureCommand::setTextureWithHue))))
            .then(CommandManager.literal("hue")
                .then(CommandManager.argument("degrees", IntegerArgumentType.integer(0, 360))
                    .executes(CustomTextureCommand::setHueShift)))
            .then(CommandManager.literal("clear")
                .executes(CustomTextureCommand::clearTexture))            .then(CommandManager.literal("give")
                .then(CommandManager.argument("texture", StringArgumentType.greedyString())
                    .executes(CustomTextureCommand::giveCustomItem)
                    .then(CommandManager.argument("hueshift", IntegerArgumentType.integer(0, 360))
                        .executes(CustomTextureCommand::giveCustomItemWithHue))))
        );
    }
    
    private static int setTexture(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        String texture = StringArgumentType.getString(context, "texture");
        
        ItemStack stack = player.getMainHandStack();
        if (!(stack.getItem() instanceof CustomTextureItem)) {
            context.getSource().sendError(Text.literal("You must hold a custom texture item"));
            return 0;
        }
        
        CustomTextureItem.setTextureName(stack, texture);
        context.getSource().sendFeedback(() -> Text.literal("Set texture to: " + texture), false);
        return 1;
    }
    
    private static int setTextureWithHue(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        String texture = StringArgumentType.getString(context, "texture");
        int hueShift = IntegerArgumentType.getInteger(context, "hueshift");
        
        ItemStack stack = player.getMainHandStack();
        if (!(stack.getItem() instanceof CustomTextureItem)) {
            context.getSource().sendError(Text.literal("You must hold a custom texture item"));
            return 0;
        }
        
        CustomTextureItem.setTextureProperties(stack, texture, hueShift);
        context.getSource().sendFeedback(() -> Text.literal("Set texture to: " + texture + " with hue shift: " + hueShift + "°"), false);
        return 1;
    }
    
    private static int setHueShift(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        int hueShift = IntegerArgumentType.getInteger(context, "degrees");
        
        ItemStack stack = player.getMainHandStack();
        if (!(stack.getItem() instanceof CustomTextureItem)) {
            context.getSource().sendError(Text.literal("You must hold a custom texture item"));
            return 0;
        }
        
        CustomTextureItem.setHueShift(stack, hueShift);
        context.getSource().sendFeedback(() -> Text.literal("Set hue shift to: " + hueShift + "°"), false);
        return 1;
    }
    
    private static int clearTexture(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        
        ItemStack stack = player.getMainHandStack();
        if (!(stack.getItem() instanceof CustomTextureItem)) {
            context.getSource().sendError(Text.literal("You must hold a custom texture item"));
            return 0;
        }
        
        stack.remove(net.minecraft.component.DataComponentTypes.CUSTOM_DATA);
        stack.remove(net.minecraft.component.DataComponentTypes.CUSTOM_MODEL_DATA);
        context.getSource().sendFeedback(() -> Text.literal("Cleared custom texture"), false);
        return 1;
    }
    
    private static int giveCustomItem(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        String texture = StringArgumentType.getString(context, "texture");
        
        ItemStack stack = new ItemStack(The_idea_anvil.ModItems.CUSTOM_ITEM);
        CustomTextureItem.setTextureName(stack, texture);
        player.giveItemStack(stack);
        
        context.getSource().sendFeedback(() -> Text.literal("Gave custom item with texture: " + texture), false);
        return 1;
    }
    
    private static int giveCustomItemWithHue(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        String texture = StringArgumentType.getString(context, "texture");
        int hueShift = IntegerArgumentType.getInteger(context, "hueshift");
        
        ItemStack stack = new ItemStack(The_idea_anvil.ModItems.CUSTOM_ITEM);
        CustomTextureItem.setTextureProperties(stack, texture, hueShift);
        player.giveItemStack(stack);
        
        context.getSource().sendFeedback(() -> Text.literal("Gave custom item with texture: " + texture + " and hue shift: " + hueShift + "°"), false);
        return 1;
    }
}
