package dev.vercim.allergic.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.vercim.allergic.AllergicMod;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public record AllergyConfig(
        double allergyChance,
        int allergyDurationSeconds,
        int sneezeMinIntervalSeconds,
        int sneezeMaxIntervalSeconds,
        float sneezeDamageAmount,
        int darknessDurationSeconds,
        int nauseaMinIntervalSeconds,
        int nauseaMaxIntervalSeconds,
        int nauseaDurationSeconds
) {
    public static final int MAX_SECONDS = Integer.MAX_VALUE / 20;
    public static final double DEFAULT_ALLERGY_CHANCE = 0.3D;
    public static final int DEFAULT_ALLERGY_DURATION_SECONDS = 300;
    public static final int DEFAULT_SNEEZE_MIN_INTERVAL_SECONDS = 5;
    public static final int DEFAULT_SNEEZE_MAX_INTERVAL_SECONDS = 10;
    public static final float DEFAULT_SNEEZE_DAMAGE_AMOUNT = 1.0F;
    public static final int DEFAULT_DARKNESS_DURATION_SECONDS = 1;
    public static final int DEFAULT_NAUSEA_MIN_INTERVAL_SECONDS = 20;
    public static final int DEFAULT_NAUSEA_MAX_INTERVAL_SECONDS = 40;
    public static final int DEFAULT_NAUSEA_DURATION_SECONDS = 5;

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("allergic.json");
    private static volatile AllergyConfig current = defaults();

    public static AllergyConfig get() {
        return current;
    }

    public static void load() {
        AllergyConfig loaded = defaults();

        if (Files.exists(CONFIG_PATH)) {
            try (Reader reader = Files.newBufferedReader(CONFIG_PATH)) {
                JsonElement root = JsonParser.parseReader(reader);
                if (root.isJsonObject()) {
                    loaded = fromJson(root.getAsJsonObject());
                } else {
                    AllergicMod.LOGGER.warn("The allergy configuration root is not a JSON object; defaults will be used.");
                }
            } catch (Exception exception) {
                AllergicMod.LOGGER.warn("Could not read the allergy configuration; defaults will be used.", exception);
            }
        }

        current = loaded;
        save(loaded);
    }

    public static AllergyConfig defaults() {
        return new AllergyConfig(
                DEFAULT_ALLERGY_CHANCE,
                DEFAULT_ALLERGY_DURATION_SECONDS,
                DEFAULT_SNEEZE_MIN_INTERVAL_SECONDS,
                DEFAULT_SNEEZE_MAX_INTERVAL_SECONDS,
                DEFAULT_SNEEZE_DAMAGE_AMOUNT,
                DEFAULT_DARKNESS_DURATION_SECONDS,
                DEFAULT_NAUSEA_MIN_INTERVAL_SECONDS,
                DEFAULT_NAUSEA_MAX_INTERVAL_SECONDS,
                DEFAULT_NAUSEA_DURATION_SECONDS
        );
    }

    /** Applies a validated configuration and persists it immediately. */
    public static void set(AllergyConfig config) {
        AllergyConfig validated = fromJson(GSON.toJsonTree(config).getAsJsonObject());
        current = validated;
        save(validated);
    }

    private static AllergyConfig fromJson(JsonObject object) {
        boolean legacyConfig = object.has("damageIntervalTicks") || object.has("damageAmount");
        double chance = readFiniteDouble(object, "allergyChance", DEFAULT_ALLERGY_CHANCE);
        if (chance < 0.0D || chance > 1.0D) {
            chance = DEFAULT_ALLERGY_CHANCE;
        }

        int duration = readPositiveInteger(
                object,
                "allergyDurationSeconds",
                DEFAULT_ALLERGY_DURATION_SECONDS,
                MAX_SECONDS
        );
        if (legacyConfig && duration == 20) {
            duration = DEFAULT_ALLERGY_DURATION_SECONDS;
        }
        int sneezeMinInterval = readPositiveInteger(
                object,
                "sneezeMinIntervalSeconds",
                DEFAULT_SNEEZE_MIN_INTERVAL_SECONDS,
                MAX_SECONDS
        );
        int sneezeMaxInterval = readPositiveInteger(
                object,
                "sneezeMaxIntervalSeconds",
                DEFAULT_SNEEZE_MAX_INTERVAL_SECONDS,
                MAX_SECONDS
        );
        if (sneezeMinInterval > sneezeMaxInterval) {
            sneezeMinInterval = DEFAULT_SNEEZE_MIN_INTERVAL_SECONDS;
            sneezeMaxInterval = DEFAULT_SNEEZE_MAX_INTERVAL_SECONDS;
        }

        double legacyDamageAmount = readFiniteDouble(object, "damageAmount", DEFAULT_SNEEZE_DAMAGE_AMOUNT);
        double amountValue = readFiniteDouble(object, "sneezeDamageAmount", legacyDamageAmount);
        float amount = amountValue > 0.0D && amountValue <= Float.MAX_VALUE
                ? (float) amountValue
                : DEFAULT_SNEEZE_DAMAGE_AMOUNT;

        int darknessDuration = readPositiveInteger(
                object,
                "darknessDurationSeconds",
                DEFAULT_DARKNESS_DURATION_SECONDS,
                MAX_SECONDS
        );
        int nauseaMinInterval = readPositiveInteger(
                object,
                "nauseaMinIntervalSeconds",
                DEFAULT_NAUSEA_MIN_INTERVAL_SECONDS,
                MAX_SECONDS
        );
        int nauseaMaxInterval = readPositiveInteger(
                object,
                "nauseaMaxIntervalSeconds",
                DEFAULT_NAUSEA_MAX_INTERVAL_SECONDS,
                MAX_SECONDS
        );
        if (nauseaMinInterval > nauseaMaxInterval) {
            nauseaMinInterval = DEFAULT_NAUSEA_MIN_INTERVAL_SECONDS;
            nauseaMaxInterval = DEFAULT_NAUSEA_MAX_INTERVAL_SECONDS;
        }
        int nauseaDuration = readPositiveInteger(
                object,
                "nauseaDurationSeconds",
                DEFAULT_NAUSEA_DURATION_SECONDS,
                MAX_SECONDS
        );

        return new AllergyConfig(
                chance,
                duration,
                sneezeMinInterval,
                sneezeMaxInterval,
                amount,
                darknessDuration,
                nauseaMinInterval,
                nauseaMaxInterval,
                nauseaDuration
        );
    }

    private static double readFiniteDouble(JsonObject object, String key, double fallback) {
        try {
            if (!object.has(key)
                    || !object.get(key).isJsonPrimitive()
                    || !object.getAsJsonPrimitive(key).isNumber()) {
                return fallback;
            }
            double value = object.get(key).getAsDouble();
            return Double.isFinite(value) ? value : fallback;
        } catch (RuntimeException exception) {
            return fallback;
        }
    }

    private static int readPositiveInteger(JsonObject object, String key, int fallback, int maximum) {
        double value = readFiniteDouble(object, key, fallback);
        if (value <= 0.0D || value > maximum || value != Math.rint(value)) {
            return fallback;
        }
        return (int) value;
    }

    private static void save(AllergyConfig config) {
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            try (Writer writer = Files.newBufferedWriter(CONFIG_PATH)) {
                GSON.toJson(config, writer);
            }
        } catch (IOException exception) {
            AllergicMod.LOGGER.error("Could not write the allergy configuration to {}.", CONFIG_PATH, exception);
        }
    }
}
