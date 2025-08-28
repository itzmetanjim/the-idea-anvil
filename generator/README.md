# Texture Processing System for The Idea Anvil

This folder contains Python scripts to process textures and generate model files for the custom texture system.

## Requirements

```bash
pip install Pillow numpy
```

## Scripts

### 1. texture_processor.py
Main processing script that:
- Takes input textures (swords, bows, etc.)
- HSV shifts them so average color is #ff0000 (red)
- Creates hue-shifted versions in 8 common colors
- Generates texture mapping JSON file

**Usage:**
```bash
python texture_processor.py input_textures/ output/
```

### 2. model_generator.py
Generates Minecraft model files with CustomModelData overrides based on texture mapping.

**Usage:**
```bash
python model_generator.py output/texture_mapping.json minecraft_assets/
```

## Workflow

1. Place your source textures (PNG files) in an input folder
2. Run `texture_processor.py` to generate colored variants and mapping
3. Run `model_generator.py` to create Minecraft model files
4. Copy generated textures and models to your mod's assets folder

## Output Structure

```
output/
├── redded/              # Textures shifted to red base
├── colored/             # Final colored texture variants  
├── textures/            # Ready-to-use texture files
├── models/              # Generated model JSON files
└── texture_mapping.json # Mapping of texture,color -> CustomModelData
```

## Color Variants

Each input texture is processed into 8 color variants:
- Red (base)
- Orange  
- Yellow
- Green
- Cyan
- Blue
- Purple
- Magenta

## Integration

After running the scripts:
1. Copy textures from `output/textures/` to `src/main/resources/assets/the_idea_anvil/textures/item/`
2. Copy models from `output/models/` to `src/main/resources/assets/the_idea_anvil/models/item/`
3. Use the mapping JSON to determine CustomModelData values in your code
