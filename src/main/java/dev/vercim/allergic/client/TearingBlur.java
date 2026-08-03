package dev.vercim.allergic.client;

import dev.vercim.allergic.registry.ModStatusEffects;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;

public final class TearingBlur {
    private static final float BASE_STRENGTH = 2.5F;
    private static final float STRENGTH_VARIATION = 0.75F;
    private static final float FADE_DURATION_SECONDS = 1.5F;
    private static final double OSCILLATION_SPEED = 1.35D;
    private static final float MAX_FRAME_TIME_SECONDS = 0.1F;

    private static float fadeAmount;
    private static long lastFrameNanos = Long.MIN_VALUE;

    private TearingBlur() {
    }

    public static void render(WorldRenderContext context) {
        MinecraftClient client = MinecraftClient.getInstance();
        long now = System.nanoTime();
        boolean shouldBlur = client.player != null
                && client.currentScreen == null
                && client.player.hasStatusEffect(ModStatusEffects.TEARING);
        updateFade(shouldBlur, now);

        if (client.currentScreen != null || fadeAmount <= 0.0F) {
            return;
        }

        double seconds = now / 1_000_000_000.0D;
        float oscillatingStrength = BASE_STRENGTH
                + STRENGTH_VARIATION * (float) Math.sin(seconds * OSCILLATION_SPEED);
        float strength = fadeAmount * oscillatingStrength;
        client.gameRenderer.renderBlur(strength);
    }

    private static void updateFade(boolean shouldBlur, long now) {
        if (lastFrameNanos == Long.MIN_VALUE) {
            lastFrameNanos = now;
        }

        float elapsedSeconds = Math.min(
                (now - lastFrameNanos) / 1_000_000_000.0F,
                MAX_FRAME_TIME_SECONDS
        );
        lastFrameNanos = now;

        float fadeStep = elapsedSeconds / FADE_DURATION_SECONDS;
        if (shouldBlur) {
            fadeAmount = Math.min(1.0F, fadeAmount + fadeStep);
        } else {
            fadeAmount = Math.max(0.0F, fadeAmount - fadeStep);
        }
    }
}
