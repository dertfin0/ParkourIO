package ru.dfhub.parkourio.components;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.Command;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.paper.LegacyPaperCommandManager;
import ru.dfhub.parkourio.components.parkour_level.ParkourLevels;
import ru.dfhub.parkourio.util.CloudCommand;
import ru.dfhub.parkourio.util.Config;

/**
 * Комадна для перезагрузки кастомных компонентов плагина
 */
public class PReloadCommand implements CloudCommand {

    public void register(LegacyPaperCommandManager<CommandSender> manager) {
        Command.Builder<CommandSender> builder = manager.commandBuilder("preload", "reloadp").permission("ru.dfhub.parkourio.command.reload");

        manager.command(builder
                .literal("config")
                .handler(this::reloadConfig)
        );
        manager.command(builder
                .literal("levels")
                .handler(this::reloadLevels)
        );
    }

    public void handle(CommandContext<CommandSender> ctx) {}

    private void reloadConfig(CommandContext<CommandSender> ctx) {
        Config.reload();
        ctx.sender().sendMessage(MiniMessage.miniMessage().deserialize(
            "<green>Конфиг перезагружен!</green>"
        ));
    }

    private void reloadLevels(CommandContext<CommandSender> ctx) {
        ParkourLevels.reload();
        ctx.sender().sendMessage(MiniMessage.miniMessage().deserialize(
                "<green>Уровни паркура перезагружены!</green>"
        ));
    }
}
