package ru.dfhub.parkourio.components;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.Command;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.paper.LegacyPaperCommandManager;
import ru.dfhub.parkourio.util.CloudCommand;
import ru.dfhub.parkourio.util.Config;

public class PReloadCommand implements CloudCommand {

    public void register(LegacyPaperCommandManager<CommandSender> manager) {
        Command.Builder<CommandSender> builder = manager.commandBuilder("preload", "reloadp").permission("ru.dfhub.parkourio.command.reload");

        manager.command(builder
                .literal("config")
                .handler(this::reloadConfig)
        );
    }

    public void handle(CommandContext<CommandSender> ctx) {}

    private void reloadConfig(CommandContext<CommandSender> ctx) {
        Config.reload();
        ctx.sender().sendMessage(MiniMessage.miniMessage().deserialize(
            "<green>Конфиг перезагружен!</green>"
        ));
    }
}
