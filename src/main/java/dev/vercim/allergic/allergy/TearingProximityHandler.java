package dev.vercim.allergic.allergy;

import dev.vercim.allergic.config.AllergyConfig;
import dev.vercim.allergic.registry.ModStatusEffects;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.block.BlockState;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

public final class TearingProximityHandler {
    private static final int CHECK_INTERVAL_TICKS = 5;

    private TearingProximityHandler() {
    }

    public static void registerEvents() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                if (player.age % CHECK_INTERVAL_TICKS == 0) {
                    updateEffect(player);
                }
            }
        });
    }

    private static void updateEffect(ServerPlayerEntity player) {
        boolean isNearTrigger = hasTriggerBlockNearby(
                player,
                AllergyConfig.get().tearingTriggerRadius()
        );

        if (isNearTrigger) {
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

    private static boolean hasTriggerBlockNearby(ServerPlayerEntity player, int radius) {
        BlockPos center = player.getBlockPos();
        double radiusSquared = (double) radius * radius;

        for (BlockPos pos : BlockPos.iterateOutwards(center, radius, radius, radius)) {
            if (pos.getSquaredDistance(center) > radiusSquared) {
                continue;
            }

            BlockState state = player.getWorld().getBlockState(pos);
            if (state.isIn(BlockTags.FLOWERS)
                    || state.isIn(BlockTags.LOGS)
                    || state.isIn(BlockTags.LEAVES)) {
                return true;
            }
        }

        return false;
    }
}
