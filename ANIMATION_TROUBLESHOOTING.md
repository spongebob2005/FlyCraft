# FlyCraft Animation System - Troubleshooting Guide

## Problem: Animations Not Showing

If you can't see any animations when pressing the keybinds (V, C, B, X, Z), follow this troubleshooting guide.

---

## ✅ Step-by-Step Troubleshooting

### Step 1: Verify Keybinds Are Registered

1. Launch the game
2. Go to **Settings → Controls → Movement**
3. Look for these keybinds:
   - "Wave Animation" (should be V)
   - "Clap Animation" (should be C)
   - "Dance Animation" (should be B)
   - "Spin Animation" (should be X)
   - "Jump Boost Animation" (should be Z)

**If keybinds are NOT there:**
- The keybind registration failed
- Check `KeybindHandler.java` has correct event bus
- Rebuild and restart the game

**If keybinds ARE there:**
- Move to Step 2

---

### Step 2: Check Console for Messages

When you press an animation keybind, check the console for messages:

```
[FlyCraft] Wave animation playing
[FlyCraft] Animation system initialized for player
```

**If you DON'T see these messages:**
- The keybind press isn't being detected
- Check if keybind conflicts with other mods
- Try changing the key in `AnimationKeybinds.java`
- Move to Step 3

**If you DO see these messages:**
- Animations are registering correctly
- Move to Step 3

---

### Step 3: Check ParticleEffects Are Showing

When you press an animation keybind, look around your player for particle effects:
- **Wave (V)**: Sparkle particles (white/magical looking)
- **Clap (C)**: Burst particles (explosion-like)
- **Dance (B)**: Dust particles (gray cloud)
- **Spin (X)**: Spiral particles (circular pattern)
- **Jump (Z)**: Wave particles (expanding ring)

**If particles ARE showing:**
- Animation system is working!
- Proceed to Step 4 to verify model animation

**If particles are NOT showing:**
- Check particle settings in Options
- Make sure you're not in a POV that hides particles
- Check console for errors

---

### Step 4: Check Player Model Animation

This is the trickiest part because player model animations are subtle. Here's how to check:

1. **Use Third-Person View**
   - Press F5 or your camera toggle key
   - Position camera to see your player from the side

2. **Watch the Player Model**
   - Press V (Wave)
   - Watch your player's **right arm** - it should raise up and wave side-to-side
   - Press C (Clap)
   - Watch your player's **arms** - they should clap together repeatedly
   - Press B (Dance)
   - Watch your player's **body** - should sway and legs should move

**If model IS animating:**
- ✅ Everything is working correctly!

**If model is NOT animating:**
- Proceed to Step 5

---

### Step 5: Enable Debug Mode

Edit `AnimationController.java` and find the `onRenderPlayer` method:

```java
@SubscribeEvent
public static void onRenderPlayer(RenderPlayerEvent.Pre event) {
    // ... existing code ...
    System.out.println("[FlyCraft] Render event - player: " + player.getName().getString());
}
```

Add this line to see if the render event is being called:

Look for console messages like:
```
[FlyCraft] Render event - player: YourPlayerName
[FlyCraft] Animations initialized on render event
```

**If you see these messages:**
- Animation system is initializing
- Issue is with model manipulation

**If you DON'T see these messages:**
- Render event isn't being called
- Check `AnimationController` registration in `@Mod.EventBusSubscriber`

---

## 🔧 Common Issues & Solutions

### Issue 1: Keybinds Don't Appear in Controls Menu

**Cause**: Keybinds not registered in `RegisterKeyMappingsEvent`

**Solution**:
1. Check `KeybindHandler.registerKeys()` is being called
2. Verify `@SubscribeEvent` and `bus = Mod.EventBusSubscriber.Bus.MOD` are set
3. Rebuild: `./gradlew.bat clean build`

---

### Issue 2: Keybinds Work (console shows messages) But No Particles

**Cause**: Particle effects not rendering

