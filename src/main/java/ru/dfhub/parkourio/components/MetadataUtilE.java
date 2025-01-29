package ru.dfhub.parkourio.components;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.incendo.cloud.Command;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.paper.LegacyPaperCommandManager;
import org.incendo.cloud.parser.standard.EnumParser;
import org.incendo.cloud.parser.standard.StringParser;
import ru.dfhub.parkourio.ParkourIO;
import ru.dfhub.parkourio.util.CloudCommand;
import ru.dfhub.parkourio.util.Metadata;

/**
 * Команда-утилита для работы с метадатой(-ами),
 * использует enum Metadata для получения зазвания
 */
public class MetadataUtilE implements CloudCommand {

    @Override
    public void register(LegacyPaperCommandManager<CommandSender> manager) {
        Command.Builder<CommandSender> builder = manager.commandBuilder("metadata-util-e").permission("ru.dfhub.parkourio.command.metadata-util");

        manager.command(builder
                .literal("get")
                .required("name", EnumParser.enumParser(Metadata.class))
                .handler(this::handleGet)
        );
        manager.command(builder
                .literal("set")
                .required("name", EnumParser.enumParser(Metadata.class))
                .required("value", StringParser.greedyFlagYieldingStringParser())
                .handler(this::handleSet)
        );
        manager.command(builder
                .literal("remove")
                .required("name", EnumParser.enumParser(Metadata.class))
                .handler(this::handleRemove)
        );
    }

    private void handleGet(CommandContext<CommandSender> ctx) {
        if (!(ctx.sender() instanceof Player player)) return;

        Metadata meta = ctx.get("name");

        if (!player.hasMetadata(meta.value())) {
            player.sendMessage(MiniMessage.miniMessage().deserialize(
                    "<red>Указанная метадата отсуствует</red>"
            ));
            return;
        }

        StringBuilder valueBuilder = new StringBuilder();
        int values = 0;
        for (MetadataValue value : player.getMetadata(meta.value())) {
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
        Metadata meta = ctx.get("name");

        player.setMetadata(meta.value(), new FixedMetadataValue(ParkourIO.getInstance(), ctx.get("value")));
        player.sendMessage(MiniMessage.miniMessage().deserialize(
                "<green>Метадата установлена!</green>"
        ));
    }

    private void handleRemove(CommandContext<CommandSender> ctx) {
        if (!(ctx.sender() instanceof Player player)) return;
        Metadata meta = ctx.get("name");

        if (!player.hasMetadata(meta.value())) {
            player.sendMessage(MiniMessage.miniMessage().deserialize(
                    "<red>Указанная метадата отсуствует</red>"
            ));
            return;
        }

        player.removeMetadata(meta.value(), ParkourIO.getInstance());
        player.sendMessage(MiniMessage.miniMessage().deserialize(
                "<green>Метадата удалена!</green>"
        ));
    }
}
