package com.sameeran.flycraft;

import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(FlyCraftMod.MOD_ID)
public class FlyCraftMod {
    public static final String MOD_ID = "flycraft";
    public static final Logger LOGGER  = LogManager.getLogger(MOD_ID);

    public FlyCraftMod() {
        // Mod initialization happens automatically through @EventBusSubscriber
    }
}
