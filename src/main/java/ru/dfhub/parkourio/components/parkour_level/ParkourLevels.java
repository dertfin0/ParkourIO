package ru.dfhub.parkourio.components.parkour_level;

import org.bukkit.Bukkit;
import org.json.JSONArray;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * Утилита для работы с уровнями
 */
public class ParkourLevels {

    private static final List<ParkourLevel> parkourLevels = new ArrayList<>(); // Загруженные сейчас уровни

    /**
     * Перезагрузить список уровней
     */
    public static void reload() {
        JSONArray levelsJson = new JSONArray(readLevelsFile());
        for (int i = 0; i < levelsJson.length(); i++) {
            Bukkit.getLogger().log(Level.INFO, "Loading %s parkour-level".formatted(i));
            parkourLevels.add(new ParkourLevel(levelsJson.getJSONObject(i)));
        }
    }

    /**
     * Получить уровень по id (УСЛОВНОМУ)
     * @param id ID (порядок в json-фалйе)
     * @return Паркур-уровень
     */
    public static ParkourLevel getLevelById(int id) {
        return parkourLevels.get(id);
    }

    private static String readLevelsFile() {
        try {
            return Files.readString(Paths.get("plugins/ParkourIO/parkour-levels.json"));
        } catch (Exception e) {
            Bukkit.getLogger().log(Level.WARNING, "CAN'T READ MAIN PLUGIN CONFIG!");
            e.printStackTrace();
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "stop");
        }
        return "[]";
    }

}
