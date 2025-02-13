package ru.dfhub.parkourio.components;

import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.paper.LegacyPaperCommandManager;
import org.incendo.cloud.parser.standard.StringParser;
import ru.dfhub.parkourio.ParkourIO;
import ru.dfhub.parkourio.common.ParkourPlayer;
import ru.dfhub.parkourio.util.CloudCommand;
import ru.dfhub.parkourio.util.Metadata;
import ru.dfhub.parkourio.util.TempPlayerListCache;


import static net.kyori.adventure.text.minimessage.MiniMessage.miniMessage;
import static ru.dfhub.parkourio.util.MessageManager.getMessage;

public class TimeplayedCommand implements CloudCommand {
    @Override
    public void register(LegacyPaperCommandManager<CommandSender> manager) {
        manager.command(manager
                .commandBuilder("timeplayed")
                .permission("ru.dfhub.parkourio.command.timeplayed")
                .optional("player", StringParser.stringParser(), new TempPlayerListCache.Suggestions())
                .handler(this::handle)
        );
    }

    private void handle(CommandContext<CommandSender> ctx) {
        String player = ctx.getOrDefault("player", ctx.sender().getName());
        long time = new ParkourPlayer(player).getTimeplayed();

        if (time == 0) {
            ctx.sender().sendMessage(miniMessage().deserialize(getMessage("command.timeplayed.not-found")));
            return;
        }

        ctx.sender().sendMessage(miniMessage().deserialize(getMessage(player.equals(ctx.sender().getName()) ? "command.timeplayed.result-self" : "command.timeplayed.result")
                .replace("%player%", player)
                .replace("%time%", Math.round((double) (time * 10) / 3_600_000) / 10.0 + "ч"))
        );
    }

    public static class Handler implements Listener {

        @EventHandler
        public void onPlayerJoin(PlayerJoinEvent e) {
            e.getPlayer().setMetadata(Metadata.JOINED_AT.value(), new FixedMetadataValue(ParkourIO.getInstance(), System.currentTimeMillis()));
        }

        @EventHandler
        public void onPlayerQuit(PlayerQuitEvent e) {
            long timeplayed = System.currentTimeMillis() - e.getPlayer().getMetadata(Metadata.JOINED_AT.value()).get(0).asLong();
            e.getPlayer().removeMetadata(Metadata.JOINED_AT.value(), ParkourIO.getInstance());
            new ParkourPlayer(e.getPlayer()).addTimeplayed(timeplayed);
        }
    }
}
