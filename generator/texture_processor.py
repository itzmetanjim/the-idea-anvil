#!/usr/bin/env python3
"""
Texture Processing Script for The Idea Anvil Mod

This script processes texture files to:
1. Take input textures (swords, bows, etc.)
2. HSV shift them so their average color is #ff0000 (red)
3. Store redded versions in processed/redded/ folder
4. Create hue-shifted versions in common colors (blue, purple, etc.)
5. Generate model files with CustomModelData mappings
6. Create JSON mapping file for texture,color -> CustomModelData values

Usage:
    python texture_processor.py input_folder output_folder
"""

import os
import sys
import json
import argparse
from PIL import Image, ImageStat, ImageEnhance
import colorsys
import numpy as np
from pathlib import Path

class TextureProcessor:
    def __init__(self, input_folder, output_folder):
        self.input_folder = Path(input_folder)
        self.output_folder = Path(output_folder)
        self.redded_folder = self.output_folder / "redded"
        self.colored_folder = self.output_folder / "colored"
        self.models_folder = self.output_folder / "models"
        self.textures_folder = self.output_folder / "textures"
        
        # Target colors for hue shifting - Minecraft colors + additional
        self.target_colors = {
            # Minecraft 16 colors
            "white": (0, 0, 250),        # #F9FFFE -> HSV(0, 0, 250)
            "orange": (25, 200, 248),    # #F9801D -> HSV(25, 200, 248)
            "magenta": (300, 150, 200),  # #C74EBD -> HSV(300, 150, 200)
            "light_blue": (195, 120, 220), # #3AB3DA -> HSV(195, 120, 220)
            "yellow": (55, 180, 254),    # #FED83D -> HSV(55, 180, 254)
            "lime": (90, 180, 190),      # #80C71F -> HSV(90, 180, 190)
            "pink": (340, 100, 245),     # #F38BAA -> HSV(340, 100, 245)
            "gray": (0, 20, 120),        # #474F52 -> HSV(0, 20, 120)
            "light_gray": (45, 10, 160), # #9D9D97 -> HSV(45, 10, 160)
            "cyan": (180, 150, 155),     # #169C9C -> HSV(180, 150, 155)
            "purple": (270, 180, 145),   # #8932B8 -> HSV(270, 180, 145)
            "blue": (230, 150, 170),     # #3C44AA -> HSV(230, 150, 170)
            "brown": (20, 120, 135),     # #835432 -> HSV(20, 120, 135)
            "green": (85, 150, 125),     # #5E7C16 -> HSV(85, 150, 125)
            "red": (0, 180, 180),        # #B02E26 -> HSV(0, 180, 180)
            "black": (0, 0, 30),         # #1D1D21 -> HSV(0, 0, 30)
            # Additional colors
            "indigo": (250, 150, 155),   # #4b369d -> HSV(250, 150, 155)
            "emerald": (91,196,98), # #
            "redstone": (174,49,29), # #
            "stone_gray": (45, 15, 180), # #b3b1af -> HSV(45, 15, 180)
            "iron_gray": (0, 0, 220),    # #d8d8d8 -> HSV(0, 0, 220)
            "gold": (50, 150, 254),      # #fdf55f -> HSV(50, 150, 254)
            "copper": (15, 120, 250),    # #fc9982 -> HSV(15, 120, 250)
            "diamond": (165, 100, 250),  # #a4fdf0 -> HSV(165, 100, 250)
            "netherite": (270, 50, 115), # #706770 -> HSV(270, 50, 115)
            "amethyst": (270, 120, 185), # #b38ef3 -> HSV(270, 120, 185)
            "wood_dark": (35, 120, 140), # #866526 -> HSV(35, 120, 140)
            "wood_medium": (45, 80, 195), # #c29e63 -> HSV(45, 80, 195)
            "wood_light": (55, 60, 220), # #d7cc8e -> HSV(55, 60, 220)
            "wood_cherry": (5, 50, 215), # #d6ada8 -> HSV(5, 50, 215)
            "wood_crimson": (340, 120, 130), # #7f3a57 -> HSV(340, 120, 130)
            "wood_acacia": (20, 150, 175), # #ac5b32 -> HSV(20, 150, 175)
            "grass": (90, 100, 165)      # #517a47 -> HSV(90, 100, 165)
        }
        
        self.mapping = {}
        self.current_model_data = 1
        
    def setup_folders(self):
        """Create output folder structure"""
        for folder in [self.redded_folder, self.colored_folder, 
                      self.models_folder, self.textures_folder]:
            folder.mkdir(parents=True, exist_ok=True)
    
    def get_average_color(self, image):
        """Get average color of image as HSV"""
        stat = ImageStat.Stat(image)
        r, g, b = stat.mean[:3]
        h, s, v = colorsys.rgb_to_hsv(r/255.0, g/255.0, b/255.0)
        return h * 360, s * 100, v * 100
    
    def shift_to_red(self, image):
        """Shift image average color to red (#ff0000)"""
        # Convert to HSV
        hsv_image = image.convert('HSV')
        h, s, v = hsv_image.split()
        
        # Get current average hue
        current_h, current_s, current_v = self.get_average_color(image)
        
        # Calculate hue shift needed to make average red (hue 0)
        hue_shift = -current_h
        
        # Apply hue shift
        h_array = np.array(h)
        h_array = (h_array + hue_shift) % 256
        
        # Reconstruct image
        new_h = Image.fromarray(h_array.astype('uint8'), 'L')
        shifted_hsv = Image.merge('HSV', [new_h, s, v])
        return shifted_hsv.convert('RGBA')
    
    def shift_hue(self, image, target_hue):
        """Shift image hue to target hue (0-360)"""
        hsv_image = image.convert('HSV')
        h, s, v = hsv_image.split()
        
        # Get current average hue
        current_h, _, _ = self.get_average_color(image)
        
        # Calculate hue shift
        hue_shift = target_hue - current_h
        
        # Apply hue shift
        h_array = np.array(h)
        h_array = (h_array + hue_shift * 255/360) % 256
        
        # Reconstruct image
        new_h = Image.fromarray(h_array.astype('uint8'), 'L')
        shifted_hsv = Image.merge('HSV', [new_h, s, v])
        return shifted_hsv.convert('RGBA')
    
    def process_texture(self, texture_path):
        """Process a single texture file"""
        print(f"Processing {texture_path.name}...")
        
        # Load image
        image = Image.open(texture_path).convert('RGBA')
        texture_name = texture_path.stem
        
        # Step 0: Copy original texture to textures folder
        original_path = self.textures_folder / f"{texture_name}_original.png"
        image.save(original_path)
        
        # Add original to mapping
        original_key = f"{texture_name},original"
        self.mapping[original_key] = self.current_model_data
        self.current_model_data += 1
        
        # Step 1: Shift to red and save
        red_image = self.shift_to_red(image)
        red_path = self.redded_folder / f"{texture_name}_red.png"
        red_image.save(red_path)
        
        # Step 2: Create colored versions
        for color_name, (hue, sat, val) in self.target_colors.items():
            colored_image = self.shift_hue(red_image, hue)
            
            # Save colored texture
            colored_path = self.colored_folder / f"{texture_name}_{color_name}.png"
            colored_image.save(colored_path)
            
            # Copy to textures folder with proper naming
            texture_output_path = self.textures_folder / f"{texture_name}_{color_name}.png"
            colored_image.save(texture_output_path)
            
            # Add to mapping
            mapping_key = f"{texture_name},{color_name}"
            self.mapping[mapping_key] = self.current_model_data
            self.current_model_data += 1
    
    def generate_model_files(self):
        """Generate Minecraft model files with CustomModelData overrides"""
        base_model = {
            "parent": "item/generated",
            "textures": {
                "layer0": "the_idea_anvil:item/custom_item"
            },
            "overrides": []
        }
        
        # Add overrides for each texture,color combination
        for mapping_key, model_data in self.mapping.items():
            texture_name, color_name = mapping_key.split(',')
            
            override = {
                "predicate": {
                    "custom_model_data": model_data
                },
                "model": f"the_idea_anvil:item/{texture_name}_{color_name}"
            }
            base_model["overrides"].append(override)
            
            # Create individual model file
            individual_model = {
                "parent": "item/generated",
                "textures": {
                    "layer0": f"the_idea_anvil:item/{texture_name}_{color_name}"
                }
            }
            
            model_file_path = self.models_folder / f"{texture_name}_{color_name}.json"
            with open(model_file_path, 'w') as f:
                json.dump(individual_model, f, indent=2)
        
        # Save main model file
        main_model_path = self.models_folder / "custom_item.json"
        with open(main_model_path, 'w') as f:
            json.dump(base_model, f, indent=2)
    
    def save_mapping(self):
        """Save texture,color -> CustomModelData mapping to JSON"""
        mapping_path = self.output_folder / "texture_mapping.json"
        with open(mapping_path, 'w') as f:
            json.dump(self.mapping, f, indent=2)
        
        print(f"Saved mapping with {len(self.mapping)} entries to {mapping_path}")
    
    def process_all(self):
        """Process all textures in input folder"""
        self.setup_folders()
        
        # Find all PNG files in input folder
        texture_files = list(self.input_folder.glob("*.png"))
        
        if not texture_files:
            print(f"No PNG files found in {self.input_folder}")
            return
        
        print(f"Found {len(texture_files)} texture files to process")
        
        # Process each texture
        for texture_file in texture_files:
            try:
                self.process_texture(texture_file)
            except Exception as e:
                print(f"Error processing {texture_file}: {e}")
        
        # Generate model files and mapping
        self.generate_model_files()
        self.save_mapping()
        
        print(f"\\nProcessing complete!")
        print(f"- Redded textures: {self.redded_folder}")
        print(f"- Colored textures: {self.colored_folder}")
        print(f"- Model files: {self.models_folder}")
        print(f"- Texture mapping: {self.output_folder}/texture_mapping.json")

def main():
    parser = argparse.ArgumentParser(description="Process textures for The Idea Anvil mod")
    parser.add_argument("input_folder", help="Folder containing input PNG textures")
    parser.add_argument("output_folder", help="Output folder for processed files")
    
    args = parser.parse_args()
    
    processor = TextureProcessor(args.input_folder, args.output_folder)
    processor.process_all()

if __name__ == "__main__":
    main()
