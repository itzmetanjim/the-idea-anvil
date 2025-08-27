package org.tanjimkamal.the_idea_anvil.item;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.CustomModelDataComponent;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

public class CustomTextureItem extends Item {
    public static final String TEXTURE_NAME_KEY = "texture_name";
    public static final String HUE_SHIFT_KEY = "hue_shift";

    public CustomTextureItem(Settings settings) {
        super(settings);
    }

    /**
     * Sets the texture name for an item stack
     * @param stack The item stack to modify
     * @param textureName The texture identifier (e.g., "minecraft:diamond_sword")
     */
    public static void setTextureName(ItemStack stack, String textureName) {
        NbtCompound nbt = getOrCreateNbt(stack);
        nbt.putString(TEXTURE_NAME_KEY, textureName);
        stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbt));
        updateCustomModelData(stack);
    }

    /**
     * Sets the hue shift value for an item stack
     * @param stack The item stack to modify
     * @param hueShift The hue shift value (0-360 degrees)
     */
    public static void setHueShift(ItemStack stack, int hueShift) {
        NbtCompound nbt = getOrCreateNbt(stack);
        nbt.putInt(HUE_SHIFT_KEY, hueShift % 360);
        stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbt));
        updateCustomModelData(stack);
    }

    /**
     * Sets both texture name and hue shift
     */
    public static void setTextureProperties(ItemStack stack, String textureName, int hueShift) {
        NbtCompound nbt = getOrCreateNbt(stack);
        nbt.putString(TEXTURE_NAME_KEY, textureName);
        nbt.putInt(HUE_SHIFT_KEY, hueShift % 360);
        stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbt));
        updateCustomModelData(stack);
    }    /**
     * Gets the texture name from an item stack
     */
    public static String getTextureName(ItemStack stack) {
        NbtComponent customData = stack.get(DataComponentTypes.CUSTOM_DATA);
        if (customData == null) return null;
        
        NbtCompound nbt = customData.copyNbt();
        return nbt.contains(TEXTURE_NAME_KEY) ? nbt.getString(TEXTURE_NAME_KEY).orElse(null) : null;
    }    /**
     * Gets the hue shift value from an item stack
     */
    public static int getHueShift(ItemStack stack) {
        NbtComponent customData = stack.get(DataComponentTypes.CUSTOM_DATA);
        if (customData == null) return 0;
        
        NbtCompound nbt = customData.copyNbt();
        return nbt.contains(HUE_SHIFT_KEY) ? nbt.getInt(HUE_SHIFT_KEY).orElse(0) : 0;
    }

    /**
     * Checks if the item has custom texture properties
     */
    public static boolean hasCustomTexture(ItemStack stack) {
        return getTextureName(stack) != null;
    }    /**
     * Updates custom model data based on texture properties
     */
    private static void updateCustomModelData(ItemStack stack) {
        if (hasCustomTexture(stack)) {
            // Use custom model data 1 to trigger custom renderer
            stack.set(DataComponentTypes.CUSTOM_MODEL_DATA, new CustomModelDataComponent(
                java.util.List.of(), // floats
                java.util.List.of(), // booleans
                java.util.List.of(), // strings
                java.util.List.of(1) // integers
            ));
        } else {
            // Remove custom model data to use default model
            stack.remove(DataComponentTypes.CUSTOM_MODEL_DATA);
        }
    }

    /**
     * Helper method to get or create NBT data
     */
    private static NbtCompound getOrCreateNbt(ItemStack stack) {
        NbtComponent customData = stack.get(DataComponentTypes.CUSTOM_DATA);
        if (customData != null) {
            return customData.copyNbt();
        }
        return new NbtCompound();
    }    @Override
    public Text getName(ItemStack stack) {
        String textureName = getTextureName(stack);
        if (textureName != null) {
            // Display the texture name in the item name to show it's working
            return Text.literal("Custom Item (" + textureName + ")");
        }
        return super.getName(stack);
    }    public void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType type) {
        String textureName = getTextureName(stack);
        if (textureName != null) {
            int hueShift = getHueShift(stack);
            tooltip.add(Text.literal("Texture: " + textureName).formatted(Formatting.AQUA));
            if (hueShift != 0) {
                tooltip.add(Text.literal("Hue Shift: " + hueShift + "°").formatted(Formatting.YELLOW));
            }
            tooltip.add(Text.literal("NBT stored successfully!").formatted(Formatting.GREEN));
        } else {
            tooltip.add(Text.literal("No custom texture").formatted(Formatting.GRAY));
        }
    }
}
