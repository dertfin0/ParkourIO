package ru.dfhub.parkourio.components.punishments;

import com.mysql.cj.util.StringUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.util.StringUtil;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.paper.LegacyPaperCommandManager;
import org.incendo.cloud.parser.standard.IntegerParser;
import org.incendo.cloud.parser.standard.StringParser;
import ru.dfhub.parkourio.common.dao.PunishmentsDAO;
import ru.dfhub.parkourio.common.entity.Punishment;
import ru.dfhub.parkourio.util.CloudCommand;
import ru.dfhub.parkourio.util.TimeParser;

import java.sql.Time;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static net.kyori.adventure.text.minimessage.MiniMessage.miniMessage;
import static ru.dfhub.parkourio.util.MessageManager.getMessage;

public class PunishmentsCommand implements CloudCommand {
    @Override
    public void register(LegacyPaperCommandManager<CommandSender> manager) {
        manager.command(manager
                .commandBuilder("punishments")
                .permission("ru.dfhub.parkourio.command.punishments")
                .required("player", StringParser.stringParser())
                .optional("limit", IntegerParser.integerParser(1, 100))
                .handler(this::handle)
        );
    }

    private void handle(CommandContext<CommandSender> ctx) {
        List<Punishment> punishments = PunishmentsDAO.getPunishmentsByPlayer(Bukkit.getOfflinePlayer(ctx.getOrDefault("player", "JohnDoe")));

        if (punishments.isEmpty()) {
            ctx.sender().sendMessage(miniMessage().deserialize(getMessage("command.punishments.empty")));
            return;
        }
        int limit = ctx.getOrDefault("limit", 100);

        Component result = Component.empty();
        result = result.append(miniMessage().deserialize(
                getMessage("command.punishments.header").replace("%player%", ctx.get("player"))
        ));

        for (int i = (punishments.size() < limit ? 0 : punishments.size() - limit); i < punishments.size(); i++) {
            Punishment p = punishments.get(i);

            String prefix = switch (p.getType()) {
                case BAN:
                    yield getMessage("command.punishments.prefix.ban");
                case MUTE:
                    yield getMessage("command.punishments.prefix.mute");
                case KICK:
                    yield getMessage("command.punishments.prefix.kick");
            };

            // Зачеркивание для отменённых наказаний
            String strikethroughPrefix = p.getActive() == 0 ? "<st>" : "";
            String strikethroughSuffix = p.getActive() == 0 ? "</st>" : "";

            // Тёмно-серый если наказание закончилось и серый, если активно
            String lateColorPrefix = p.isLate() ? "<dark_gray>" : "<gray>";
            String lateColorSuffix = p.isLate() ? "</dark_gray>" : "</gray>";

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yy HH:mm");

            String hover = getMessage("command.punishments.hover")
                    .replace("%admin%", p.getFromAdmin())
                    .replace("%duration%", p.getDuration() == 0 ? "не указано" : TimeParser.longToString(p.getDuration()))
                    .replace("%startsAt%", Instant.ofEpochMilli(p.getStartsAt()).atZone(ZoneId.of("Europe/Moscow")).format(formatter))
                    .replace("%endsAt%", Instant.ofEpochMilli(p.getStartsAt() + p.getDuration()).atZone(ZoneId.of("Europe/Moscow")).format(formatter));

            result = result.append(miniMessage().deserialize("<hover:show_text:'%s'>".formatted(hover) +
                    StringUtils.padString(prefix, 7) + " " + lateColorPrefix + strikethroughPrefix + p.getReason() + strikethroughSuffix + lateColorSuffix
            + "</hover>\n"));
        }
        result = result.append(miniMessage().deserialize(
                getMessage("command.punishments.footer")
        ));

        ctx.sender().sendMessage(result);
    }
}
