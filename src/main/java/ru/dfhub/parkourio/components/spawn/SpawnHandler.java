package ru.dfhub.parkourio.components.spawn;

import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.json.JSONObject;
import ru.dfhub.parkourio.ParkourIO;
import ru.dfhub.parkourio.components.menu.ParkourMenu;
import ru.dfhub.parkourio.components.parkour.ParkourItems;
import ru.dfhub.parkourio.util.Config;
import ru.dfhub.parkourio.util.Metadata;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static net.kyori.adventure.text.minimessage.MiniMessage.miniMessage;

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

    @EventHandler
    public void onCloseToSpawnParkour(PlayerMoveEvent e) {
        if (e.getPlayer().hasMetadata(Metadata.ON_PARKOUR_LEVEL.value())) return;
        if (e.getTo().getWorld().getName().equals(getSpawnLocation().getWorld().getName()) &&
                e.getTo().getBlockY() == 7 &&
                e.getTo().getBlockX() <= -16 && e.getTo().getBlockX() >= -18 &&
                e.getTo().getBlockZ() <= -21 && e.getTo().getBlockZ() >= -23
        ) {
            e.getPlayer().setMetadata(Metadata.ON_PARKOUR_LEVEL.value(), new FixedMetadataValue(ParkourIO.getInstance(), Config.getConfig().getInt("spawn-parkour-level")));
            ParkourItems.give(e.getPlayer());
        }
    }

    @EventHandler
    public void onTalkWithHelper(PlayerInteractAtEntityEvent e) {
        if (!e.getPlayer().getWorld().getName().equals(getSpawnLocation().getWorld().getName())) return;
        if(e.getRightClicked().getLocation().getBlockX() != 20 || e.getRightClicked().getLocation().getBlockZ() != -4) return;
        e.setCancelled(true);
        CompletableFuture.runAsync(() -> {
            try {
                e.getPlayer().sendMessage(miniMessage().deserialize("<yellow>> Добро пожаловать на <gradient:#7733ff:#b82bff><b>ParkourIO</b></gradient>!</yellow>"));
                Thread.sleep(1250);
                e.getPlayer().sendMessage(miniMessage().deserialize("<yellow>> Здесь вы можете отточить свои навыки паркура, играя на разных уровнях - от простых, рассчитаных на конкретный навык, до длинных сложных уровней, которые собирают в себе самые сложные прыжки.</yellow>"));
                Thread.sleep(4000);
                e.getPlayer().sendMessage(miniMessage().deserialize("<yellow>> Пройти ознакомительный паркур можно, пройдя налево или нажав <click:run_command:'/parkour tp 0'><aqua>сюда</aqua></click>.</yellow>"));
            } catch (InterruptedException ex) {}
        });
    }

    @EventHandler
    public void onNeedCloseDefaultInventory(InventoryOpenEvent e) {
        if (List.of(
                "Помощник"
        ).contains(((TextComponent) e.getView().title()).content())) {
            e.setCancelled(true);
        }
    }

    public static void handleTeleport(Player player) {
        player.teleport(getSpawnLocation());
        SpawnItems.give(player);

        player.removeMetadata(Metadata.ON_PARKOUR_LEVEL.value(), ParkourIO.getInstance());
        player.removeMetadata(Metadata.STARTED_AT.value(), ParkourIO.getInstance());
        player.removeMetadata(Metadata.CHECKPOINT.value(), ParkourIO.getInstance());

        // Игрок при телепортации на спавн автоматически получает метадату, что он на спавновом паркуре
        //if (Config.getConfig().optInt("spawn-parkour-level", -1) != -1) player.setMetadata(Metadata.ON_PARKOUR_LEVEL.value(), new FixedMetadataValue(ParkourIO.getInstance(), Config.getConfig().getInt("spawn-parkour-level")));
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
