package ru.dfhub.parkourio.components.spawn;

import org.bukkit.entity.Player;
import org.json.JSONArray;
import org.json.JSONObject;
import ru.dfhub.parkourio.util.Config;
import ru.dfhub.parkourio.util.IconItems;

public class SpawnItems {

    public static void give(Player player) {
        JSONArray items = Config.getConfig().getJSONArray("spawn-items");

        player.getInventory().clear();
        for (int i = 0; i < items.length(); i++) {
            player.getInventory().setItem(
                    items.getJSONObject(i).optInt("slot", 0),
                    IconItems.serialize(items.getJSONObject(i).optJSONObject("item", new JSONObject()))
            );
        }
    }
}
