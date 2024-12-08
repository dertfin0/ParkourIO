package ru.dfhub.parkourio.util;

import org.bukkit.Bukkit;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;

public class Config {

    private static final String CONFIG_PATH = "plugins/ParkourIO/config.json";

    private static JSONObject getConfig() {
        try {
            return new JSONObject(Files.readString(Paths.get(CONFIG_PATH)));
        } catch (IOException e) {
            Bukkit.getLogger().log(Level.WARNING, "CAN'T READ MAIN PLUGIN CONFIG!");
            e.printStackTrace();
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "stop");
        }
        return null;
    }
}
