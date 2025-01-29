package ru.dfhub.parkourio.components.punishments.mute;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
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
import ru.dfhub.parkourio.common.ParkourPlayer;
import ru.dfhub.parkourio.common.entity.Punishment;
import ru.dfhub.parkourio.util.CloudCommand;
import ru.dfhub.parkourio.util.TimeParser;

import static net.kyori.adventure.text.minimessage.MiniMessage.miniMessage;

public class Mute implements CloudCommand {

    public void register(LegacyPaperCommandManager<CommandSender> manager) {
        manager.command(manager
                .commandBuilder("mute", "мут")
                .flag(manager.flagBuilder("silent").build())
                .required("player", StringParser.stringParser())
                .required("duration", StringParser.stringParser())
                .optional("reason", StringParser.greedyStringParser())
                .permission("ru.dfhub.parkourio.punishments.mute")
                .handler(this::handle)
        );
    }

    private void handle(CommandContext<CommandSender> ctx) {
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


        new ParkourPlayer(player).mute(ctx.sender().getName(), duration, reason.isEmpty() ? "Причина не указана" : reason);

        ctx.sender().sendMessage(miniMessage().deserialize(
                "<green>Вы успешно замутили <aqua>%player%</aqua> на <aqua>%time%</aqua>."
                        .replace("%player%", player.getName())
                        .replace("%time%", TimeParser.longToString(duration))
        ));

        if (!isSilent) {
            Component message = miniMessage().deserialize(
                    "<yellow>Администратор <aqua>%admin%</aqua> замутил игрока <aqua>%player%</aqua> на <aqua>%time%</aqua>. Причина: <aqua>%reason%</aqua>"
                            .replace("%admin%", ctx.sender().getName())
                            .replace("%player%", player.getName())
                            .replace("%time%", TimeParser.longToString(duration))
                            .replace("%reason%", ctx.getOrDefault("reason", "Причина не указана"))
            );
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                onlinePlayer.sendMessage(message);
            }
        }

        if (player.isOnline()) {
            Component message = miniMessage().deserialize(
                    "<yellow>Администратор <aqua>%admin%</aqua> замутил вас на <aqua>%time%</aqua>. Причина: <aqua>%reason%</aqua>"
                            .replace("%admin%", ctx.sender().getName())
                            .replace("%time%", TimeParser.longToString(duration))
                            .replace("%reason%", ctx.getOrDefault("reason", "Причина не указана"))
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

                String hoverText = "<yellow>Администратор <aqua>%admin%</aqua> ограничил вам доступ к чату %forever% по причине: <aqua>%reason%</aqua>.</yellow>\n\n<yellow>Данное решение может быть перестроено администратором</yellow>"
                        .replace("%admin%", mute.getFromAdmin())
                        .replace("%reason%", mute.getReason())
                        .replace("%forever%", mute.getDuration() == -1 ? "<red>НАВСЕГДА</red>" : "");

                String mainMessage = mute.getDuration() == -1 ?
                        "<red>Вы больше не можете отправлять сообщения в чат! <hover:show_text:'%hover%'><u>Подробнее</u></hover></red>" :
                        "<red>Вы не можете отправлять сообщения ещё <aqua>%time%</aqua>! <hover:show_text:'%hover%'><u>Подробнее</u></hover></red>";


                e.getPlayer().sendMessage(miniMessage().deserialize(
                        mainMessage
                                .replace("%hover%", hoverText)
                                .replace("%time%", TimeParser.longToString(mute.getStartsAt() + mute.getDuration() - System.currentTimeMillis()))
                ));
            }
        }
    }
}
