# Animation System Diagnostic Checklist

Use this checklist to systematically verify the animation system is working.

## Pre-Build Checks

- [ ] All 13 animation Java files exist in `src/main/java/com/sameeran/flycraft/animation/`
- [ ] `ClientEvents.java` has animation keybind handling code
- [ ] `KeybindHandler.java` imports `AnimationKeybinds`
- [ ] `en_us.json` has animation keybind strings
- [ ] No compilation errors: `./gradlew.bat clean build`

## Build & Run

- [ ] Build successful: `./gradlew.bat clean build`
- [ ] Gradle reports no errors
- [ ] Run game: `./gradlew.bat runClient`
- [ ] Game starts without crashes

## In-Game Checks (In This Order)

### 1. Check Keybinds (CRITICAL)

- [ ] Open Settings → Controls → Movement
- [ ] Search for "wave" or "animation"
- [ ] See these keybinds?
  - [ ] Wave Animation (V)
  - [ ] Clap Animation (C)
  - [ ] Dance Animation (B)
  - [ ] Spin Animation (X)
  - [ ] Jump Boost Animation (Z)

**Result**: ✅ Pass / ❌ Fail

If FAIL: 
- Check `KeybindHandler.registerKeys()` method
- Verify event bus is MOD bus
- Rebuild and restart

---

### 2. Check Keybind Activation (CRITICAL)

Open Java Console (View → Developer Console or check launcher log)

- [ ] Press V in-game
- [ ] Look for console message: `[FlyCraft] Wave animation playing`
- [ ] Repeat for C, B, X, Z keys
- [ ] All should show similar messages

**Result**: ✅ Pass / ❌ Fail

If FAIL:
- Keybinds aren't being detected
- Check for keybind conflicts
- Try changing keys in `AnimationKeybinds.java`

---

### 3. Check Particle Effects (VISUAL)

- [ ] Press V (Wave)
- [ ] Look for sparkly/magical particles around your character
- [ ] Press C (Clap)  
- [ ] Look for burst-like particles around your character
- [ ] Press B (Dance)
- [ ] Look for dust cloud around your character
- [ ] Press X (Spin)
- [ ] Look for spiral particles
- [ ] Press Z (Jump)
- [ ] Look for wave/ring particles

**Result**: ✅ Pass / ❌ Fail

If FAIL:
- Check Options → Particles → Set to "All"
- Try from different distances
- Check Java Console for particle errors

---

### 4. Check Model Animation (MOST IMPORTANT)

- [ ] Press F5 to switch to third-person view
- [ ] Position camera so you can see your player's arms/body clearly
- [ ] Press V (Wave)
- [ ] Watch right arm - should raise and wave side-to-side
- [ ] Press C (Clap)
- [ ] Watch arms - should clap together
- [ ] Press B (Dance)
- [ ] Watch body - should sway, legs should move
- [ ] Press X (Spin)
- [ ] Watch body - should rotate
- [ ] Press Z (Jump)
- [ ] Watch legs - should extend/retract

**Result**: ✅ Pass / ❌ Fail

If FAIL (but particles work):
- Model animation is missing
- Check `AnimationController.onRenderPlayer()` initialization
- Verify PlayerModel reference is obtained correctly

---

### 5. Check Console Initialization Messages

Keep Java Console open while playing:

Look for these messages:
- [ ] `[FlyCraft] Animations initialized on render event` (appears once when game starts)
- [ ] `[FlyCraft] Clap animation playing` (appears each time you press C)
- [ ] `[FlyCraft] Animation system initialized for player` (may appear from tick event)

**Result**: ✅ Pass / ❌ Fail

If FAIL:
- Initialization events not firing
- Check event subscriptions
- Check `@SubscribeEvent` annotations

---

## Detailed Diagnostic Procedure

### If Keybinds Don't Appear (Test 1 Failed)

1. Open `KeybindHandler.java`
2. Verify it has this annotation:
   ```java
   @Mod.EventBusSubscriber(modid = FlyCraftMod.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
   ```
3. Verify method is:
   ```java
   @SubscribeEvent
   public static void registerKeys(RegisterKeyMappingsEvent event)
   ```
4. Verify it calls `event.register()` for all keybinds
5. Rebuild and test again

### If Keybinds Don't Activate (Test 2 Failed)

1. Check if other keybinds work (like R for toggle)
2. Try rebinding animation keys to different keys
3. Check `ClientEvents.onKeyPress()` has proper null checks
4. Add debug console.log to see if method is called
5. Verify `AnimationKeybinds.WAVE_KEY.consumeClick()` logic

### If No Particles (Test 3 Failed)

1. Check if particles are enabled: Options → Particles
2. Try getting closer to character
3. Check if other mods' particles work
4. Verify `Minecraft.getInstance().particleEngine` is not null
5. Check `ParticleEffects.java` - verify particle types exist

### If No Model Animation (Test 4 Failed) 

**This is most common - animations are subtle!**

1. Make absolutely sure you're in third-person (F5)
2. Position camera low and to the side (easier to see arms)
3. Try clap animation - easiest to see
4. Watch VERY carefully for subtle arm movements
5. If still nothing:
   - Check `AnimationController.initializePlayerAnimations()` is called
   - Verify PlayerModel reference is not null
   - Check animation values are reasonable (±0.5 to ±1.5)

---

## Success Indicators

### ✅ Fully Working
- Keybinds appear in controls
- Console shows animation messages
- Particles appear around player
- Player model moves when animating
- Model returns to normal after animation

### ⚠️ Partially Working  
- Keybinds appear but don't work
- Particles work but model doesn't animate
- Animations work for some keys but not others
- Animations stutter or look wrong

### ❌ Not Working
- Keybinds don't appear at all
- Nothing happens when pressing keys
- No console messages appear

---

## Quick Test Commands

1. **Rebuild**: `./gradlew.bat clean build`
2. **Run**: `./gradlew.bat runClient`
3. **Check errors**: `./gradlew.bat build --info` (shows detailed output)

---

## Logs to Check

1. **Game Output**: Look in launcher console
2. **Forge Logs**: Check `logs/latest.log` in .minecraft folder
3. **IDE Console**: Check Eclipse/IntelliJ console output
4. **Crash Reports**: Check `crash-reports/` folder if game crashes

---

## If You're Stuck

Provide this information:
1. [ ] Which tests pass? (1, 2, 3, 4, 5)
2. [ ] Console messages shown? (list them)
3. [ ] Compilation errors? (yes/no, what are they?)
4. [ ] Minecraft version: ________
5. [ ] Forge version: ________

---

**Status**: ☐ All Pass (✅ Working!) / ☐ Some Fail / ☐ All Fail

**Date Tested**: __________

**Notes**: ___________________________________________________________________

---

**Next Steps**: Refer to `ANIMATION_TROUBLESHOOTING.md` for solutions
