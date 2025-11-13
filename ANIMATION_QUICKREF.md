# FlyCraft Animation System - Quick Reference

## Animation Keybinds

| Key | Animation | Duration | Type | Effect |
|-----|-----------|----------|------|--------|
| **V** | Wave | 1.0s | One-time | Sparkle particles |
| **C** | Clap | 1.5s | One-time | Burst particles |
| **B** | Dance | 2.0s | Looping | Dust particles |
| **X** | Spin | 1.0s | One-time | Spiral particles |
| **Z** | Jump Boost | 0.4s | One-time | Wave particles |
| **R** | Toggle Jet Mode | - | Toggle | HUD indicator |

## Animation Package Structure

```
src/main/java/com/sameeran/flycraft/animation/
├── Animation.java                 # Abstract base class
├── EasingFunction.java           # Easing functions
├── AnimationManager.java         # Animation registry
├── AnimationController.java      # Event handler
├── AnimationKeybinds.java        # Keybind definitions
├── ParticleEffects.java          # Particle system
├── FlyingAnimation.java          # Arms raised
├── SpinAnimation.java            # Body rotation
├── GlideAnimation.java           # Gliding pose
├── JumpBoostAnimation.java       # Crouch & spring
├── WaveAnimation.java            # Wave gesture
├── ClapAnimation.java            # Clapping hands
└── DanceAnimation.java           # Full body dance
```

## Creating a Custom Animation

### Step 1: Create Animation Class
```java
public class MyAnimation extends Animation {
    private PlayerModel<?> model;
    
    public MyAnimation(float duration, AbstractClientPlayer player, PlayerModel<?> model) {
        super(duration, false); // false = one-time, true = looping
        this.model = model;
    }
    
    @Override
    protected void onUpdate(float progress) {
        // progress goes from 0.0 to 1.0
        float eased = easeProgress(EasingFunction.Easing.EASE_IN_OUT);
        
        // Modify model parts
        model.rightArm.xRot = eased * 1.5f;
        model.body.zRot = (float)Math.sin(progress * Math.PI) * 0.3f;
    }
}
```

### Step 2: Register Animation
In `AnimationController.initializePlayerAnimations()`:
```java
AnimationManager.registerAnimation("myanimation", 
    new MyAnimation(1.0f, player, model));
```

### Step 3: Add Keybind
In `AnimationKeybinds.java`:
```java
public static final KeyMapping MY_ANIMATION_KEY = new KeyMapping(
    "key.flycraft.myanimation",
    GLFW.GLFW_KEY_N,
    "key.categories.movement"
);
```

### Step 4: Handle Input
In `ClientEvents.onKeyPress()`:
```java
if (AnimationKeybinds.MY_ANIMATION_KEY.consumeClick()) {
    AnimationController.playAnimation("myanimation");
    ParticleEffects.sparkleEffect(player, 10);
}
```

### Step 5: Add Localization
In `en_us.json`:
```json
"key.flycraft.myanimation": "My Animation"
```

## Easing Functions Reference

- **LINEAR**: Constant speed
- **EASE_IN**: Slow start, accelerates
- **EASE_OUT**: Fast start, decelerates
- **EASE_IN_OUT**: Slow at both ends, fast middle
- **ELASTIC**: Bouncy spring effect
- **BOUNCE**: Multiple bounce effect

## Particle Effects Reference

### burstEffect(LocalPlayer player, int count)
Particles burst outward in all directions

### spiralEffect(LocalPlayer player, int count)
Particles form a spiral ascending upward

### trailEffect(LocalPlayer player)
Single particles falling behind player

### dustEffect(LocalPlayer player, int count)
Random dust cloud around player

### sparkleEffect(LocalPlayer player, int count)
Magical sparkle particles in sphere

### waveEffect(LocalPlayer player)
Expanding ring of particles

## PlayerModel Animation Parts

```
Head          body          Legs
├─ head       ├─ body       ├─ rightLeg
├─ rightEar   ├─ rightArm   └─ leftLeg
└─ leftEar    └─ leftArm
```

## Rotation Properties

- `.xRot` - Pitch (up/down rotation around X-axis)
- `.yRot` - Yaw (left/right rotation around Y-axis)  
- `.zRot` - Roll (clockwise/counter-clockwise around Z-axis)
- `.x`, `.y`, `.z` - Position offsets

## Typical Rotation Values

| Rotation | Radians | Degrees | Usage |
|----------|---------|---------|-------|
| 0 | 0 | 0° | Neutral |
| ±0.5 | ±0.5 rad | ±28.6° | Minor gesture |
| ±1.0 | ±1.0 rad | ±57.3° | Normal gesture |
| ±1.5 | ±1.5 rad | ±85.9° | Major gesture |
| ±π | ±3.14 rad | ±180° | Full reverse |

## Common Animation Patterns

### Waving
```java
model.rightArm.xRot = -1.5f; // Raise arm
model.rightArm.zRot = (float)Math.sin(progress * Math.PI * 3) * 0.5f; // Wave side to side
```

### Jumping
```java
model.body.y = -eased * 2; // Crouch
model.rightLeg.y = eased * 2; // Extend legs
```

### Dancing
```java
float sway = (float)Math.sin(progress * Math.PI * 4) * 0.3f;
model.body.zRot = sway; // Sway body
model.rightLeg.xRot = sway * 0.5f; // Move legs
```

### Spinning
```java
float rotation = 360 * eased;
player.yBodyRotO = rotation; // Full rotation
```

## API Methods

### AnimationController
- `playAnimation(String name)` - Start animation
- `queueAnimation(String name)` - Queue for later
- `stopAnimation(String name)` - Stop specific animation
- `stopAllAnimations()` - Stop all animations
- `isAnimationPlaying(String name)` - Check status

### AnimationManager
- `registerAnimation(String name, Animation anim)` - Register animation
- `updateAnimations(float deltaTime)` - Update all (called automatically)
- `getAnimation(String name)` - Get animation object
- `clearAnimations()` - Remove all animations

## Performance Tips

- Keep animations under 2 seconds
- Use sparkling/fade particles for smooth performance
- Limit particle count to 20-30 per animation
- Clean up animations after completion (automatic)
- Avoid complex model manipulations (keep rotation-based)

## Troubleshooting

**Animation not playing:**
- Check keybind registration in `KeybindHandler`
- Verify animation name spelling matches in `ClientEvents`
- Check console for error messages

**Particles not showing:**
- Verify player is in CLIENT mode
- Check `ParticleEffects` has MC instance
- Ensure particle type exists in Minecraft

**Animation janky/stuttering:**
- Use easing functions for smooth transitions
- Check update frequency (should be ~50ms)
- Reduce particle count

**Keybind conflicts:**
- Change GLFW key in `AnimationKeybinds`
- Available keys: GLFW_KEY_A through GLFW_KEY_Z
- Check Minecraft key conflict warnings
