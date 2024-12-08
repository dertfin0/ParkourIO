package ru.dfhub.parkourio;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import static org.bukkit.Bukkit.getPluginManager;

public final class ParkourIO extends JavaPlugin {

    private static ParkourIO instance;

    @Override
    public void onEnable() {
        instance = this;
        registerEvents();
    }

    @Override
    public void onDisable() {
    }

    public static ParkourIO getInstance() {
        return instance;
    }

    private void registerEvents(Listener ...listeners) {
        for (Listener listener : listeners) {
            getPluginManager().registerEvents(listener, this);
        }
    }
}
