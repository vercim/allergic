package dev.vercim.allergic.effect;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

public final class TearingStatusEffect extends StatusEffect {
    private static final int PARTICLE_COLOR = 0xE88CB5;

    public TearingStatusEffect() {
        super(StatusEffectCategory.HARMFUL, PARTICLE_COLOR);
    }
}
