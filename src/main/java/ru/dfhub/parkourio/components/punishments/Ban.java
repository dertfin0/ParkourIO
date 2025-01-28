package ru.dfhub.parkourio.components.punishments;

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
                ctx.sender().sendMessage(miniMessage().deserialize(
                        "<red>Не удалось распознать указанное время!</red>"
                ));
                return;
            }

            new ParkourPlayer(player).ban(ctx.sender().getName(), duration, reason.isEmpty() ? "Причина не указана" : reason);

            ctx.sender().sendMessage(miniMessage().deserialize(
                    "<green>Вы успешно забанили <aqua>%player%</aqua> на <aqua>%time%</aqua>."
                            .replace("%player%", player.getName())
                            .replace("%time%", TimeParser.longToString(duration))
            ));

        if (!isSilent) {
            Component message = miniMessage().deserialize(
                    "<yellow>Администратор <aqua>%admin%</aqua> забанил игрока <aqua>%player%</aqua> на <aqua>%time%</aqua>. Причина: <aqua>%reason%</aqua>"
                            .replace("%admin%", ctx.sender().getName())
                            .replace("%player%", player.getName())
                            .replace("%time%", TimeParser.longToString(duration))
                            .replace("%reason%", ctx.getOrDefault("reason", "Причина не указана"))
            );
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                onlinePlayer.sendMessage(message);
            }
        }
    }

    public static Component getBanTitle(Player player) {
        Punishment punishment = new ParkourPlayer(player).getActiveBan();

        return miniMessage().deserialize("""
                <red><b>Ваш аккаунт заблокирован!</b></red>
            
                Администратор %admin% забанил ваш аккаунт по причине:
                <aqua>%reason%</aqua>
                %time%
            """
                .trim()
                .replace("%admin%", punishment.getFromAdmin())
                .replace("%reason%", punishment.getReason())
                .replace("%time%", punishment.getDuration() == -1 ? "Блокировка выдана <red>навсегда</red>, но данное решение может пересмотреть администратор" :
                        "До конца блокировки осталось: <aqua>" + TimeParser.longToString(punishment.getStartsAt() + punishment.getDuration() - System.currentTimeMillis()) + "</aqua>"
                )
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
