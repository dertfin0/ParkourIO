package ru.dfhub.parkourio.components;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.incendo.cloud.Command;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.paper.LegacyPaperCommandManager;
import org.incendo.cloud.parser.standard.StringParser;
import ru.dfhub.parkourio.ParkourIO;
import ru.dfhub.parkourio.util.CloudCommand;

/**
 * Команда-утилита для работы с метадатой(-ами)
 */
public class MetadataUtilCommand implements CloudCommand {

    @Override
    public void register(LegacyPaperCommandManager<CommandSender> manager) {
        Command.Builder<CommandSender> builder = manager.commandBuilder("metadata-util").permission("ru.dfhub.parkourio.command.metadata-util");

        manager.command(builder
                .literal("get")
                .required("name", StringParser.stringParser())
                .handler(this::handleGet)
        );
        manager.command(builder
                .literal("set")
                .required("name", StringParser.stringParser())
                .required("value", StringParser.greedyFlagYieldingStringParser())
                .handler(this::handleSet)
        );
        manager.command(builder
                .literal("remove")
                .required("name", StringParser.stringParser())
                .handler(this::handleRemove)
        );
    }

    private void handleGet(CommandContext<CommandSender> ctx) {
        if (!(ctx.sender() instanceof Player player)) return;

        if (!player.hasMetadata(ctx.get("name"))) {
            player.sendMessage(MiniMessage.miniMessage().deserialize(
                    "<red>Указанная метадата отсуствует</red>"
            ));
            return;
        }

        StringBuilder valueBuilder = new StringBuilder();
        int values = 0;
        for (MetadataValue value : player.getMetadata(ctx.get("name"))) {
            valueBuilder.append(value.asString()).append(", ");
            values++;
        }

        player.sendMessage(MiniMessage.miniMessage().deserialize(
                "<green>Найдено <blue>%s</blue> значений: <yellow>%s</yellow>.</green>"
                        .formatted(
                                values,
                                valueBuilder.substring(0, valueBuilder.length() - 2)
                        )
        ));
    }

    private void handleSet(CommandContext<CommandSender> ctx) {
        if (!(ctx.sender() instanceof Player player)) return;

        player.setMetadata(ctx.get("name"), new FixedMetadataValue(ParkourIO.getInstance(), ctx.get("value")));
        player.sendMessage(MiniMessage.miniMessage().deserialize(
                "<green>Метадата установлена!</green>"
        ));
    }

    private void handleRemove(CommandContext<CommandSender> ctx) {
        if (!(ctx.sender() instanceof Player player)) return;

        if (!player.hasMetadata(ctx.get("name"))) {
            player.sendMessage(MiniMessage.miniMessage().deserialize(
                    "<red>Указанная метадата отсуствует</red>"
            ));
            return;
        }

        player.removeMetadata(ctx.get("name"), ParkourIO.getInstance());
        player.sendMessage(MiniMessage.miniMessage().deserialize(
                "<green>Метадата удалена!</green>"
        ));
    }
}
