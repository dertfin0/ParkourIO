package ru.dfhub.parkourio.components.punishments.mute;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.paper.LegacyPaperCommandManager;
import org.incendo.cloud.parser.standard.StringParser;
import org.w3c.dom.Text;
import ru.dfhub.parkourio.common.ParkourPlayer;
import ru.dfhub.parkourio.common.entity.Punishment;
import ru.dfhub.parkourio.util.CloudCommand;
import ru.dfhub.parkourio.util.TempPlayerListCache;
import ru.dfhub.parkourio.util.TimeParser;

import static net.kyori.adventure.text.minimessage.MiniMessage.miniMessage;
import static ru.dfhub.parkourio.util.MessageManager.getMessage;

public class Mute implements CloudCommand {

    public void register(LegacyPaperCommandManager<CommandSender> manager) {
        manager.command(manager
                .commandBuilder("mute", "мут")
                .flag(manager.flagBuilder("silent").build())
                .required("player", StringParser.stringParser(), new TempPlayerListCache.Suggestions())
                .required("duration", StringParser.stringParser(), new TimeParser.Suggestions())
                .optional("reason", StringParser.greedyStringParser())
                .permission("ru.dfhub.parkourio.punishments.mute")
                .handler(this::handle)
        );
    }

    private void handle(CommandContext<CommandSender> ctx) {
        OfflinePlayer player = Bukkit.getOfflinePlayer(ctx.getOrDefault("player", "null"));

        boolean isSilent = ctx.getOrDefault("reason", "").contains("--silent");
        String reason = ctx.getOrDefault("reason", "Причина не указана").replaceAll("--silent", "");

        if (reason.isEmpty()) {
            reason = "Причина не указана";
        } // Если указан только --silent, без причины

        long duration;
        try {
            duration = TimeParser.stringToLong(ctx.get("duration"));
        } catch (Exception e) {
            ctx.sender().sendMessage(miniMessage().deserialize(getMessage("punishments.cant-recognize-duration")));
            return;
        }


        new ParkourPlayer(player).mute(ctx.sender().getName(), duration, reason);

        ctx.sender().sendMessage(miniMessage().deserialize(
                (duration == -1 ? getMessage("punishments.mute.mute.muted-permanently.to-admin") : getMessage("punishments.mute.mute.muted.to-admin"))
                        .replace("%player%", player.getName())
                        .replace("%time%", TimeParser.longToString(duration))
        ));

        if (!isSilent) {
            Component message = miniMessage().deserialize(
                    (duration == -1 ? getMessage("punishments.mute.mute.muted-permanently.broadcast") : getMessage("punishments.mute.mute.muted.broadcast"))
                            .replace("%admin%", ctx.sender().getName())
                            .replace("%player%", player.getName())
                            .replace("%time%", TimeParser.longToString(duration))
                            .replace("%reason%", reason)
            );
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                onlinePlayer.sendMessage(message);
            }
        }

        if (player.isOnline()) {
            Component message = miniMessage().deserialize(
                    (duration == -1 ? getMessage("punishments.mute.mute.muted-permanently.to-player") : getMessage("punishments.mute.mute.muted.to-player"))
                            .replace("%admin%", ctx.sender().getName())
                            .replace("%time%", TimeParser.longToString(duration))
                            .replace("%reason%", reason)
            );
            player.getPlayer().sendMessage(message);
        }
    }

    public static class Handler implements Listener {

        @EventHandler(priority = EventPriority.LOWEST)
        public void onChatMessage(AsyncChatEvent e) {
            ParkourPlayer player = new ParkourPlayer(e.getPlayer());
            if (player.hasActiveMute()) {
                e.setCancelled(true);
                Punishment mute = player.getActiveMute();

                String mainMessage = mute.getDuration() == -1 ? getMessage("punishments.mute.mute.message-with-mute-permanent.main") : getMessage("punishments.mute.mute.message-with-mute.main");

                String hoverText = (mute.getDuration() == -1 ? getMessage("punishments.mute.mute.message-with-mute-permanent.hover") : getMessage("punishments.mute.mute.message-with-mute.hover"))
                        .replace("%admin%", mute.getFromAdmin())
                        .replace("%reason%", mute.getReason());

                e.getPlayer().sendMessage(miniMessage().deserialize(
                        mainMessage
                                .replace("%hover%", hoverText)
                                .replace("%time%", TimeParser.longToString(mute.getStartsAt() + mute.getDuration() - System.currentTimeMillis()))
                ));

                ShowMutedMsg.handle(e.getPlayer().getName(), ((TextComponent) e.message()).content());
            }
        }
    }
}
