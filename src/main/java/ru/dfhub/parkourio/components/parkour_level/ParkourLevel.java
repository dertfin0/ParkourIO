package ru.dfhub.parkourio.components.parkour_level;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.WorldCreator;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.json.JSONObject;

/**
 * Уровень с паркуром. Содержит всю информаци о уровне
 */
public class ParkourLevel {

    private final int id;
    private final Location spawn;
    private final int reward;
    private final ItemStack icon;
    private final int fallLevel;

    public ParkourLevel(JSONObject object) {
        id = object.getInt("id");
        reward = object.optInt("reward", 0);
        fallLevel = object.getInt("fall-level");

        JSONObject spawnJson = object.getJSONObject("spawn");
        createWorld(spawnJson.getString("world")); // Загрузка мира для дальнейшей работы
        spawn = new Location(
                Bukkit.getWorld(spawnJson.getString("world")),
                spawnJson.getDouble("x"),
                spawnJson.getDouble("y"),
                spawnJson.getDouble("z"),
                spawnJson.optFloat("yaw", 0f),
                spawnJson.optFloat("pitch", 0f)
        );
        icon = getIcon(object.getJSONObject("icon-item"));
    }

    private ItemStack getIcon(JSONObject iconItemJson) {
        ItemStack icon = new ItemStack(
                Material.getMaterial(iconItemJson.optString("material", "STONE").toUpperCase()),
                iconItemJson.optInt("amount", 1)
        );
        ItemMeta iconMeta = icon.getItemMeta();
        iconMeta.displayName(MiniMessage.miniMessage().deserialize(iconItemJson.getString("name")));
        iconMeta.lore(
                iconItemJson.getJSONArray("lore").toList()
                        .stream()
                        .map(obj -> (String) obj)
                        .map(str -> MiniMessage.miniMessage().deserialize(str))
                        .toList()
        );

        iconMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

        if (iconItemJson.optBoolean("enchanted", false)) {
            iconMeta.addEnchant(Enchantment.KNOCKBACK, 1, true);
            iconMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        icon.setItemMeta(iconMeta);

        return icon;
    }

    private static void createWorld(String name) {
        Bukkit.createWorld(new WorldCreator(name));
    }

    public int getId() {
        return id;
    }

    public Location getSpawn() {
        return spawn;
    }

    public int getReward() {
        return reward;
    }

    public ItemStack getIcon() {
        return icon;
    }

    public int getFallLevel() {
        return fallLevel;
    }
}
