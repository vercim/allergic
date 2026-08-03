package dev.vercim.allergic.client;

import dev.vercim.allergic.network.SneezePayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

public final class AllergicClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(SneezePayload.ID, (payload, context) ->
                context.client().execute(SneezeOverlay::trigger));
        HudRenderCallback.EVENT.register(SneezeOverlay::render);
    }
}
