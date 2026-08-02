package dev.vercim.allergic.integration;

import dev.vercim.allergic.config.AllergyConfig;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

final class AllergyClothConfigScreen {
    private AllergyClothConfigScreen() {
    }

    static Screen create(Screen parent) {
        AllergyConfig saved = AllergyConfig.get();
        Draft draft = new Draft(saved);
        AllergyConfig defaults = AllergyConfig.defaults();

        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Text.translatable("text.allergic.config.title"));
        ConfigCategory settings = builder.getOrCreateCategory(Text.empty());
        ConfigEntryBuilder entries = builder.entryBuilder();

        settings.addEntry(entries.startDoubleField(Text.translatable("option.allergic.allergyChance"), saved.allergyChance())
                .setDefaultValue(defaults.allergyChance()).setMin(0.0D).setMax(1.0D)
                .setTooltip(Text.translatable("option.allergic.allergyChance.tooltip"))
                .setSaveConsumer(value -> draft.allergyChance = value).build());
        settings.addEntry(entries.startIntField(Text.translatable("option.allergic.allergyDurationSeconds"), saved.allergyDurationSeconds())
                .setDefaultValue(defaults.allergyDurationSeconds()).setMin(1).setMax(AllergyConfig.MAX_SECONDS)
                .setTooltip(Text.translatable("option.allergic.allergyDurationSeconds.tooltip"))
                .setSaveConsumer(value -> draft.allergyDurationSeconds = value).build());
        settings.addEntry(entries.startBooleanToggle(Text.translatable("option.allergic.sneezingEnabled"), saved.sneezingEnabled())
                .setDefaultValue(defaults.sneezingEnabled())
                .setTooltip(Text.translatable("option.allergic.sneezingEnabled.tooltip"))
                .setSaveConsumer(value -> draft.sneezingEnabled = value).build());
        settings.addEntry(entries.startBooleanToggle(Text.translatable("option.allergic.sneezeDamageEnabled"), saved.sneezeDamageEnabled())
                .setDefaultValue(defaults.sneezeDamageEnabled())
                .setTooltip(Text.translatable("option.allergic.sneezeDamageEnabled.tooltip"))
                .setSaveConsumer(value -> draft.sneezeDamageEnabled = value).build());
        settings.addEntry(entries.startIntField(Text.translatable("option.allergic.sneezeMinIntervalSeconds"), saved.sneezeMinIntervalSeconds())
                .setDefaultValue(defaults.sneezeMinIntervalSeconds()).setMin(1).setMax(AllergyConfig.MAX_SECONDS)
                .setTooltip(Text.translatable("option.allergic.sneezeMinIntervalSeconds.tooltip"))
                .setSaveConsumer(value -> draft.sneezeMinIntervalSeconds = value).build());
        settings.addEntry(entries.startIntField(Text.translatable("option.allergic.sneezeMaxIntervalSeconds"), saved.sneezeMaxIntervalSeconds())
                .setDefaultValue(defaults.sneezeMaxIntervalSeconds()).setMin(1).setMax(AllergyConfig.MAX_SECONDS)
                .setTooltip(Text.translatable("option.allergic.sneezeMaxIntervalSeconds.tooltip"))
                .setSaveConsumer(value -> draft.sneezeMaxIntervalSeconds = value).build());
        settings.addEntry(entries.startFloatField(Text.translatable("option.allergic.sneezeDamageAmount"), saved.sneezeDamageAmount())
                .setDefaultValue(defaults.sneezeDamageAmount()).setMin(0.01F).setMax(Float.MAX_VALUE)
                .setTooltip(Text.translatable("option.allergic.sneezeDamageAmount.tooltip"))
                .setSaveConsumer(value -> draft.sneezeDamageAmount = value).build());
        settings.addEntry(entries.startBooleanToggle(Text.translatable("option.allergic.nauseaEnabled"), saved.nauseaEnabled())
                .setDefaultValue(defaults.nauseaEnabled())
                .setTooltip(Text.translatable("option.allergic.nauseaEnabled.tooltip"))
                .setSaveConsumer(value -> draft.nauseaEnabled = value).build());
        settings.addEntry(entries.startIntField(Text.translatable("option.allergic.nauseaMinIntervalSeconds"), saved.nauseaMinIntervalSeconds())
                .setDefaultValue(defaults.nauseaMinIntervalSeconds()).setMin(1).setMax(AllergyConfig.MAX_SECONDS)
                .setTooltip(Text.translatable("option.allergic.nauseaMinIntervalSeconds.tooltip"))
                .setSaveConsumer(value -> draft.nauseaMinIntervalSeconds = value).build());
        settings.addEntry(entries.startIntField(Text.translatable("option.allergic.nauseaMaxIntervalSeconds"), saved.nauseaMaxIntervalSeconds())
                .setDefaultValue(defaults.nauseaMaxIntervalSeconds()).setMin(1).setMax(AllergyConfig.MAX_SECONDS)
                .setTooltip(Text.translatable("option.allergic.nauseaMaxIntervalSeconds.tooltip"))
                .setSaveConsumer(value -> draft.nauseaMaxIntervalSeconds = value).build());
        settings.addEntry(entries.startIntField(Text.translatable("option.allergic.nauseaDurationSeconds"), saved.nauseaDurationSeconds())
                .setDefaultValue(defaults.nauseaDurationSeconds()).setMin(1).setMax(AllergyConfig.MAX_SECONDS)
                .setTooltip(Text.translatable("option.allergic.nauseaDurationSeconds.tooltip"))
                .setSaveConsumer(value -> draft.nauseaDurationSeconds = value).build());
        settings.addEntry(entries.startBooleanToggle(Text.translatable("option.allergic.weaknessEnabled"), saved.weaknessEnabled())
                .setDefaultValue(defaults.weaknessEnabled())
                .setTooltip(Text.translatable("option.allergic.weaknessEnabled.tooltip"))
                .setSaveConsumer(value -> draft.weaknessEnabled = value).build());
        settings.addEntry(entries.startBooleanToggle(Text.translatable("option.allergic.slownessEnabled"), saved.slownessEnabled())
                .setDefaultValue(defaults.slownessEnabled())
                .setTooltip(Text.translatable("option.allergic.slownessEnabled.tooltip"))
                .setSaveConsumer(value -> draft.slownessEnabled = value).build());
        settings.addEntry(entries.startBooleanToggle(Text.translatable("option.allergic.miningFatigueEnabled"), saved.miningFatigueEnabled())
                .setDefaultValue(defaults.miningFatigueEnabled())
                .setTooltip(Text.translatable("option.allergic.miningFatigueEnabled.tooltip"))
                .setSaveConsumer(value -> draft.miningFatigueEnabled = value).build());

