package com.sameeran.flycraft;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = FlyCraftMod.MOD_ID, value = Dist.CLIENT)
public class ClientEvents {

    private static boolean jetMode = false;

    public static boolean isJetMode() {
        return jetMode;
    }

    @SubscribeEvent
    public static void onKeyPress(InputEvent.Key event) {
        if (KeybindHandler.TOGGLE_MODE_KEY.consumeClick()) {
            jetMode = !jetMode;
            System.out.println("[FlyCraft] Jet Mode toggled: " + jetMode);
        }
    }
}
