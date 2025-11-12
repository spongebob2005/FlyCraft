# Jet Mode Overlay Textures

This directory contains texture assets for jet mode visual effects.

## Files

- `jet_vignette.png` - Dark vignette effect around screen edges (512x512 recommended)
- `speed_lines.png` - Horizontal motion lines for speed visualization (512x64 recommended)
- `g_force_blur.png` - Motion blur effect for intense maneuvers (512x512 recommended)

## Creating Custom Textures

You can replace these textures with your own designs. For best results:

1. **jet_vignette.png**: Grayscale image with dark corners fading to transparent center
   - Dimensions: 512x512 pixels
   - Format: PNG with transparency

2. **speed_lines.png**: Horizontal striped pattern, transparent background
   - Dimensions: 512x64 pixels
   - Format: PNG with transparency

3. **g_force_blur.png**: Radial gradient blur, white/light colors on transparent background
   - Dimensions: 512x512 pixels
   - Format: PNG with transparency

The mod uses procedural rendering by default if textures are not found, so these are optional.
