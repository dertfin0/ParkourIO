package ru.dfhub.parkourio.components.spawn;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.json.JSONObject;
import ru.dfhub.parkourio.ParkourIO;
import ru.dfhub.parkourio.util.Config;
import ru.dfhub.parkourio.util.Metadata;

public class SpawnHandler implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent e) {
        handleTeleport(e.getPlayer());
    }

    @EventHandler
    public void onFall(PlayerMoveEvent e) {
        if (!e.getTo().getWorld().getName().equalsIgnoreCase(Config.getConfig().getJSONObject("spawn-location").getString("world"))) return;

        if (e.getTo().getBlockY() <= Config.getConfig().getJSONObject("spawn-location").getInt("fall-level")) {
            e.getPlayer().teleport(getSpawnLocation());
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        if (!e.getPlayer().hasPermission("ru.dfhub.parkourio.spawn.build")) e.setCancelled(true);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        if (!e.getPlayer().hasPermission("ru.dfhub.parkourio.spawn.build")) e.setCancelled(true);
    }

    public static void handleTeleport(Player player) {
        player.teleport(getSpawnLocation());
        SpawnItems.give(player);

        player.removeMetadata(Metadata.ON_PARKOUR_LEVEL.value(), ParkourIO.getInstance());
        player.removeMetadata(Metadata.STARTED_AT.value(), ParkourIO.getInstance());
        player.removeMetadata(Metadata.CHECKPOINT.value(), ParkourIO.getInstance());

        if (Config.getConfig().optInt("spawn-parkour-level", -1) != -1) player.setMetadata(Metadata.ON_PARKOUR_LEVEL.value(), new FixedMetadataValue(ParkourIO.getInstance(), Config.getConfig().getInt("spawn-parkour-level")));
    }

    private static Location getSpawnLocation() {
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
