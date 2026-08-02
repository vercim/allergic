package dev.vercim.allergic.client;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.vercim.allergic.AllergicMod;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.Identifier;

public final class SneezeOverlay {
    private static final Identifier TEXTURE =
            Identifier.of(AllergicMod.MOD_ID, "textures/gui/sneeze_overlay.png");
    private static final int TEXTURE_WIDTH = 1920;
    private static final int TEXTURE_HEIGHT = 1080;
    private static final long FADE_DURATION_NANOS = 1_000_000_000L;

    private static long startedAtNanos = Long.MIN_VALUE;

    private SneezeOverlay() {
    }

    public static void trigger() {
        startedAtNanos = System.nanoTime();
    }

    public static void render(DrawContext context, RenderTickCounter tickCounter) {
        if (startedAtNanos == Long.MIN_VALUE) {
            return;
        }

        long elapsed = System.nanoTime() - startedAtNanos;
        if (elapsed >= FADE_DURATION_NANOS) {
            startedAtNanos = Long.MIN_VALUE;
            return;
        }

        float alpha = 1.0F - (float) elapsed / FADE_DURATION_NANOS;
        int width = context.getScaledWindowWidth();
        int height = context.getScaledWindowHeight();

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha);
        context.drawTexture(
                TEXTURE,
                0,
                0,
                width,
                height,
                0.0F,
                0.0F,
                TEXTURE_WIDTH,
                TEXTURE_HEIGHT,
                TEXTURE_WIDTH,
                TEXTURE_HEIGHT
        );
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableBlend();
    }
}
