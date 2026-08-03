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
        int tearingBlurStrength,
        int tearingTriggerRadius,
        boolean sneezingEnabled,
        boolean sneezeDamageEnabled,
        int sneezeMinIntervalSeconds,
        int sneezeMaxIntervalSeconds,
        float sneezeDamageAmount,
        boolean nauseaEnabled,
        int nauseaMinIntervalSeconds,
        int nauseaMaxIntervalSeconds,
        int nauseaDurationSeconds,
        boolean weaknessEnabled,
        boolean slownessEnabled,
        boolean miningFatigueEnabled
) {
    public static final int MAX_SECONDS = Integer.MAX_VALUE / 20;
    public static final double DEFAULT_ALLERGY_CHANCE = 0.35D;
    public static final int DEFAULT_ALLERGY_DURATION_SECONDS = 300;
    public static final int MIN_TEARING_BLUR_STRENGTH = 0;
    public static final int MAX_TEARING_BLUR_STRENGTH = 10;
    public static final int DEFAULT_TEARING_BLUR_STRENGTH = 5;
    public static final int MIN_TEARING_TRIGGER_RADIUS = 1;
    public static final int MAX_TEARING_TRIGGER_RADIUS = 16;
    public static final int DEFAULT_TEARING_TRIGGER_RADIUS = 3;
    public static final boolean DEFAULT_SNEEZING_ENABLED = true;
    public static final boolean DEFAULT_SNEEZE_DAMAGE_ENABLED = true;
    public static final int DEFAULT_SNEEZE_MIN_INTERVAL_SECONDS = 10;
    public static final int DEFAULT_SNEEZE_MAX_INTERVAL_SECONDS = 30;
    public static final float DEFAULT_SNEEZE_DAMAGE_AMOUNT = 1.0F;
    public static final int DEFAULT_NAUSEA_MIN_INTERVAL_SECONDS = 20;
    public static final int DEFAULT_NAUSEA_MAX_INTERVAL_SECONDS = 60;
    public static final int DEFAULT_NAUSEA_DURATION_SECONDS = 10;
    public static final boolean DEFAULT_NAUSEA_ENABLED = true;
    public static final boolean DEFAULT_WEAKNESS_ENABLED = true;
    public static final boolean DEFAULT_SLOWNESS_ENABLED = true;
    public static final boolean DEFAULT_MINING_FATIGUE_ENABLED = true;

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
                DEFAULT_TEARING_BLUR_STRENGTH,
                DEFAULT_TEARING_TRIGGER_RADIUS,
                DEFAULT_SNEEZING_ENABLED,
                DEFAULT_SNEEZE_DAMAGE_ENABLED,
                DEFAULT_SNEEZE_MIN_INTERVAL_SECONDS,
                DEFAULT_SNEEZE_MAX_INTERVAL_SECONDS,
                DEFAULT_SNEEZE_DAMAGE_AMOUNT,
                DEFAULT_NAUSEA_ENABLED,
                DEFAULT_NAUSEA_MIN_INTERVAL_SECONDS,
                DEFAULT_NAUSEA_MAX_INTERVAL_SECONDS,
                DEFAULT_NAUSEA_DURATION_SECONDS,
                DEFAULT_WEAKNESS_ENABLED,
                DEFAULT_SLOWNESS_ENABLED,
                DEFAULT_MINING_FATIGUE_ENABLED
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
        int tearingBlurStrength = readIntegerInRange(
                object,
                "tearingBlurStrength",
                DEFAULT_TEARING_BLUR_STRENGTH,
                MIN_TEARING_BLUR_STRENGTH,
                MAX_TEARING_BLUR_STRENGTH
        );
        int tearingTriggerRadius = readIntegerInRange(
                object,
                "tearingTriggerRadius",
                DEFAULT_TEARING_TRIGGER_RADIUS,
                MIN_TEARING_TRIGGER_RADIUS,
                MAX_TEARING_TRIGGER_RADIUS
        );
        boolean sneezingEnabled = readBoolean(object, "sneezingEnabled", DEFAULT_SNEEZING_ENABLED);
        boolean sneezeDamageEnabled = readBoolean(object, "sneezeDamageEnabled", DEFAULT_SNEEZE_DAMAGE_ENABLED);
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

        boolean nauseaEnabled = readBoolean(object, "nauseaEnabled", DEFAULT_NAUSEA_ENABLED);
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
        boolean weaknessEnabled = readBoolean(object, "weaknessEnabled", DEFAULT_WEAKNESS_ENABLED);
        boolean slownessEnabled = readBoolean(object, "slownessEnabled", DEFAULT_SLOWNESS_ENABLED);
        boolean miningFatigueEnabled = readBoolean(object, "miningFatigueEnabled", DEFAULT_MINING_FATIGUE_ENABLED);

        return new AllergyConfig(
                chance,
                duration,
                tearingBlurStrength,
                tearingTriggerRadius,
                sneezingEnabled,
                sneezeDamageEnabled,
                sneezeMinInterval,
                sneezeMaxInterval,
                amount,
                nauseaEnabled,
                nauseaMinInterval,
                nauseaMaxInterval,
                nauseaDuration,
                weaknessEnabled,
                slownessEnabled,
                miningFatigueEnabled
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

    private static int readIntegerInRange(JsonObject object, String key, int fallback, int minimum, int maximum) {
        double value = readFiniteDouble(object, key, fallback);
        if (value < minimum || value > maximum || value != Math.rint(value)) {
            return fallback;
        }
        return (int) value;
    }

    private static boolean readBoolean(JsonObject object, String key, boolean fallback) {
        try {
            if (!object.has(key)
                    || !object.get(key).isJsonPrimitive()
                    || !object.getAsJsonPrimitive(key).isBoolean()) {
                return fallback;
            }
            return object.get(key).getAsBoolean();
        } catch (RuntimeException exception) {
            return fallback;
        }
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
