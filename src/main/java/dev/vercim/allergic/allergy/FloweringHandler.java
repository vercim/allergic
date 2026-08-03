package dev.vercim.allergic.allergy;

import dev.vercim.allergic.registry.ModBiomeTags;
import dev.vercim.allergic.registry.ModStatusEffects;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.biome.Biome;

public final class FloweringHandler {
    private static final float MIN_FLOWERING_TEMPERATURE = 0.5F;

    private FloweringHandler() {
    }

    public static void registerEvents() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                updateEffect(player);
            }
        });
    }

    private static void updateEffect(ServerPlayerEntity player) {
        RegistryEntry<Biome> biome = player.getWorld().getBiome(player.getBlockPos());
        boolean isFlowering = biome.isIn(ModBiomeTags.FLOWERING)
                && biome.value().getTemperature() >= MIN_FLOWERING_TEMPERATURE;

        if (isFlowering) {
            if (!player.hasStatusEffect(ModStatusEffects.TEARING)) {
                player.addStatusEffect(new StatusEffectInstance(
                        ModStatusEffects.TEARING,
                        StatusEffectInstance.INFINITE,
                        0,
                        false,
                        false,
                        true
                ));
            }
        } else if (player.hasStatusEffect(ModStatusEffects.TEARING)) {
            player.removeStatusEffect(ModStatusEffects.TEARING);
        }
    }
}
