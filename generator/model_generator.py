#!/usr/bin/env python3
"""
Model Generator for The Idea Anvil Mod

This script generates Minecraft model files with CustomModelData overrides
based on the texture mapping created by texture_processor.py

Usage:
    python model_generator.py mapping_file output_models_folder
"""

import json
import argparse
from pathlib import Path

class ModelGenerator:
    def __init__(self, mapping_file, output_folder):
        self.mapping_file = Path(mapping_file)
        self.output_folder = Path(output_folder)
        self.models_item_folder = self.output_folder / "assets" / "the_idea_anvil" / "models" / "item"
        
    def load_mapping(self):
        """Load texture mapping from JSON file"""
        with open(self.mapping_file, 'r') as f:
            return json.load(f)
    
    def generate_base_model(self, mapping):
        """Generate the main custom_item.json with all overrides"""
        base_model = {
            "parent": "item/generated",
            "textures": {
                "layer0": "the_idea_anvil:item/custom_item"
            },
            "overrides": []
        }
        
        # Sort by CustomModelData value for consistency
        sorted_mapping = sorted(mapping.items(), key=lambda x: x[1])
        
        for mapping_key, model_data in sorted_mapping:
            texture_name, color_name = mapping_key.split(',')
            
            override = {
                "predicate": {
                    "custom_model_data": model_data
                },
                "model": f"the_idea_anvil:item/{texture_name}_{color_name}"
            }
            base_model["overrides"].append(override)
        
        return base_model
    
    def generate_individual_models(self, mapping):
        """Generate individual model files for each texture variant"""
        models = {}
        
        for mapping_key, model_data in mapping.items():
            texture_name, color_name = mapping_key.split(',')
            model_name = f"{texture_name}_{color_name}"
            
            individual_model = {
                "parent": "item/generated",
                "textures": {
                    "layer0": f"the_idea_anvil:item/{texture_name}_{color_name}"
                }
            }
            
            models[model_name] = individual_model
        
        return models
    
    def save_models(self, base_model, individual_models):
        """Save all model files to output folder"""
        # Create directory structure
        self.models_item_folder.mkdir(parents=True, exist_ok=True)
        
        # Save base model
        base_model_path = self.models_item_folder / "custom_item.json"
        with open(base_model_path, 'w') as f:
            json.dump(base_model, f, indent=2)
        
        print(f"Saved base model: {base_model_path}")
        
        # Save individual models
        for model_name, model_data in individual_models.items():
            model_path = self.models_item_folder / f"{model_name}.json"
            with open(model_path, 'w') as f:
                json.dump(model_data, f, indent=2)
        
        print(f"Saved {len(individual_models)} individual models")
    
    def generate_all(self):
        """Generate all model files"""
        print(f"Loading mapping from {self.mapping_file}")
        mapping = self.load_mapping()
        
        print(f"Generating models for {len(mapping)} texture variants")
        
        # Generate models
        base_model = self.generate_base_model(mapping)
        individual_models = self.generate_individual_models(mapping)
        
        # Save models
        self.save_models(base_model, individual_models)
        
        print(f"\nModel generation complete!")
        print(f"Output folder: {self.models_item_folder}")
        print(f"Base model overrides: {len(base_model['overrides'])}")

def main():
    parser = argparse.ArgumentParser(description="Generate Minecraft model files")
    parser.add_argument("mapping_file", help="JSON file with texture mapping")
    parser.add_argument("output_folder", help="Output folder for model files")
    
    args = parser.parse_args()
    
    generator = ModelGenerator(args.mapping_file, args.output_folder)
    generator.generate_all()

if __name__ == "__main__":
    main()
