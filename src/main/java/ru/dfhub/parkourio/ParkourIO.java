package ru.dfhub.parkourio;

import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.paper.LegacyPaperCommandManager;
import ru.dfhub.parkourio.components.*;
import ru.dfhub.parkourio.components.spawn.SpawnOnJoin;
import ru.dfhub.parkourio.util.CloudCommand;
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
        registerCommands(
                new PReloadCommand(),
                new WorldCommand()
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

    private void registerCommands(CloudCommand ...commands) {
        for (CloudCommand command : commands) {
            command.register(manager);
        }
    }
}
