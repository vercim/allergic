package dev.vercim.allergic.effect;

import dev.vercim.allergic.config.AllergyConfig;
import dev.vercim.allergic.registry.ModSounds;
import dev.vercim.allergic.registry.ModStatusEffects;
import dev.vercim.allergic.network.SneezePayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.server.network.ServerPlayerEntity;
import org.joml.Vector3f;

import java.util.Map;
import java.util.WeakHashMap;

public final class AllergyStatusEffect extends StatusEffect {
    private static final int PARTICLE_COLOR = 0xC94F5C;
    private static final int TICKS_PER_SECOND = 20;
    private static final int MAX_INITIAL_NAUSEA_DELAY_SECONDS = 10;
    private static final DustParticleEffect SNEEZE_PARTICLE_BRIGHT =
            new DustParticleEffect(new Vector3f(0.55F, 1.0F, 0.08F), 1.2F);
    private static final DustParticleEffect SNEEZE_PARTICLE_DARK =
            new DustParticleEffect(new Vector3f(0.16F, 0.55F, 0.03F), 0.9F);

    private final Map<LivingEntity, SymptomSchedule> schedules = new WeakHashMap<>();

    public AllergyStatusEffect() {
        super(StatusEffectCategory.HARMFUL, PARTICLE_COLOR);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }

    @Override
    public boolean applyUpdateEffect(LivingEntity entity, int amplifier) {
        if (entity.getWorld().isClient) {
            return true;
        }

        StatusEffectInstance allergy = entity.getStatusEffect(ModStatusEffects.ALLERGY);
        if (allergy != null) {
            applyPersistentEffects(entity, allergy.getDuration());
        }

        long now = entity.getWorld().getTime();
        AllergyConfig config = AllergyConfig.get();
        SymptomSchedule schedule = schedules.get(entity);
        if (schedule == null || now > schedule.lastTick + 1L || now < schedule.lastTick) {
            schedule = new SymptomSchedule(
                    now + randomDelayTicks(entity, config.sneezeMinIntervalSeconds(), config.sneezeMaxIntervalSeconds()),
                    now + initialNauseaDelayTicks(entity, config),
                    now
            );
            schedules.put(entity, schedule);
        }

        if (now >= schedule.nextSneezeTick) {
            sneeze(entity, config, amplifier);
            schedule.nextSneezeTick = now + randomDelayTicks(
                    entity,
                    config.sneezeMinIntervalSeconds(),
                    config.sneezeMaxIntervalSeconds()
            );
        }

        if (now >= schedule.nextNauseaTick) {
            entity.addStatusEffect(visibleSymptomSeconds(StatusEffects.NAUSEA, config.nauseaDurationSeconds()));
            schedule.nextNauseaTick = now + randomDelayTicks(
                    entity,
                    config.nauseaMinIntervalSeconds(),
                    config.nauseaMaxIntervalSeconds()
            );
        }

        schedule.lastTick = now;
        return true;
    }

    private static void applyPersistentEffects(LivingEntity entity, int allergyDurationTicks) {
        entity.addStatusEffect(visibleSymptom(StatusEffects.WEAKNESS, allergyDurationTicks));
        entity.addStatusEffect(visibleSymptom(StatusEffects.SLOWNESS, allergyDurationTicks));
        entity.addStatusEffect(visibleSymptom(StatusEffects.MINING_FATIGUE, allergyDurationTicks));
    }

    public static void triggerSneeze(LivingEntity entity) {
        sneeze(entity, AllergyConfig.get(), 0);
    }

    private static void sneeze(LivingEntity entity, AllergyConfig config, int amplifier) {
        entity.damage(entity.getDamageSources().magic(), config.sneezeDamageAmount() * (amplifier + 1));
        spawnSneezeParticles(entity);
        if (entity instanceof ServerPlayerEntity player) {
            ServerPlayNetworking.send(player, SneezePayload.INSTANCE);
        }
        entity.getWorld().playSound(
                null,
                entity.getX(),
                entity.getY(),
                entity.getZ(),
                ModSounds.SNEEZE,
                SoundCategory.PLAYERS,
                1.0F,
                0.85F + entity.getRandom().nextFloat() * 0.3F
        );
    }

    private static void spawnSneezeParticles(LivingEntity entity) {
        if (!(entity.getWorld() instanceof ServerWorld world)) {
            return;
        }

        double x = entity.getX();
        double y = entity.getEyeY();
        double z = entity.getZ();
        world.spawnParticles(SNEEZE_PARTICLE_BRIGHT, x, y, z, 28, 0.32D, 0.22D, 0.32D, 0.18D);
        world.spawnParticles(SNEEZE_PARTICLE_DARK, x, y, z, 16, 0.26D, 0.18D, 0.26D, 0.13D);
    }

    private static StatusEffectInstance visibleSymptomSeconds(
            net.minecraft.registry.entry.RegistryEntry<StatusEffect> effect,
            int durationSeconds
    ) {
        return visibleSymptom(effect, durationSeconds * TICKS_PER_SECOND);
    }

    private static StatusEffectInstance visibleSymptom(
            net.minecraft.registry.entry.RegistryEntry<StatusEffect> effect,
            int durationTicks
    ) {
        return new StatusEffectInstance(effect, durationTicks, 0, false, false, true);
    }

    private static int randomDelayTicks(LivingEntity entity, int minSeconds, int maxSeconds) {
        int minTicks = minSeconds * TICKS_PER_SECOND;
        int maxTicks = maxSeconds * TICKS_PER_SECOND;
        return entity.getRandom().nextBetween(minTicks, maxTicks);
    }

    private static int initialNauseaDelayTicks(LivingEntity entity, AllergyConfig config) {
        int maxSeconds = Math.min(config.nauseaMaxIntervalSeconds(), MAX_INITIAL_NAUSEA_DELAY_SECONDS);
        maxSeconds = Math.min(maxSeconds, Math.max(1, config.allergyDurationSeconds() / 2));
        int minSeconds = Math.min(config.nauseaMinIntervalSeconds(), maxSeconds);
        return randomDelayTicks(entity, minSeconds, maxSeconds);
    }

    private static final class SymptomSchedule {
        private long nextSneezeTick;
        private long nextNauseaTick;
        private long lastTick;

        private SymptomSchedule(long nextSneezeTick, long nextNauseaTick, long lastTick) {
            this.nextSneezeTick = nextSneezeTick;
            this.nextNauseaTick = nextNauseaTick;
            this.lastTick = lastTick;
        }
    }
}