        builder.setSavingRunnable(() -> AllergyConfig.set(draft.toConfig()));
        return builder.build();
    }

    private static final class Draft {
        private double allergyChance;
        private int allergyDurationSeconds;
        private boolean sneezingEnabled;
        private boolean sneezeDamageEnabled;
        private int sneezeMinIntervalSeconds;
        private int sneezeMaxIntervalSeconds;
        private float sneezeDamageAmount;
        private boolean nauseaEnabled;
        private int nauseaMinIntervalSeconds;
        private int nauseaMaxIntervalSeconds;
        private int nauseaDurationSeconds;
        private boolean weaknessEnabled;
        private boolean slownessEnabled;
        private boolean miningFatigueEnabled;

        private Draft(AllergyConfig config) {
            allergyChance = config.allergyChance();
            allergyDurationSeconds = config.allergyDurationSeconds();
            sneezingEnabled = config.sneezingEnabled();
            sneezeDamageEnabled = config.sneezeDamageEnabled();
            sneezeMinIntervalSeconds = config.sneezeMinIntervalSeconds();
            sneezeMaxIntervalSeconds = config.sneezeMaxIntervalSeconds();
            sneezeDamageAmount = config.sneezeDamageAmount();
            nauseaEnabled = config.nauseaEnabled();
            nauseaMinIntervalSeconds = config.nauseaMinIntervalSeconds();
            nauseaMaxIntervalSeconds = config.nauseaMaxIntervalSeconds();
            nauseaDurationSeconds = config.nauseaDurationSeconds();
            weaknessEnabled = config.weaknessEnabled();
            slownessEnabled = config.slownessEnabled();
            miningFatigueEnabled = config.miningFatigueEnabled();
        }

        private AllergyConfig toConfig() {
            return new AllergyConfig(
                    allergyChance,
                    allergyDurationSeconds,
                    sneezingEnabled,
                    sneezeDamageEnabled,
                    sneezeMinIntervalSeconds,
                    sneezeMaxIntervalSeconds,
                    sneezeDamageAmount,
                    nauseaEnabled,
                    nauseaMinIntervalSeconds,
                    nauseaMaxIntervalSeconds,
                    nauseaDurationSeconds,
                    weaknessEnabled,
                    slownessEnabled,
                    miningFatigueEnabled
            );
        }
    }
}
