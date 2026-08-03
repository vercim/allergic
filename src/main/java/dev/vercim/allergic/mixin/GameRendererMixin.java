package dev.vercim.allergic.mixin;

import dev.vercim.allergic.client.TearingBlur;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
    @Redirect(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/hud/InGameHud;render(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/client/render/RenderTickCounter;)V"
            )
    )
    private void allergic$renderHudAfterTearingBlur(
            InGameHud hud,
            DrawContext context,
            RenderTickCounter tickCounter
    ) {
        context.draw();
        TearingBlur.render();
        hud.render(context, tickCounter);
    }
}
