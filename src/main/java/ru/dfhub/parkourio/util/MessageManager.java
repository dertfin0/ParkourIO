package ru.dfhub.parkourio.util;

import org.bukkit.Bukkit;
import ru.dfhub.parkourio.ParkourIO;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;

public class MessageManager {

    private static final Properties messages = new Properties();

    public static void init() {
        try {
            messages.load(new FileReader("plugins/ParkourIO/messages.properties"));
        } catch (IOException e) {
            ParkourIO.getInstance().getLogger().severe("CAN'T READ PLUGIN MESSAGES!");
            e.printStackTrace();
            Bukkit.shutdown();
        }
    }

    public static String getMessage(String key) {
        String msg = messages.getProperty(key);
        if (msg == null) {
            ParkourIO.getInstance().getLogger().log(Level.WARNING, "Requested message '%s' is not defined!".formatted(key));
            return "Can't find message '%s'".formatted(key);
        }
        return msg;
    }
}
