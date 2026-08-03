package dev.vercim.allergic.client;

import dev.vercim.allergic.config.AllergyConfig;
import dev.vercim.allergic.mixin.GameRendererAccessor;
import dev.vercim.allergic.registry.ModStatusEffects;
import net.minecraft.client.gl.PostEffectProcessor;
import net.minecraft.client.MinecraftClient;

public final class TearingBlur {
    private TearingBlur() {
    }

    public static void render() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || !client.player.hasStatusEffect(ModStatusEffects.TEARING)) {
            return;
        }

        int strength = AllergyConfig.get().tearingBlurStrength();
        PostEffectProcessor blur = ((GameRendererAccessor) client.gameRenderer).allergic$getBlurPostProcessor();
        if (strength <= 0 || blur == null) {
            return;
        }

        blur.setUniforms("Radius", strength);
        blur.render(0.0F);
    }
}
