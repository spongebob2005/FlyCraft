# Custom Animation Examples

This directory contains example animations that can be adapted for your own use.

## To Create a Custom Animation

### Example 1: Simple Meditation Animation

```java
public class MeditationAnimation extends Animation {
    private PlayerModel<?> model;

    public MeditationAnimation(float duration, AbstractClientPlayer player, PlayerModel<?> model) {
        super(duration, true); // Looping
        this.model = model;
    }

    @Override
    protected void onUpdate(float progress) {
        if (model == null) return;
        
        // Float up and down
        float float_effect = (float)Math.sin(progress * Math.PI * 4) * 0.3f;
        
        // Sitting cross-legged
        model.rightLeg.xRot = 2.0f;
        model.leftLeg.xRot = 2.0f;
        
        // Meditation pose (arms)
        model.rightArm.xRot = 2.0f;
        model.leftArm.xRot = 2.0f;
        
        // Lean forward
        model.body.xRot = 0.3f;
        model.body.y = float_effect;
    }
}
```

### Example 2: Victory Pose

```java
public class VictoryPose extends Animation {
    private PlayerModel<?> model;

    public VictoryPose(float duration, AbstractClientPlayer player, PlayerModel<?> model) {
        super(duration, false); // One-time
        this.model = model;
    }

    @Override
    protected void onUpdate(float progress) {
        if (model == null) return;
        
        float eased = easeProgress(EasingFunction.Easing.EASE_IN_OUT);
        
        // Raise both arms
        model.rightArm.xRot = -1.5f * eased;
        model.leftArm.xRot = -1.5f * eased;
        
        // Spread arms
        model.rightArm.zRot = 0.5f * eased;
        model.leftArm.zRot = -0.5f * eased;
        
        // Tilt head back
        model.head.xRot = -0.3f * eased;
    }
}
```

### Example 3: Combat Stance

```java
public class CombatStance extends Animation {
    private PlayerModel<?> model;

    public CombatStance(float duration, AbstractClientPlayer player, PlayerModel<?> model) {
        super(duration, false);
        this.model = model;
    }

    @Override
    protected void onUpdate(float progress) {
        if (model == null) return;
        
        float eased = easeProgress(EasingFunction.Easing.EASE_OUT);
        
        // Forward lean
        model.body.xRot = 0.5f * eased;
        
        // Right arm extended (jab)
        model.rightArm.xRot = -0.8f * eased;
        model.rightArm.zRot = 0.3f * eased;
        
        // Left arm guarding
        model.leftArm.xRot = 1.0f * eased;
        model.leftArm.zRot = -0.2f * eased;
        
        // Combat stance legs
        model.leftLeg.xRot = 0.3f * eased;
        model.head.yRot = 0.3f * eased;
    }
}
```

### Example 4: Swimming Motion

```java
public class SwimmingMotion extends Animation {
    private PlayerModel<?> model;

    public SwimmingMotion(float duration, AbstractClientPlayer player, PlayerModel<?> model) {
        super(duration, true); // Looping
        this.model = model;
    }

    @Override
    protected void onUpdate(float progress) {
        if (model == null) return;
        
        // Alternating strokes
        float stroke1 = (float)Math.sin(progress * Math.PI * 2) * 0.5f;
        float stroke2 = (float)Math.sin(progress * Math.PI * 2 + Math.PI) * 0.5f;
        
        // Swimming arms
        model.rightArm.xRot = -1.2f + stroke1;
        model.rightArm.zRot = 0.3f;
        
        model.leftArm.xRot = -1.2f + stroke2;
        model.leftArm.zRot = -0.3f;
        
        // Forward lean
        model.body.xRot = 0.2f;
        
        // Leg kicks
        float kick = (float)Math.sin(progress * Math.PI * 4) * 0.2f;
        model.rightLeg.xRot = kick;
        model.leftLeg.xRot = -kick;
    }
}
```

### Example 5: Thinking/Confused

```java
public class ThinkingAnimation extends Animation {
    private PlayerModel<?> model;

    public ThinkingAnimation(float duration, AbstractClientPlayer player, PlayerModel<?> model) {
        super(duration, false);
        this.model = model;
    }

    @Override
    protected void onUpdate(float progress) {
        if (model == null) return;
        
        float eased = easeProgress(EasingFunction.Easing.EASE_IN_OUT);
        
        // Scratch head
        model.rightArm.xRot = -2.0f * eased;
        model.rightArm.zRot = 0.5f * eased;
        
        // Head tilt
        model.head.zRot = 0.3f * eased;
        
        // Body shift
        model.body.xRot = 0.2f * eased;
        model.body.zRot = 0.2f * eased;
    }
}
```

### Example 6: Angry/Aggressive

```java
public class AngryAnimation extends Animation {
    private PlayerModel<?> model;

    public AngryAnimation(float duration, AbstractClientPlayer player, PlayerModel<?> model) {
        super(duration, false);
        this.model = model;
    }

    @Override
    protected void onUpdate(float progress) {
        if (model == null) return;
        
        float eased = easeProgress(EasingFunction.Easing.BOUNCE);
        
        // Clenched fists
        model.rightArm.xRot = -1.0f * eased;
        model.leftArm.xRot = -1.0f * eased;
        model.rightArm.zRot = 0.3f * eased;
        model.leftArm.zRot = -0.3f * eased;
        
        // Aggressive stance
        model.body.xRot = 0.5f * eased;
        model.head.xRot = -0.3f * eased;
        model.rightLeg.xRot = -0.2f * eased;
    }
}
```

### Example 7: Tired/Exhausted

```java
public class ExhaustedAnimation extends Animation {
    private PlayerModel<?> model;

    public ExhaustedAnimation(float duration, AbstractClientPlayer player, PlayerModel<?> model) {
        super(duration, true); // Looping (breathing)
        this.model = model;
    }

    @Override
    protected void onUpdate(float progress) {
        if (model == null) return;
        
        // Breathing motion
        float breathe = (float)Math.sin(progress * Math.PI * 2) * 0.1f;
        
        // Slouched
        model.body.xRot = 0.3f;
        
        // Drooping arms
        model.rightArm.xRot = 0.5f + breathe;
        model.leftArm.xRot = 0.5f + breathe;
        
        // Head drop
        model.head.xRot = 0.3f;
        
        // Tired stance
        model.rightLeg.xRot = 0.2f;
        model.leftLeg.xRot = 0.2f;
        
        // Sway
        float sway = (float)Math.sin(progress * Math.PI) * 0.15f;
        model.body.zRot = sway;
    }
}
```

## How to Use These Examples

1. Copy the code for the animation you want
2. Create a new Java file in `com.sameeran.flycraft.animation` package
3. Paste the code and adjust as needed
4. Register in `AnimationController.initializePlayerAnimations()`
5. Add keybind to `AnimationKeybinds.java`
6. Add input handler to `ClientEvents.java`
7. Add localization to `en_us.json`

## Tips

- Use `easeProgress()` with different easing functions for smooth effects
- `Math.sin()` and `Math.cos()` are great for continuous/wave motions
- Always check `if (model == null)` to avoid crashes
- Loop animations should have `duration = true` in constructor
- Single animations should have `duration = false`
- Typical rotation values: 0, ±0.5, ±1.0, ±1.5, ±π

See `ANIMATION_QUICKREF.md` for more details!
