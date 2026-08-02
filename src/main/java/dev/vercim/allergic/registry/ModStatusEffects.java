package dev.vercim.allergic.registry;

import dev.vercim.allergic.AllergicMod;
import dev.vercim.allergic.effect.AllergyStatusEffect;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

public final class ModStatusEffects {
    public static final RegistryEntry.Reference<StatusEffect> ALLERGY = Registry.registerReference(
            Registries.STATUS_EFFECT,
            Identifier.of(AllergicMod.MOD_ID, "allergy"),
            new AllergyStatusEffect()
    );

    private ModStatusEffects() {
    }

    public static void register() {
        AllergicMod.LOGGER.info("Registered the allergy status effect.");
    }
}
