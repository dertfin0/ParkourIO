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
import ru.dfhub.parkourio.util.IconItems;

import java.util.ArrayList;
import java.util.List;

/**
 * Уровень с паркуром. Содержит всю информаци о уровне
 */
public class ParkourLevel {

    private final int id;
    private final Location spawn;
    private final int reward;
    private final ItemStack icon;
    private final int fallLevel;
    private final JSONObject start, end;
    private final List<JSONObject> checkpoints = new ArrayList<>();

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
        icon = IconItems.serialize(object.getJSONObject("icon-item"));
        start = object.getJSONObject("start");
        end = object.getJSONObject("end");
        for (int i = 0; i < object.getJSONArray("checkpoints").length(); i++) {
            checkpoints.addLast(object.getJSONArray("checkpoints").getJSONObject(i));
        }
    }

    @Deprecated
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

    public JSONObject getStart() {
        return start;
    }

    public boolean isStart(Location location) {
        return (int) location.getX() == start.getInt("x") &&
                (int) location.getY() == start.getInt("y") &&
                (int) location.getZ() == start.getInt("z");
    }

    public JSONObject getEnd() {
        return end;
    }

    public boolean isEnd(Location location) {
        return (int) location.getX() == end.getInt("x") &&
                (int) location.getY() == end.getInt("y") &&
                (int) location.getZ() == end.getInt("z");
    }

    public List<JSONObject> getCheckpoints() {
        return checkpoints;
    }


    /**
     * Узнать, является ли Location чекпоинтом и получить его id
     * @param location Location
     * @return ID чекпоинта или -1, если такой чекпоинт не найден
     */
    public int isCheckpoint(Location location) {
        int x = (int) location.getX();
        int y = (int) location.getY();
        int z = (int) location.getZ();
        for (int i = 0; i < checkpoints.size(); i++) {
            JSONObject checkpoint = checkpoints.get(i);
            if (
                    x == checkpoint.getInt("x") &&
                    y == checkpoint.getInt("y") &&
                    z == checkpoint.getInt("z")
            ) return i;
        }
        return -1;
    }

    public Location getCheckpointById(int id) {
        if (id == -1) return spawn;
        if (id > checkpoints.size() - 1) return spawn;


        JSONObject checkpoint = checkpoints.get(id);
        return new Location(
                Bukkit.getWorld(checkpoint.getString("world")),
                checkpoint.getDouble("x"),
                checkpoint.getDouble("y"),
                checkpoint.getDouble("z"),
                checkpoint.getFloat("yaw"),
                checkpoint.getFloat("pitch")
        );
    }
}
