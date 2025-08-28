#!/usr/bin/env python3
"""
Requirements installer for texture processing scripts
"""

import subprocess
import sys

def install_requirements():
    """Install required Python packages"""
    requirements = [
        "Pillow>=9.0.0",
        "numpy>=1.20.0"
    ]
    
    print("Installing required packages...")
    
    for package in requirements:
        try:
            print(f"Installing {package}...")
            subprocess.check_call([sys.executable, "-m", "pip", "install", package])
            print(f"✓ {package} installed successfully")
        except subprocess.CalledProcessError as e:
            print(f"✗ Failed to install {package}: {e}")
            return False
    
    print("\\nAll requirements installed successfully!")
    return True

if __name__ == "__main__":
    install_requirements()