**Solution**:
1. Check particle settings: Options → Particles → Set to "All"
2. Try different game modes (Creative/Survival)
3. Check Java Console for particle errors
4. Verify `ParticleEffects.java` is accessing MC correctly

---

### Issue 3: Particles Show But Model Doesn't Animate

**Cause**: PlayerModel not being manipulated correctly

**Solution**:
1. Switch to third-person view (F5)
2. The animation is subtle - watch carefully for arm/body movement
3. Try the clap animation (most visible)
4. If still nothing, check:
   - PlayerModel reference is not null
   - Animation values are being set correctly
   - Model parts exist (model.rightArm, etc.)

---

### Issue 4: Everything Works but Animation Looks Wrong

**Cause**: Rotation values need adjustment

**Solution**:
1. Edit the animation file (e.g., `WaveAnimation.java`)
2. Adjust rotation values:
   - Normal range: -π to π (about -3.14 to 3.14)
   - Typical values: ±0.5 to ±1.5
3. Test incrementally
4. Rebuild and retest

---

## 🔍 Debug Checklist

- [ ] Keybinds visible in Controls menu
- [ ] Console shows animation messages when key pressed
- [ ] Particle effects appear around player
- [ ] Switch to third-person view
- [ ] Watch player model for subtle movements
- [ ] Try clap animation (most visible)
- [ ] Check for console errors

---

## 📋 What to Check in Each File

### ClientEvents.java
```
✓ Animation keybind checks
✓ AnimationController.playAnimation() called
✓ ParticleEffects triggered
✓ Console output shows animation name
```

### KeybindHandler.java
```
✓ Keybinds registered in registerKeys()
✓ Bus type is Mod.EventBusSubscriber.Bus.MOD
✓ All animation keybinds included
```

### AnimationController.java
```
✓ initializePlayerAnimations() called
✓ onRenderPlayer() event is subscribed
✓ Animations registered in AnimationManager
```

### AnimationManager.java
```
✓ playAnimation() sets isPlaying = true
✓ updateAnimations() called each tick
✓ Animation progress advances correctly
```

### Animation (specific animation files)
```
✓ onUpdate() modifies PlayerModel
✓ Rotation values are reasonable
✓ Progress parameter used (0.0 to 1.0)
```

---

## 📊 Console Output Examples

### Everything Working Correctly
```
[FlyCraft] Jet Mode toggled: true
[FlyCraft] Animation system initialized for player
[FlyCraft] Render event - player: YourName
[FlyCraft] Wave animation playing
```

### Keybind Issue
```
(No console output when pressing keybind)
```

### Render Event Issue
```
[FlyCraft] Wave animation playing
(But no render event message)
```

### Animation Registration Issue
```
[FlyCraft] Wave animation playing
[FlyCraft] Render event - player: YourName
[FlyCraft] Animations initialized on render event
(But animation doesn't play)
```

---

## 🎯 Testing Strategy

1. **Test Keybind**: Press key → See console message
2. **Test Particles**: Verify particle effects appear
3. **Test Model**: Switch to F5 third-person → Watch model
4. **Test Animation**: Press different keys and watch
5. **Debug**: Add console messages to pinpoint issue

---

## 💡 Tips

- **Third-person view** (F5) makes animations much easier to see
- **Clap animation** (C) is the most visible
- **Particles** appear even if model animation fails
- **Console messages** help identify which step fails
- **Rebuild** (`./gradlew.bat clean build`) after any changes

---

## 🚀 If Nothing Works

1. Rebuild the entire project: `./gradlew.bat clean build`
2. Delete run cache: `rm -r build/` then rebuild
3. Restart the IDE
4. Restart the game
5. Check all imports in files
6. Verify no compilation errors: `get_errors`

---

## 📞 Need Help?

If you're still having issues:
1. Share console output
2. Confirm which step fails
3. Check if this is a fresh install or previously worked
4. Try a simple test: just make particles show (step 3)

Good luck! 🎬✨
