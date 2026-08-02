package dev.vercim.allergic.effect;

import dev.vercim.allergic.AllergicMod;
import dev.vercim.allergic.config.AllergyConfig;
import dev.vercim.allergic.registry.ModSounds;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.Identifier;

import java.util.Map;
import java.util.WeakHashMap;

public final class AllergyStatusEffect extends StatusEffect {
    private static final int PARTICLE_COLOR = 0xC94F5C;
    private static final int TICKS_PER_SECOND = 20;
    private static final double WEAKNESS_ATTACK_DAMAGE = -4.0D;

    private final Map<LivingEntity, SymptomSchedule> schedules = new WeakHashMap<>();

    public AllergyStatusEffect() {
        super(StatusEffectCategory.HARMFUL, PARTICLE_COLOR);
        addAttributeModifier(
                EntityAttributes.GENERIC_ATTACK_DAMAGE,
                Identifier.of(AllergicMod.MOD_ID, "effect.allergy.weakness"),
                WEAKNESS_ATTACK_DAMAGE,
                EntityAttributeModifier.Operation.ADD_VALUE
        );
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

        long now = entity.getWorld().getTime();
        AllergyConfig config = AllergyConfig.get();
        SymptomSchedule schedule = schedules.get(entity);
        if (schedule == null || now > schedule.lastTick + 1L || now < schedule.lastTick) {
            schedule = new SymptomSchedule(
                    now + randomDelayTicks(entity, config.sneezeMinIntervalSeconds(), config.sneezeMaxIntervalSeconds()),
                    now + randomDelayTicks(entity, config.nauseaMinIntervalSeconds(), config.nauseaMaxIntervalSeconds()),
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
            entity.addStatusEffect(visibleSymptom(StatusEffects.NAUSEA, config.nauseaDurationSeconds()));
            schedule.nextNauseaTick = now + randomDelayTicks(
                    entity,
                    config.nauseaMinIntervalSeconds(),
                    config.nauseaMaxIntervalSeconds()
            );
        }

        schedule.lastTick = now;
        return true;
    }

    private static void sneeze(LivingEntity entity, AllergyConfig config, int amplifier) {
        entity.damage(entity.getDamageSources().magic(), config.sneezeDamageAmount() * (amplifier + 1));
        entity.addStatusEffect(visibleSymptom(StatusEffects.DARKNESS, config.darknessDurationSeconds()));
        entity.playSound(ModSounds.SNEEZE, 1.0F, 0.95F + entity.getRandom().nextFloat() * 0.1F);
    }

    private static StatusEffectInstance visibleSymptom(
            net.minecraft.registry.entry.RegistryEntry<StatusEffect> effect,
            int durationSeconds
    ) {
        return new StatusEffectInstance(effect, durationSeconds * TICKS_PER_SECOND, 0, false, true, true);
    }

    private static int randomDelayTicks(LivingEntity entity, int minSeconds, int maxSeconds) {
        int minTicks = minSeconds * TICKS_PER_SECOND;
        int maxTicks = maxSeconds * TICKS_PER_SECOND;
        return entity.getRandom().nextBetween(minTicks, maxTicks);
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
