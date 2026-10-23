package com.sameeran.flycraft;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.function.Supplier;

/**
 * Tells the server when a player toggles jet mode, so it can cancel fall
 * damage for them. The channel is optional on both sides: the mod still
 * works client-only on servers without FlyCraft (just without that help).
 */
public class FlyCraftNetwork {

    private static final String PROTOCOL = "1";

    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
        new ResourceLocation(FlyCraftMod.MOD_ID, "main"),
        () -> PROTOCOL,
        NetworkRegistry.acceptMissingOr(PROTOCOL),
        NetworkRegistry.acceptMissingOr(PROTOCOL));

    public static void register() {
        CHANNEL.messageBuilder(JetModePacket.class, 0, NetworkDirection.PLAY_TO_SERVER)
            .encoder(JetModePacket::encode)
            .decoder(JetModePacket::decode)
            .consumerMainThread(JetModePacket::handle)
            .add();
    }

    public record JetModePacket(boolean enabled) {
        static void encode(JetModePacket msg, FriendlyByteBuf buf) { buf.writeBoolean(msg.enabled); }
        static JetModePacket decode(FriendlyByteBuf buf)           { return new JetModePacket(buf.readBoolean()); }

        static void handle(JetModePacket msg, Supplier<NetworkEvent.Context> ctx) {
            ServerPlayer sender = ctx.get().getSender();
            if (sender == null) return;
            FlightModeManager.setMode(sender.getUUID(),
                msg.enabled ? FlightModeManager.FlightMode.JET : FlightModeManager.FlightMode.NORMAL);
        }
    }
}
