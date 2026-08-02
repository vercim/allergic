package dev.vercim.allergic.integration;

import dev.vercim.allergic.config.AllergyConfig;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

final class AllergyClothConfigScreen {
    private static final String CATEGORY = "text.allergic.config.category.general";

    private AllergyClothConfigScreen() {
    }

    static Screen create(Screen parent) {
        AllergyConfig saved = AllergyConfig.get();
        Draft draft = new Draft(saved);
        AllergyConfig defaults = AllergyConfig.defaults();

        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Text.translatable("text.allergic.config.title"));
        ConfigCategory category = builder.getOrCreateCategory(Text.translatable(CATEGORY));
        ConfigEntryBuilder entries = builder.entryBuilder();

        category.addEntry(entries.startDoubleField(Text.translatable("option.allergic.allergyChance"), saved.allergyChance())
                .setDefaultValue(defaults.allergyChance()).setMin(0.0D).setMax(1.0D)
                .setTooltip(Text.translatable("option.allergic.allergyChance.tooltip"))
                .setSaveConsumer(value -> draft.allergyChance = value).build());
        category.addEntry(entries.startIntField(Text.translatable("option.allergic.allergyDurationSeconds"), saved.allergyDurationSeconds())
                .setDefaultValue(defaults.allergyDurationSeconds()).setMin(1).setMax(AllergyConfig.MAX_SECONDS)
                .setTooltip(Text.translatable("option.allergic.allergyDurationSeconds.tooltip"))
                .setSaveConsumer(value -> draft.allergyDurationSeconds = value).build());
        category.addEntry(entries.startIntField(Text.translatable("option.allergic.sneezeMinIntervalSeconds"), saved.sneezeMinIntervalSeconds())
                .setDefaultValue(defaults.sneezeMinIntervalSeconds()).setMin(1).setMax(AllergyConfig.MAX_SECONDS)
                .setTooltip(Text.translatable("option.allergic.sneezeMinIntervalSeconds.tooltip"))
                .setSaveConsumer(value -> draft.sneezeMinIntervalSeconds = value).build());
        category.addEntry(entries.startIntField(Text.translatable("option.allergic.sneezeMaxIntervalSeconds"), saved.sneezeMaxIntervalSeconds())
                .setDefaultValue(defaults.sneezeMaxIntervalSeconds()).setMin(1).setMax(AllergyConfig.MAX_SECONDS)
                .setTooltip(Text.translatable("option.allergic.sneezeMaxIntervalSeconds.tooltip"))
                .setSaveConsumer(value -> draft.sneezeMaxIntervalSeconds = value).build());
        category.addEntry(entries.startFloatField(Text.translatable("option.allergic.sneezeDamageAmount"), saved.sneezeDamageAmount())
                .setDefaultValue(defaults.sneezeDamageAmount()).setMin(0.01F).setMax(Float.MAX_VALUE)
                .setTooltip(Text.translatable("option.allergic.sneezeDamageAmount.tooltip"))
                .setSaveConsumer(value -> draft.sneezeDamageAmount = value).build());
        category.addEntry(entries.startIntField(Text.translatable("option.allergic.nauseaMinIntervalSeconds"), saved.nauseaMinIntervalSeconds())
                .setDefaultValue(defaults.nauseaMinIntervalSeconds()).setMin(1).setMax(AllergyConfig.MAX_SECONDS)
                .setTooltip(Text.translatable("option.allergic.nauseaMinIntervalSeconds.tooltip"))
                .setSaveConsumer(value -> draft.nauseaMinIntervalSeconds = value).build());
        category.addEntry(entries.startIntField(Text.translatable("option.allergic.nauseaMaxIntervalSeconds"), saved.nauseaMaxIntervalSeconds())
                .setDefaultValue(defaults.nauseaMaxIntervalSeconds()).setMin(1).setMax(AllergyConfig.MAX_SECONDS)
                .setTooltip(Text.translatable("option.allergic.nauseaMaxIntervalSeconds.tooltip"))
                .setSaveConsumer(value -> draft.nauseaMaxIntervalSeconds = value).build());
        category.addEntry(entries.startIntField(Text.translatable("option.allergic.nauseaDurationSeconds"), saved.nauseaDurationSeconds())
                .setDefaultValue(defaults.nauseaDurationSeconds()).setMin(1).setMax(AllergyConfig.MAX_SECONDS)
                .setTooltip(Text.translatable("option.allergic.nauseaDurationSeconds.tooltip"))
                .setSaveConsumer(value -> draft.nauseaDurationSeconds = value).build());

        builder.setSavingRunnable(() -> AllergyConfig.set(draft.toConfig()));
        return builder.build();
    }

    private static final class Draft {
        private double allergyChance;
        private int allergyDurationSeconds;
        private int sneezeMinIntervalSeconds;
        private int sneezeMaxIntervalSeconds;
        private float sneezeDamageAmount;
        private int nauseaMinIntervalSeconds;
        private int nauseaMaxIntervalSeconds;
        private int nauseaDurationSeconds;

        private Draft(AllergyConfig config) {
            allergyChance = config.allergyChance();
            allergyDurationSeconds = config.allergyDurationSeconds();
            sneezeMinIntervalSeconds = config.sneezeMinIntervalSeconds();
            sneezeMaxIntervalSeconds = config.sneezeMaxIntervalSeconds();
            sneezeDamageAmount = config.sneezeDamageAmount();
            nauseaMinIntervalSeconds = config.nauseaMinIntervalSeconds();
            nauseaMaxIntervalSeconds = config.nauseaMaxIntervalSeconds();
            nauseaDurationSeconds = config.nauseaDurationSeconds();
        }

        private AllergyConfig toConfig() {
            return new AllergyConfig(
                    allergyChance,
                    allergyDurationSeconds,
                    sneezeMinIntervalSeconds,
                    sneezeMaxIntervalSeconds,
                    sneezeDamageAmount,
                    nauseaMinIntervalSeconds,
                    nauseaMaxIntervalSeconds,
                    nauseaDurationSeconds
            );
        }
    }
}
