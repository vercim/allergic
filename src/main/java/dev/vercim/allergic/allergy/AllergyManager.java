package dev.vercim.allergic.allergy;

import dev.vercim.allergic.AllergicMod;
import dev.vercim.allergic.config.AllergyConfig;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ThreadLocalRandom;

public final class AllergyManager {
    private AllergyManager() {
    }

    public static void registerEvents() {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> ensureGenerated(handler.player));
    }

    public static Set<Identifier> ensureGenerated(ServerPlayerEntity player) {
        AllergyPersistentState state = getState(player);
        if (!state.hasPlayer(player.getUuid())) {
            Set<Identifier> generated = generate(AllergyConfig.get().allergyChance());
            state.setAllergens(player.getUuid(), generated);
            AllergicMod.LOGGER.info("Generated {} food allergens for player {}.", generated.size(), player.getGameProfile().getName());
        }
        return state.getAllergens(player.getUuid());
    }

    public static Set<Identifier> regenerate(ServerPlayerEntity player) {
        Set<Identifier> generated = generate(AllergyConfig.get().allergyChance());
        getState(player).setAllergens(player.getUuid(), generated);
        AllergicMod.LOGGER.info("Regenerated {} food allergens for player {}.", generated.size(), player.getGameProfile().getName());
        return generated;
    }

    public static Set<Identifier> getAllergens(ServerPlayerEntity player) {
        ensureGenerated(player);
        return getState(player).getAllergens(player.getUuid());
    }

    public static boolean isAllergen(ServerPlayerEntity player, Identifier identifier) {
        ensureGenerated(player);
        return getState(player).contains(player.getUuid(), identifier);
    }

    public static boolean add(ServerPlayerEntity player, Identifier identifier) {
        ensureGenerated(player);
        return getState(player).add(player.getUuid(), identifier);
    }

    public static boolean remove(ServerPlayerEntity player, Identifier identifier) {
        ensureGenerated(player);
        return getState(player).remove(player.getUuid(), identifier);
    }

    private static AllergyPersistentState getState(ServerPlayerEntity player) {
        MinecraftServer server = player.getServer();
        if (server == null) {
            throw new IllegalStateException("Server player is not attached to a server.");
        }
        return AllergyPersistentState.get(server);
    }

    private static Set<Identifier> generate(double chance) {
        Set<Identifier> generated = new TreeSet<>();
        ThreadLocalRandom random = ThreadLocalRandom.current();
        for (Identifier identifier : EdibleItemCatalog.findAll()) {
            if (random.nextDouble() < chance) {
                generated.add(identifier);
            }
        }
        return generated;
    }
}
