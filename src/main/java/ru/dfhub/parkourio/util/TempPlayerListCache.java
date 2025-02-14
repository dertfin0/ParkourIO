package ru.dfhub.parkourio.util;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.context.CommandInput;
import org.incendo.cloud.suggestion.Suggestion;
import org.incendo.cloud.suggestion.SuggestionProvider;
import ru.dfhub.parkourio.common.dao.TimeplayedDAO;

import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Класс, сохраняющий последних играющих на сервере игроков
 */
public class TempPlayerListCache {

    @Deprecated
    private static final HashSet<String> players = new HashSet<>();

    @Deprecated
    public static class Handler implements Listener {
        @EventHandler
        public void onPlayerJoin(PlayerJoinEvent e) {
            players.add(e.getPlayer().getName());
        }
    }

    public static class Suggestions implements SuggestionProvider<CommandSender> {

        /*
        @Override
        public @NonNull CompletableFuture<? extends @NonNull Iterable<? extends @NonNull Suggestion>> suggestionsFuture(org.incendo.cloud.context.@NonNull CommandContext<CommandSender> context, @NonNull CommandInput input) {
            return CompletableFuture.supplyAsync(() -> players.stream().map(Suggestion::suggestion).toList());
        }

         */

        @Override
        public @NonNull CompletableFuture<? extends @NonNull Iterable<? extends @NonNull Suggestion>> suggestionsFuture(org.incendo.cloud.context.@NonNull CommandContext<CommandSender> context, @NonNull CommandInput input) {
            return CompletableFuture.supplyAsync(() -> TimeplayedDAO.getPlayers()
                    .stream()
                    .map(Suggestion::suggestion)
                    .toList());
        }
    }
}
