package ru.dfhub.parkourio.components.parkour;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.json.JSONObject;
import ru.dfhub.parkourio.ParkourIO;
import ru.dfhub.parkourio.components.parkour_level.ParkourLevels;
import ru.dfhub.parkourio.util.Metadata;
import ru.dfhub.parkourio.util.TimeUtil;

import java.util.HashMap;
import java.util.Map;

public class ParkourHandler implements Listener {

    private final Map<String, Long> cooldown = new HashMap<>();

    int id;

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        if (!e.getPlayer().hasMetadata(Metadata.ON_PARKOUR_LEVEL.value())) return;

        int level = e.getPlayer().getMetadata(Metadata.ON_PARKOUR_LEVEL.value()).getFirst().asInt();
        Location location = e.getTo();

        // Check for fall
        if (location.getBlockY() <= ParkourLevels.getLevelById(level).getFallLevel()) {
            //e.getPlayer().teleport(ParkourLevels.getLevelById(level).getSpawn()); // TODO: In ftr - checkpoint (from metadata)
            handleCheckpointTeleport(e.getPlayer());
            cooldown.remove(e.getPlayer().getName());
        }

        Location locationHead = location.clone(); locationHead.setY(location.getBlockY() + 1);

        if (location.getBlock().getType() != Material.STRUCTURE_VOID && locationHead.getBlock().getType() != Material.STRUCTURE_VOID) return;

        // Check for start
        if (ParkourLevels.getLevelById(level).isStart(location)) {
            handleStart(e.getPlayer());
        }

        // Check for end
        if (ParkourLevels.getLevelById(level).isEnd(location)) {
            handleEnd(e.getPlayer());
        }

        if ((id = ParkourLevels.getLevelById(level).isCheckpoint(location)) != -1) {
            handleCheckpointReach(e.getPlayer(), id,false);
        }
    }

    private void handleStart(Player p) {
        if (!checkCooldown(p)) return;
        p.setMetadata(Metadata.STARTED_AT.value(), new FixedMetadataValue(ParkourIO.getInstance(), System.currentTimeMillis()));
        p.sendMessage(MiniMessage.miniMessage().deserialize(
                "<green>Вы начали паркур!</green>"
        ));
        handleCheckpointReach(p, -1,true);
    }

    private void handleEnd(Player p) {
        if (!p.hasMetadata(Metadata.STARTED_AT.value())) return;
        if (!checkCooldown(p)) return;

        p.sendMessage(MiniMessage.miniMessage().deserialize(
                "<green>Вы закончили паркур за <aqua>%s</aqua>!</green>".formatted(
                        TimeUtil.formatTime(System.currentTimeMillis() - p.getMetadata(Metadata.STARTED_AT.value()).getFirst().asLong())
                )
        ));

        p.setMetadata(Metadata.CHECKPOINT.value(), new FixedMetadataValue(ParkourIO.getInstance(), -1));
        p.removeMetadata(Metadata.STARTED_AT.value(), ParkourIO.getInstance());
    }

    private void handleCheckpointTeleport(Player p) {
        if (!checkCooldown(p)) return;

        if (!p.hasMetadata(Metadata.CHECKPOINT.value())) {
            p.teleport(ParkourLevels.getLevelById(
                    p.getMetadata(Metadata.ON_PARKOUR_LEVEL.value()).getFirst().asInt()
            ).getSpawn());
            return;
        }

        p.teleport(
                ParkourLevels.getLevelById(p.getMetadata(Metadata.ON_PARKOUR_LEVEL.value()).getFirst().asInt())
                                .getCheckpointById(p.getMetadata(Metadata.CHECKPOINT.value()).getFirst().asInt())

        );
    }

    private void handleCheckpointReach(Player p, int id, boolean noMessage) {
        if (!checkCooldown(p)) return;

        p.setMetadata(Metadata.CHECKPOINT.value(), new FixedMetadataValue(ParkourIO.getInstance(), id));

        if (noMessage) return;
        p.sendMessage(MiniMessage.miniMessage().deserialize(
                "<green>Вы дошли до чекпоинта за <aqua>%s</aqua>!</green>".formatted(
                        TimeUtil.formatTime(System.currentTimeMillis() - p.getMetadata(Metadata.STARTED_AT.value()).getFirst().asLong())
                )
        ));
    }

    private boolean checkCooldown(Player player) {
        if (!cooldown.containsKey(player.getName())) {
            cooldown.put(player.getName(), System.currentTimeMillis());
            return true;
        };
        if (System.currentTimeMillis() - cooldown.get(player.getName()) > 2 * 1000) {
            cooldown.put(player.getName(), System.currentTimeMillis());
            return true;
        }
        return false;
    }
}
