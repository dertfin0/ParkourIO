package ru.dfhub.parkourio.components.punishments.mute;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.context.CommandInput;
import org.incendo.cloud.paper.LegacyPaperCommandManager;
import org.incendo.cloud.parser.standard.StringParser;
import org.incendo.cloud.suggestion.Suggestion;
import org.incendo.cloud.suggestion.SuggestionProvider;
import ru.dfhub.parkourio.common.ParkourPlayer;
import ru.dfhub.parkourio.common.dao.PunishmentsDAO;
import ru.dfhub.parkourio.util.CloudCommand;
import ru.dfhub.parkourio.util.TempPlayerListCache;

import java.util.concurrent.CompletableFuture;

import static net.kyori.adventure.text.minimessage.MiniMessage.miniMessage;
import static ru.dfhub.parkourio.util.MessageManager.getMessage;

public class UnMute implements CloudCommand {
    @Override
    public void register(LegacyPaperCommandManager<CommandSender> manager) {
        manager.command(manager
                .commandBuilder("unmute")
                .required("player", StringParser.stringParser(), new MutedPlayersSuggestion())
                .permission("ru.dfhub.parkourio.punishments.unmute")
                .handler(ctx -> {
                    OfflinePlayer player = Bukkit.getOfflinePlayer(ctx.getOrDefault("player", "null"));
                    ParkourPlayer parkourPlayer = new ParkourPlayer(player);

                    if (!parkourPlayer.hasActiveMute()) {
                        ctx.sender().sendMessage(miniMessage().deserialize(
                               getMessage("punishments.mute.unmute.hasnt-mutes").formatted(player.getPlayer().getName())
                        ));
                        return;
                    }

                    parkourPlayer.unmute();
                    ctx.sender().sendMessage(miniMessage().deserialize(
                            getMessage("punishments.mute.unmute.unmuted").formatted(player.getPlayer().getName())
                    ));

                    if (player.isOnline()) {
                        player.getPlayer().sendMessage(miniMessage().deserialize(
                                getMessage("punishments.mute.unmute.unmuted-to-player").replace("%admin%", ctx.sender().getName())
                        ));
                    }
                })
        );
    }

    private static class MutedPlayersSuggestion implements SuggestionProvider<CommandSender> {

        @Override
        public @NonNull CompletableFuture<? extends @NonNull Iterable<? extends @NonNull Suggestion>> suggestionsFuture(@NonNull CommandContext<CommandSender> context, @NonNull CommandInput input) {
            return CompletableFuture.supplyAsync(() -> PunishmentsDAO.getMutedPlayers()
                    .stream()
                    .map(Suggestion::suggestion)
                    .toList()
            );
        }
    }
}
