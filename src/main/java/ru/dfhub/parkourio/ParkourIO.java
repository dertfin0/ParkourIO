package ru.dfhub.parkourio;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import ru.dfhub.parkourio.components.Chat;
import ru.dfhub.parkourio.components.JoinLeaveMessages;
import ru.dfhub.parkourio.components.MOTD;
import ru.dfhub.parkourio.components.spawn.SpawnOnJoin;
import ru.dfhub.parkourio.util.Config;

import static org.bukkit.Bukkit.getPluginManager;

public final class ParkourIO extends JavaPlugin {

    private static ParkourIO instance;

    @Override
    public void onEnable() {
        instance = this;
        Config.reload();

        registerEvents(
                new MOTD(),
                new SpawnOnJoin(),
                new JoinLeaveMessages(),
                new Chat()
        );
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
