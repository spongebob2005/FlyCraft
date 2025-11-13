# FlyCraft Animations System

This document describes the animation system added to FlyCraft mod, inspired by the "Actions and Stuff" mod for Minecraft.

## Features

### Animation Types

1. **Wave Animation** (Key: V)
   - Raises right arm and waves it side to side
   - Duration: 1 second
   - Effect: Sparkle particles
   - Perfect for greeting other players

2. **Clap Animation** (Key: C)
   - Hands clap together repeatedly
   - Duration: 1.5 seconds
   - Effect: Burst of particles
   - Great for celebrating or applauding

3. **Dance Animation** (Key: B)
   - Full body dancing motion with sway and leg movement
   - Duration: 2 seconds (looping)
   - Effect: Dust cloud particles
   - Most expressive animation

4. **Spin Animation** (Key: X)
   - Rapid spinning motion
   - Duration: 1 second
   - Effect: Spiral particle effect
   - Perfect for combat victory or celebration

5. **Jump Boost Animation** (Key: Z)
   - Crouch down then spring up with arm movement
   - Duration: 0.4 seconds
   - Effect: Wave expansion particles
   - Combines with jump effects

### Toggle Jet Mode (Key: R)
- Switch between Jet Mode and Normal Flight Mode
- Displayed in HUD

## Animation System Architecture

### Core Components

#### `Animation.java`
- Base abstract class for all animations
- Handles timing, progression, and easing
- Supports looping and one-time animations

#### `EasingFunction.java`
- Functional interface for smooth transitions
- Pre-built easing functions:
  - LINEAR: Constant speed
  - EASE_IN: Slow start, fast end
  - EASE_OUT: Fast start, slow end
  - EASE_IN_OUT: Slow at both ends
  - ELASTIC: Bouncy effect
  - BOUNCE: Multiple bounce effect

#### `AnimationManager.java`
- Centralized manager for all active animations
- Features:
  - Register and manage animations
  - Queue animations for sequential playback
  - Update all animations each tick

#### `ParticleEffects.java`
- Visual effects for animations
- Available effects:
  - `burstEffect()`: Particles exploding outward
  - `spiralEffect()`: Helical particle pattern
  - `trailEffect()`: Particle trail behind player
  - `dustEffect()`: Dust cloud
  - `sparkleEffect()`: Sparkling particles
  - `waveEffect()`: Expanding wave

#### `AnimationController.java`
- Event handler for animation rendering and updates
- Manages animation lifecycle
- Public methods for playing/stopping animations

#### `AnimationKeybinds.java`
- Defines all animation keybinds
- Easy to customize key mappings

### Event Handling

The system uses Minecraft Forge's event bus:
- `RegisterKeyMappingsEvent`: Register animation keybinds
- `InputEvent.Key`: Handle keypress input
- `TickEvent.PlayerTickEvent`: Update animations
- `RenderPlayerEvent.Pre`: Apply animation states

## Customization Guide

### Adding a New Animation

1. Create a new class extending `Animation`:
```java
public class MyAnimation extends Animation {
    private PlayerModel<?> model;
    
    public MyAnimation(float duration, AbstractClientPlayer player, PlayerModel<?> model) {
        super(duration, false); // false for one-time, true for looping
        this.model = model;
    }
    
    @Override
    protected void onUpdate(float progress) {
        // 0.0f to 1.0f progress
        float eased = easeProgress(EasingFunction.Easing.EASE_IN_OUT);
        // Modify model parts
        model.rightArm.xRot = eased * (float)Math.PI;
    }
}
```

2. Register in `AnimationManager.registerAnimation()` or create keybind

3. Add keybind to `AnimationKeybinds.java`

### Modifying Animation Properties

- **Duration**: Adjust `duration` parameter when creating animation
- **Easing**: Change `easeProgress(EasingFunction.Easing.EASE_OUT)` to different easing
- **Looping**: Set `loop = true` in constructor for continuous animation

### Adding Custom Particle Effects

Add method to `ParticleEffects.java`:
```java
public static void myEffect(LocalPlayer player) {
    Vec3 pos = player.position();
    MC.particleEngine.createParticle(
        ParticleTypes.FLAME,
        pos.x, pos.y, pos.z,
        vx, vy, vz
    );
}
```

## Model Parts Reference

Available PlayerModel parts to animate:
- `model.head` - Head
- `model.body` - Torso
- `model.rightArm` - Right arm
- `model.leftArm` - Left arm
- `model.rightLeg` - Right leg
- `model.leftLeg` - Left leg

Common rotation properties:
- `.xRot` - Rotation around X axis (pitch)
- `.yRot` - Rotation around Y axis (yaw)
- `.zRot` - Rotation around Z axis (roll)
- `.x`, `.y`, `.z` - Position offsets

## Keybind Overview

| Key | Action | Category |
|-----|--------|----------|
| R | Toggle Jet/Normal Mode | Movement |
| V | Wave | Movement |
| C | Clap | Movement |
| B | Dance | Movement |
| X | Spin | Movement |
| Z | Jump Boost | Movement |

All animation keybinds can be customized in:
`Settings → Controls → Movement`

## Performance Considerations

- Animations update every tick (50ms)
- Particle effects are moderate to keep performance good
- All animations properly clean up after completion
- Looping animations can be stopped with `AnimationController.stopAnimation()`

## Future Enhancement Ideas

- Audio effects for animations (woosh, clap sound, etc.)
- More complex multi-part animations
- Animation chaining/combos
- Custom animation configuration files
- Animation speed modifiers
- Player-specific animation packages
