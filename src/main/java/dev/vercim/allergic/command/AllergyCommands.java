package dev.vercim.allergic.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.vercim.allergic.allergy.AllergyManager;
import dev.vercim.allergic.effect.AllergyStatusEffect;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.ItemStackArgumentType;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public final class AllergyCommands {
    private AllergyCommands() {
    }

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> register(dispatcher, registryAccess));
    }

    private static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
        dispatcher.register(CommandManager.literal("allergy")
                .then(CommandManager.literal("list")
                        .executes(AllergyCommands::list)
                        .then(CommandManager.argument("targets", EntityArgumentType.players())
                                .executes(AllergyCommands::list)))
                .then(CommandManager.literal("regenerate")
                        .requires(source -> source.hasPermissionLevel(2))
                        .executes(AllergyCommands::regenerate)
                        .then(CommandManager.argument("targets", EntityArgumentType.players())
                                .executes(AllergyCommands::regenerate)))
                .then(CommandManager.literal("sneeze")
                        .requires(source -> source.hasPermissionLevel(2))
                        .executes(AllergyCommands::sneeze)
                        .then(CommandManager.argument("targets", EntityArgumentType.players())
                                .executes(AllergyCommands::sneeze)))
                .then(CommandManager.literal("add")
                        .requires(source -> source.hasPermissionLevel(2))
                        .then(CommandManager.argument("item", ItemStackArgumentType.itemStack(registryAccess))
                                .executes(AllergyCommands::add)
                                .then(CommandManager.argument("targets", EntityArgumentType.players())
                                        .executes(AllergyCommands::add))))
                .then(CommandManager.literal("remove")
                        .requires(source -> source.hasPermissionLevel(2))
                        .then(CommandManager.argument("item", ItemStackArgumentType.itemStack(registryAccess))
                                .executes(AllergyCommands::remove)
                                .then(CommandManager.argument("targets", EntityArgumentType.players())
                                        .executes(AllergyCommands::remove)))));
    }

    private static int list(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        int total = 0;
        for (ServerPlayerEntity player : getTargets(context)) {
            Set<Identifier> allergens = AllergyManager.getAllergens(player);
            if (allergens.isEmpty()) {
                context.getSource().sendFeedback(() -> Text.translatable("commands.allergic.list.empty"), false);
                continue;
            }

            context.getSource().sendFeedback(() -> Text.translatable("commands.allergic.list.header", allergens.size()), false);
            for (Identifier identifier : allergens) {
                context.getSource().sendFeedback(() -> Text.literal("- " + identifier), false);
            }
            total += allergens.size();
        }
        return total;
    }

    private static int regenerate(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        int total = 0;
        for (ServerPlayerEntity player : getTargets(context)) {
            int count = AllergyManager.regenerate(player).size();
            context.getSource().sendFeedback(() -> Text.translatable("commands.allergic.regenerated", count), true);
            total += count;
        }
        return total;
    }

    private static int sneeze(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Collection<ServerPlayerEntity> targets = getTargets(context);
        for (ServerPlayerEntity player : targets) {
            AllergyStatusEffect.triggerSneeze(player);
        }
        return targets.size();
    }

    private static int add(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Identifier identifier = getItemIdentifier(context);
        int addedCount = 0;
        for (ServerPlayerEntity player : getTargets(context)) {
            boolean added = AllergyManager.add(player, identifier);
            String key = added ? "commands.allergic.added" : "commands.allergic.already_present";
            context.getSource().sendFeedback(() -> Text.translatable(key, identifier), true);
            if (added) {
                addedCount++;
            }
        }
        return addedCount;
    }

    private static int remove(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Identifier identifier = getItemIdentifier(context);
        int removedCount = 0;
        for (ServerPlayerEntity player : getTargets(context)) {
            boolean removed = AllergyManager.remove(player, identifier);
            String key = removed ? "commands.allergic.removed" : "commands.allergic.not_present";
            context.getSource().sendFeedback(() -> Text.translatable(key, identifier), true);
            if (removed) {
                removedCount++;
            }
        }
        return removedCount;
    }

    private static Collection<ServerPlayerEntity> getTargets(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        try {
            return EntityArgumentType.getPlayers(context, "targets");
        } catch (IllegalArgumentException exception) {
            return List.of(context.getSource().getPlayerOrThrow());
        }
    }

    private static Identifier getItemIdentifier(CommandContext<ServerCommandSource> context) {
        Item item = ItemStackArgumentType.getItemStackArgument(context, "item").getItem();
        return Registries.ITEM.getId(item);
    }
}
