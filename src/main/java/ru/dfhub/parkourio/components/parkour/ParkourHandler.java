package ru.dfhub.parkourio.components.parkour;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.metadata.FixedMetadataValue;
import ru.dfhub.parkourio.ParkourIO;
import ru.dfhub.parkourio.components.parkour_level.ParkourLevels;
import ru.dfhub.parkourio.util.Metadata;

import java.util.HashMap;
import java.util.Map;

public class ParkourHandler implements Listener {

    private final Map<String, Long> cooldown = new HashMap<>();

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        if (!e.getPlayer().hasMetadata(Metadata.ON_PARKOUR_LEVEL.value())) return;

        int level = e.getPlayer().getMetadata(Metadata.ON_PARKOUR_LEVEL.value()).getFirst().asInt();
        Location location = e.getTo();

        // Check for fall
        if (location.getBlockY() <= ParkourLevels.getLevelById(level).getFallLevel()) {
            e.getPlayer().teleport(ParkourLevels.getLevelById(level).getSpawn()); // TODO: In ftr - checkpoint (from metadata)
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
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if (e.getEntity().getType() != EntityType.PLAYER) return;
        e.setCancelled(true);
    }

    private void handleStart(Player p) {
        if (!checkCooldown(p)) return;
        p.setMetadata(Metadata.STARTED_AT.value(), new FixedMetadataValue(ParkourIO.getInstance(), System.currentTimeMillis()));
        p.sendMessage(MiniMessage.miniMessage().deserialize(
                "<green>Вы начали паркур!</green>"
        ));
    }

    private void handleEnd(Player p) {
        if (!p.hasMetadata(Metadata.STARTED_AT.value())) return;
        if (!checkCooldown(p)) return;

        long time = System.currentTimeMillis() - p.getMetadata(Metadata.STARTED_AT.value()).getFirst().asLong();
        int seconds = (int) (time / 1000);
        int ms = (int) (time - seconds * 1000);
        p.sendMessage(MiniMessage.miniMessage().deserialize(
                "<green>Вы закончили паркур за <aqua>%s.%sms</aqua>!</green>"
                        .formatted(seconds, String.valueOf(ms).substring(0, 2))
        ));

        p.removeMetadata(Metadata.STARTED_AT.value(), ParkourIO.getInstance());
    }

    private boolean checkCooldown(Player player) {
        if (!cooldown.containsKey(player.getName())) {
            cooldown.put(player.getName(), System.currentTimeMillis());
            return true;
        };
        if (System.currentTimeMillis() - cooldown.get(player.getName()) > 3 * 1000) {
            cooldown.put(player.getName(), System.currentTimeMillis());
            return true;
        }
        return false;
    }
}
