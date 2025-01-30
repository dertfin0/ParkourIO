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

import static ru.dfhub.parkourio.util.MessageManager.getMessage;

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

    private void handleTeleport(CommandContext<CommandSender> ctx) {
        if (!(ctx.sender() instanceof Player player)) return;

        ParkourLevel level = ParkourLevels.getLevelById(ctx.getOrDefault("id", 9999999));
        if (level == null) {
            ctx.sender().sendMessage(MiniMessage.miniMessage().deserialize(
                    getMessage("command.parkour.not-found")
            ));
            return;
        }

        // If teleport user from other parkour level
        player.removeMetadata(Metadata.ON_PARKOUR_LEVEL.value(), ParkourIO.getInstance());
        player.removeMetadata(Metadata.STARTED_AT.value(), ParkourIO.getInstance());
        player.removeMetadata(Metadata.CHECKPOINT.value(), ParkourIO.getInstance());

        player.setMetadata(Metadata.ON_PARKOUR_LEVEL.value(), new FixedMetadataValue(ParkourIO.getInstance(), ctx.get("id")));
        player.teleport(level.getSpawn());
        ParkourItems.give(player);
    }
}
