# Jet Mode Overlay System Implementation Guide

## Overview

I've implemented a dynamic jet mode texture overlay system similar to the **Actions and Stuff** mod. The system provides visual feedback during high-speed elytra flight with procedurally-generated effects that scale with flight speed.

## What Was Added

### 1. **JetModeOverlayRenderer** class
   - Location: `src/main/java/com/sameeran/flycraft/JetModeOverlayRenderer.java`
   - Automatically renders overlay effects when a player is in jet mode and flying with an elytra
   - Provides three visual effect layers:

#### a) **Jet Vignette Effect**
   - Dark semi-transparent overlay around screen edges
   - Opacity increases with flight speed (80-200 alpha)
   - Creates a "focused intensity" feel during high-speed flight
   - Color: Dark gray (#1a1a1a)

#### b) **Speed Lines Effect**
   - Horizontal cyan-colored lines that move across the screen
   - Becomes visible only at 30% of maximum speed
   - Line opacity and speed scale with current velocity
   - Creates a motion/acceleration visual
   - Color: Cyan (#00ccff)

#### c) **G-Force Blur Effect**
   - Radial motion blur in the center of the screen
   - Appears only at 50%+ of maximum speed
   - Simulates the effect of intense G-forces during maneuvers
   - Color: White with dynamic transparency

### 2. **Texture Asset Directory**
   - Location: `src/main/resources/assets/flycraft/textures/overlay/`
   - Reserved for future custom texture implementations
   - Currently uses procedural rendering (no external textures required)

## How It Works

### Event Subscription
The overlay renderer is registered with Forge's event bus using:
```java
@Mod.EventBusSubscriber(modid = FlyCraftMod.MOD_ID, value = Dist.CLIENT)
```

This means it automatically listens for `RenderGuiEvent.Post` events on the client side.

### Render Flow
1. When the player is in jet mode AND flying with elytra
2. Calculate current speed as a fraction of max speed (0.0 to 1.0)
3. Render three overlay layers with opacity based on speed:
   - **Vignette**: Always visible, opacity scales with speed
   - **Speed Lines**: Appears at 30%+ speed, moves faster at higher speeds
   - **G-Force Blur**: Appears at 50%+ speed, intensity scales with speed

### Integration with Existing Code
- Works seamlessly with `ClientEvents.isJetMode()` to check jet mode status
- Reads flight speed from `player.getDeltaMovement().length()`
- Uses the same max speed constant as `FlightController` (2.8 m/s)
- Integrates with existing `RenderGuiEvent.Post` rendering pipeline

## Customization Options

### 1. Adjust Visual Intensity
Edit these constants in `JetModeOverlayRenderer`:

```java
// In renderJetVignette():
int alpha = (int) (80 + speedFraction * 120); // Increase numbers for stronger effect

// In renderSpeedLines():
int lineAlpha = (int) (50 * speedFraction); // Adjust line brightness

// In renderGForceBlur():
int blurAlpha = (int) (30 * (speedFraction - 0.5f) * 2); // Adjust blur intensity
```

### 2. Change Colors
Modify the color hex values:

```java
// Vignette (currently dark gray)
int vignetteColor = (alpha << 24) | 0x1a1a1a;

// Speed lines (currently cyan)
int lineColor = (lineAlpha << 24) | 0x00ccff;

// G-Force blur (currently white)
int color = (alpha << 24) | 0xffffff;
```

### 3. Add Custom Textures
To replace procedural rendering with texture files:

1. Create PNG images:
   - `jet_vignette.png` (512x512, dark gradient)
   - `speed_lines.png` (512x64, horizontal stripes)
   - `g_force_blur.png` (512x512, radial gradient blur)

2. Place in: `src/main/resources/assets/flycraft/textures/overlay/`

3. Uncomment the ResourceLocation fields in `JetModeOverlayRenderer`

4. Modify render methods to use TextureManager instead of fill()

### 4. Adjust Speed Thresholds
Control when effects become visible:

```java
// In renderSpeedLines():
if (speedFraction < 0.3f) return;  // Show lines at 30% speed

// In renderGForceBlur():
if (speedFraction < 0.5f) return;  // Show blur at 50% speed
```

## Testing the Overlay

1. **Build the mod:**
   ```bash
   ./gradlew build
   ```

2. **Run the client:**
   ```bash
   ./gradlew runClient
   ```

3. **In-game:**
   - Equip an elytra
   - Enable jet mode (default key not yet configured)
   - Jump and fly using elytra
   - Observe the overlay effects intensifying as you accelerate
   - Effects will show:
     - Vignette immediately when flying in jet mode
     - Speed lines when moving at moderate speeds
     - G-Force blur at high speeds

## Integration with Key Binding

Once you set up a key binding for jet mode toggle in `KeyBindingRegistry`, the overlay will automatically activate during jet-mode flight without any additional configuration needed.

## Performance Considerations

- **Procedural rendering**: Very lightweight (only uses Screen coordinate fills)
- **No texture loading**: No additional VRAM overhead
- **Conditional rendering**: Effects only render during jet mode flight
- **Tick-based animation**: Smooth, predictable performance impact

## File Structure
```
FlyCraft/
├── src/main/java/com/sameeran/flycraft/
│   └── JetModeOverlayRenderer.java (NEW)
└── src/main/resources/assets/flycraft/textures/overlay/
    └── README.md (NEW)
```

## Future Enhancements

Possible improvements you could add:
- Dynamic particle effects alongside overlays
- Sound effects tied to speed levels
- Screen shake/camera wobble at extreme speeds
- HUD warnings when approaching speed limits
- Customizable overlay settings in a config file
- Different overlay styles for different flight modes

---

**Enjoy your enhanced jet mode flying experience!** 🛩️✈️
