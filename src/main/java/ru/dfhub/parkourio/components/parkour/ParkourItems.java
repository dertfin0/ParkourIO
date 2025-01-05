package ru.dfhub.parkourio.components.parkour;

import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.json.JSONArray;
import org.json.JSONObject;
import ru.dfhub.parkourio.components.spawn.SpawnHandler;
import ru.dfhub.parkourio.util.Config;
import ru.dfhub.parkourio.util.IconItems;

public class ParkourItems {

    public static void give(Player player) {
        JSONArray items = Config.getConfig().getJSONArray("parkour-items");

        player.getInventory().clear();
        for (int i = 0; i < items.length(); i++) {
            player.getInventory().setItem(
                    items.getJSONObject(i).optInt("slot", 0),
                    IconItems.serialize(items.getJSONObject(i).optJSONObject("item", new JSONObject()))
            );
        }
        player.getInventory().setHeldItemSlot(1); // Set selected hotbar slot to 2
    }

    public static class Handler implements Listener {

        @EventHandler
        public void onInteract(PlayerInteractEvent e) {
            if (e.getAction() != Action.RIGHT_CLICK_AIR && e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
            if (!(e.getPlayer().getInventory().getItemInMainHand() instanceof ItemStack item)) return;
            PlainTextComponentSerializer serializer = PlainTextComponentSerializer.builder().build();

            switch (serializer.serialize(item.displayName())) {
                case "[На спавн]" -> SpawnHandler.handleTeleport(e.getPlayer());
                case "[На чекпоинт]" -> ParkourHandler.handleCheckpointTeleport(e.getPlayer());
                case "[На старт]" -> {
                    ParkourHandler.setCheckpoint(e.getPlayer(), -1); // -1 = parkour start
                    ParkourHandler.handleCheckpointTeleport(e.getPlayer());
                }
            }
        }
    }
}
