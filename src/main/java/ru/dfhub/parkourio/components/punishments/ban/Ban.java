package ru.dfhub.parkourio.components.punishments.ban;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.paper.LegacyPaperCommandManager;
import org.incendo.cloud.parser.standard.StringParser;
import ru.dfhub.parkourio.common.ParkourPlayer;
import ru.dfhub.parkourio.common.entity.Punishment;
import ru.dfhub.parkourio.util.CloudCommand;
import ru.dfhub.parkourio.util.TimeParser;

import static net.kyori.adventure.text.minimessage.MiniMessage.miniMessage;
import static ru.dfhub.parkourio.util.MessageManager.getMessage;

public class Ban implements CloudCommand {
    @Override
    public void register(LegacyPaperCommandManager<CommandSender> manager) {
        manager.command(manager
                .commandBuilder("tempban", "бан")
                .permission("ru.dfhub.parkourio.punishments.ban")
                .required("player", StringParser.stringParser())
                .required("duration", StringParser.stringParser())
                .optional("reason", StringParser.greedyStringParser())
                .handler(this::handler)
        );
    }

    private void handler(CommandContext<CommandSender> ctx) {
            OfflinePlayer player = Bukkit.getOfflinePlayer(ctx.getOrDefault("player", "null"));

            boolean isSilent = ctx.getOrDefault("reason", "").contains("--silent");
            String reason = ctx.getOrDefault("reason", "Причина не указана").replace("--silent", "");

            long duration;
            try {
                duration = TimeParser.stringToLong(ctx.get("duration"));
            } catch (Exception e) {
                ctx.sender().sendMessage(miniMessage().deserialize(getMessage("punishments.cant-recognize-duration")));
                return;
            }

            new ParkourPlayer(player).ban(ctx.sender().getName(), duration, reason.isEmpty() ? "Причина не указана" : reason);

            ctx.sender().sendMessage(miniMessage().deserialize(
                    (duration == -1 ? getMessage("punishments.ban.ban.banned-permanently.to-admin") : getMessage("punishments.ban.ban.banned.to-admin"))
                            .replace("%player%", player.getName())
                            .replace("%time%", TimeParser.longToString(duration))
            ));

        if (!isSilent) {
            Component message = miniMessage().deserialize(
                    (duration == -1 ? getMessage("punishments.ban.ban.banned-permanently.broadcast") : getMessage("punishments.ban.ban.banned.broadcast"))
                            .replace("%admin%", ctx.sender().getName())
                            .replace("%player%", player.getName())
                            .replace("%time%", duration == -1 ? "<red>навсегда</red>" : "на <aqua>%s</aqua>".formatted(TimeParser.longToString(duration)))
                            .replace("%reason%", ctx.getOrDefault("reason", "Причина не указана"))
            );
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                onlinePlayer.sendMessage(message);
            }
        }
    }

    public static Component getBanTitle(Player player) {
        Punishment punishment = new ParkourPlayer(player).getActiveBan();

        return miniMessage().deserialize(
                (punishment.getDuration() == -1 ? getMessage("punishments.ban.ban.permanent-kick-reason") : getMessage("punishments.ban.ban.kick-reason"))
                .replace("%admin%", punishment.getFromAdmin())
                .replace("%reason%", punishment.getReason())
                .replace("%time%", TimeParser.longToString(punishment.getStartsAt() + punishment.getDuration() - System.currentTimeMillis()))
        );
    }

    public static class Handler implements Listener {
        @EventHandler(priority = EventPriority.LOWEST)
        public void onJoin(PlayerJoinEvent e) {
            ParkourPlayer player = new ParkourPlayer(e.getPlayer());
            if (player.hasActiveBan()) {
                e.getPlayer().kick(getBanTitle(e.getPlayer()));
            }
        }
    }
}
