package dev.vercim.allergic.registry;

import dev.vercim.allergic.AllergicMod;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public final class ModSounds {
    private static final Identifier SNEEZE_ID = Identifier.of(AllergicMod.MOD_ID, "sneeze");

    public static final SoundEvent SNEEZE = Registry.register(
            Registries.SOUND_EVENT,
            SNEEZE_ID,
            SoundEvent.of(SNEEZE_ID)
    );

    private ModSounds() {
    }

    public static void register() {
        AllergicMod.LOGGER.info("Registered Allergic sounds.");
    }
}
