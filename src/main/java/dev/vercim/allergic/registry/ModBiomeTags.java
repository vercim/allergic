package dev.vercim.allergic.registry;

import dev.vercim.allergic.AllergicMod;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;

public final class ModBiomeTags {
    public static final TagKey<Biome> FLOWERING = TagKey.of(
            RegistryKeys.BIOME,
            Identifier.of(AllergicMod.MOD_ID, "flowering")
    );

    private ModBiomeTags() {
    }
}
