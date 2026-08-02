package dev.vercim.allergic.network;

import dev.vercim.allergic.AllergicMod;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record SneezePayload() implements CustomPayload {
    public static final SneezePayload INSTANCE = new SneezePayload();
    public static final CustomPayload.Id<SneezePayload> ID =
            new CustomPayload.Id<>(Identifier.of(AllergicMod.MOD_ID, "sneeze"));
    public static final PacketCodec<RegistryByteBuf, SneezePayload> CODEC = PacketCodec.unit(INSTANCE);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
