package ru.dfhub.parkourio.components.parkour;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.incendo.cloud.Command;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.paper.LegacyPaperCommandManager;
import org.incendo.cloud.parser.standard.IntegerParser;
import ru.dfhub.parkourio.ParkourIO;
import ru.dfhub.parkourio.components.SpawnCommand;
import ru.dfhub.parkourio.components.parkour_level.ParkourLevel;
import ru.dfhub.parkourio.components.parkour_level.ParkourLevels;
import ru.dfhub.parkourio.util.CloudCommand;
import ru.dfhub.parkourio.util.Metadata;

public class ParkourCommand implements CloudCommand {
    @Override
    public void register(LegacyPaperCommandManager<CommandSender> manager) {
        Command.Builder<CommandSender> builder = manager.commandBuilder("parkour").permission("ru.dfhub.parkourio.command.parkour");

        manager.command(builder
                .literal("tp")
                .required("id", IntegerParser.integerParser(0, ParkourLevels.getLevels().size() - 1))
                .handler(this::handleTeleport)
        );
        manager.command(builder
                .literal("leave")
                .handler(new SpawnCommand()::handle)
        );
    }

    @Override
    public void handle(CommandContext<CommandSender> ctx) {

    }

    private void handleTeleport(CommandContext<CommandSender> ctx) {
        if (!(ctx.sender() instanceof Player player)) return;

        ParkourLevel level = ParkourLevels.getLevelById(ctx.getOrDefault("id", 9999999));
        if (level == null) {
            ctx.sender().sendMessage(MiniMessage.miniMessage().deserialize(
                    "<red>Уровень не найден!</red>"
            ));
            return;
        }

        player.setMetadata(Metadata.ON_PARKOUR_LEVEL.value(), new FixedMetadataValue(ParkourIO.getInstance(), ctx.get("id")));
        player.teleport(level.getSpawn());
    }
}
