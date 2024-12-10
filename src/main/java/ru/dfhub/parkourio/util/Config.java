package ru.dfhub.parkourio.util;

import org.bukkit.Bukkit;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Level;

/**
 * Класс для работы с конфигурацией<br>
 * Загружает, хранит актуальный конфиг и перезагружает его
 */
public class Config {

    private static final String CONFIG_PATH = "plugins/ParkourIO/config.json";

    private static JSONObject config;

    /**
     * Получить актуальный конфиг
     * @return Конфиг
     */
    public static JSONObject getConfig() {
        return config;
    }

    /**
     * Перезагрузить конфиг
     */
    public static void reload() {
        config = readConfig();
    }

    /**
     * Прочитать конфиг из файла и сереализовать его в JSON<br>
     * Выполнение без ошибок обязательно для работы плагина
     * @return
     */
    private static JSONObject readConfig() {
        try {
            return new JSONObject(Files.readString(Paths.get(CONFIG_PATH)));
        } catch (IOException e) {
            Bukkit.getLogger().log(Level.WARNING, "CAN'T READ MAIN PLUGIN CONFIG!");
            e.printStackTrace();
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "stop");
        }
        return null;
    }

    public static List<String> getStringList(JSONArray jsonArray) {
        return jsonArray.toList().stream()
                .map(obj -> (String) obj)
                .toList();
    }
}
