package ru.dfhub.parkourio.components.punishments.ban;

import org.bukkit.Bukkit;
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
import ru.dfhub.parkourio.common.entity.Punishment;
import ru.dfhub.parkourio.util.CloudCommand;
import ru.dfhub.parkourio.util.TempPlayerListCache;

import java.util.concurrent.CompletableFuture;

import static net.kyori.adventure.text.minimessage.MiniMessage.miniMessage;
import static ru.dfhub.parkourio.util.MessageManager.getMessage;

public class UnBan implements CloudCommand {
    @Override
    public void register(LegacyPaperCommandManager<CommandSender> manager) {
        manager.command(manager
                .commandBuilder("unban", "unban")
                .permission("ru.dfhub.parkourio.punishments.unban")
                .required("player", StringParser.stringParser(), new BannedPlayersSuggestion())
                .handler(this::handler)
        );
    }

    public void handler(CommandContext<CommandSender> ctx) {
        ParkourPlayer player = new ParkourPlayer(Bukkit.getOfflinePlayer(ctx.getOrDefault("player", "null"))) ;
        if (!player.hasActiveBan()) {
            ctx.sender().sendMessage(miniMessage().deserialize(
                    getMessage("punishments.ban.unban.not-found").replace("%player%", player.getPlayer().getName())
            ));
            return;
        }
        player.unban();
        ctx.sender().sendMessage(miniMessage().deserialize(
                getMessage("punishments.ban.unban.unbanned").replace("%player%", player.getPlayer().getName())
        ));
    }

    private static class BannedPlayersSuggestion implements SuggestionProvider<CommandSender> {

        @Override
        public @NonNull CompletableFuture<? extends @NonNull Iterable<? extends @NonNull Suggestion>> suggestionsFuture(@NonNull CommandContext<CommandSender> context, @NonNull CommandInput input) {
            return CompletableFuture.supplyAsync(() -> PunishmentsDAO.getBannedPlayers()
                    .stream()
                    .map(Suggestion::suggestion)
                    .toList()
            );
        }
    }
}
