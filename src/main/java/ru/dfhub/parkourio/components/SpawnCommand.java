package ru.dfhub.parkourio.components;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.paper.LegacyPaperCommandManager;
import org.json.JSONObject;
import ru.dfhub.parkourio.ParkourIO;
import ru.dfhub.parkourio.components.spawn.SpawnHandler;
import ru.dfhub.parkourio.util.CloudCommand;
import ru.dfhub.parkourio.util.Config;
import ru.dfhub.parkourio.util.Metadata;

public class SpawnCommand implements CloudCommand {
    @Override
    public void register(LegacyPaperCommandManager<CommandSender> manager) {
        manager.command(manager
                .commandBuilder("spawn")
                .permission("ru.dfhub.parkourio.command.spawn")
                .handler(this::handle)
        );
    }

    @Override
    public void handle(CommandContext<CommandSender> ctx) {
        if (!(ctx.sender() instanceof Player player)) return;

        player.removeMetadata(Metadata.ON_PARKOUR_LEVEL.value(), ParkourIO.getInstance());
        player.removeMetadata(Metadata.STARTED_AT.value(), ParkourIO.getInstance());
        player.removeMetadata(Metadata.CHECKPOINT.value(), ParkourIO.getInstance());

        SpawnHandler.handleTeleport(player);
    }

    @Deprecated
    /**
     * @deprecated Больше не используется
     */
    private Location getSpawnLocation() {
        JSONObject data = Config.getConfig().getJSONObject("spawn-location");
        return new Location(
                Bukkit.getWorld(data.optString("world", "world")),
                data.optDouble("x", 0),
                data.optDouble("y", 0),
                data.optDouble("z", 0),
                data.optFloat("yaw", 0.0F),
                data.optFloat("pitch", 0.0F)
        );
    }
}
