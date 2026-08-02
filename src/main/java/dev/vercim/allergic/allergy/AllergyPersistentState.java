package dev.vercim.allergic.allergy;

import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.world.PersistentState;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

public final class AllergyPersistentState extends PersistentState {
    private static final String STATE_ID = "allergic_player_allergies";
    private static final String PLAYERS_KEY = "Players";
    private static final String PLAYER_UUID_KEY = "PlayerUuid";
    private static final String ALLERGENS_KEY = "Allergens";
    private static final Type<AllergyPersistentState> TYPE = new Type<>(
            AllergyPersistentState::new,
            AllergyPersistentState::fromNbt,
            DataFixTypes.LEVEL
    );

    private final Map<UUID, Set<Identifier>> allergensByPlayer = new HashMap<>();

    public static AllergyPersistentState get(MinecraftServer server) {
        return server.getOverworld().getPersistentStateManager().getOrCreate(TYPE, STATE_ID);
    }

    public boolean hasPlayer(UUID playerUuid) {
        return allergensByPlayer.containsKey(playerUuid);
    }

    public Set<Identifier> getAllergens(UUID playerUuid) {
        Set<Identifier> identifiers = allergensByPlayer.get(playerUuid);
        if (identifiers == null) {
            return Set.of();
        }
        return Collections.unmodifiableSet(new LinkedHashSet<>(identifiers));
    }

    public boolean contains(UUID playerUuid, Identifier identifier) {
        return allergensByPlayer.getOrDefault(playerUuid, Set.of()).contains(identifier);
    }

    public void setAllergens(UUID playerUuid, Set<Identifier> identifiers) {
        allergensByPlayer.put(playerUuid, new TreeSet<>(identifiers));
        markDirty();
    }

    public boolean add(UUID playerUuid, Identifier identifier) {
        boolean changed = allergensByPlayer.computeIfAbsent(playerUuid, ignored -> new TreeSet<>()).add(identifier);
        if (changed) {
            markDirty();
        }
        return changed;
    }

    public boolean remove(UUID playerUuid, Identifier identifier) {
        Set<Identifier> identifiers = allergensByPlayer.get(playerUuid);
        boolean changed = identifiers != null && identifiers.remove(identifier);
        if (changed) {
            markDirty();
        }
        return changed;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        NbtList players = new NbtList();
        for (Map.Entry<UUID, Set<Identifier>> entry : allergensByPlayer.entrySet()) {
            NbtCompound playerNbt = new NbtCompound();
            playerNbt.putUuid(PLAYER_UUID_KEY, entry.getKey());

            NbtList allergens = new NbtList();
            for (Identifier identifier : entry.getValue()) {
                allergens.add(NbtString.of(identifier.toString()));
            }
            playerNbt.put(ALLERGENS_KEY, allergens);
            players.add(playerNbt);
        }
        nbt.put(PLAYERS_KEY, players);
        return nbt;
    }

    private static AllergyPersistentState fromNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        AllergyPersistentState state = new AllergyPersistentState();
        NbtList players = nbt.getList(PLAYERS_KEY, NbtElement.COMPOUND_TYPE);

        for (int playerIndex = 0; playerIndex < players.size(); playerIndex++) {
            NbtCompound playerNbt = players.getCompound(playerIndex);
            if (!playerNbt.containsUuid(PLAYER_UUID_KEY)) {
                continue;
            }

            UUID playerUuid = playerNbt.getUuid(PLAYER_UUID_KEY);
            Set<Identifier> identifiers = new TreeSet<>();
            NbtList allergens = playerNbt.getList(ALLERGENS_KEY, NbtElement.STRING_TYPE);
            for (int allergenIndex = 0; allergenIndex < allergens.size(); allergenIndex++) {
                Identifier identifier = Identifier.tryParse(allergens.getString(allergenIndex));
                if (identifier != null && Registries.ITEM.containsId(identifier)) {
                    identifiers.add(identifier);
                }
            }
            state.allergensByPlayer.put(playerUuid, identifiers);
        }
        return state;
    }
}
