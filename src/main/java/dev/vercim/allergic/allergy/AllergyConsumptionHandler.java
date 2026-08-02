package dev.vercim.allergic.allergy;

import dev.vercim.allergic.config.AllergyConfig;
import dev.vercim.allergic.registry.ModStatusEffects;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public final class AllergyConsumptionHandler {
    private AllergyConsumptionHandler() {
    }

    public static void onFinishedUsing(ItemStack consumedStack, World world, LivingEntity user) {
        if (world.isClient || !(user instanceof ServerPlayerEntity player) || !EdibleItemCatalog.isEdible(consumedStack)) {
            return;
        }

        Identifier itemId = Registries.ITEM.getId(consumedStack.getItem());
        if (!AllergyManager.isAllergen(player, itemId)) {
            return;
        }

        int durationTicks = Math.multiplyExact(AllergyConfig.get().allergyDurationSeconds(), 20);
        player.addStatusEffect(new StatusEffectInstance(ModStatusEffects.ALLERGY, durationTicks, 0, false, true, true));
    }
}
