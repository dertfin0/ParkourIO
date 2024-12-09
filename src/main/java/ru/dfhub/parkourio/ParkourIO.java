package ru.dfhub.parkourio;

import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.paper.LegacyPaperCommandManager;
import ru.dfhub.parkourio.components.Chat;
import ru.dfhub.parkourio.components.JoinLeaveMessages;
import ru.dfhub.parkourio.components.MOTD;
import ru.dfhub.parkourio.components.TAB;
import ru.dfhub.parkourio.components.spawn.SpawnOnJoin;
import ru.dfhub.parkourio.util.Config;

import static org.bukkit.Bukkit.getPluginManager;

public final class ParkourIO extends JavaPlugin {

    private static ParkourIO instance;
    private static LegacyPaperCommandManager<CommandSender> manager;

    @Override
    public void onEnable() {
        instance = this;
        manager = LegacyPaperCommandManager.createNative(
                this,
                ExecutionCoordinator.simpleCoordinator()
        );

        Config.reload();
        registerEvents(
                new MOTD(),
                new SpawnOnJoin(),
                new JoinLeaveMessages(),
                new Chat(),
                new TAB()
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
