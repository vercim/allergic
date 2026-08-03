package dev.vercim.allergic.registry;

import dev.vercim.allergic.AllergicMod;
import dev.vercim.allergic.effect.SneezingStatusEffect;
import dev.vercim.allergic.effect.TearingStatusEffect;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

public final class ModStatusEffects {
    public static final RegistryEntry.Reference<StatusEffect> SNEEZING = Registry.registerReference(
            Registries.STATUS_EFFECT,
            Identifier.of(AllergicMod.MOD_ID, "sneezing"),
            new SneezingStatusEffect()
    );
    public static final RegistryEntry.Reference<StatusEffect> TEARING = Registry.registerReference(
            Registries.STATUS_EFFECT,
            Identifier.of(AllergicMod.MOD_ID, "tearing"),
            new TearingStatusEffect()
    );

    private ModStatusEffects() {
    }

    public static void register() {
        AllergicMod.LOGGER.info("Registered Allergic status effects.");
    }
}
