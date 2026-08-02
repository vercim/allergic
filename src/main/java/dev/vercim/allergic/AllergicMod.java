package dev.vercim.allergic;

import dev.vercim.allergic.allergy.AllergyManager;
import dev.vercim.allergic.command.AllergyCommands;
import dev.vercim.allergic.config.AllergyConfig;
import dev.vercim.allergic.registry.ModStatusEffects;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class AllergicMod implements ModInitializer {
    public static final String MOD_ID = "allergic";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        AllergyConfig.load();
        ModStatusEffects.register();
        AllergyManager.registerEvents();
        AllergyCommands.register();
        LOGGER.info("Allergic initialized successfully.");
    }
}
