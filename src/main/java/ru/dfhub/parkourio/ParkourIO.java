package ru.dfhub.parkourio;

import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.paper.LegacyPaperCommandManager;
import ru.dfhub.parkourio.components.*;
import ru.dfhub.parkourio.components.parkour_level.ParkourLevels;
import ru.dfhub.parkourio.components.spawn.SpawnOnJoin;
import ru.dfhub.parkourio.util.CloudCommand;
import ru.dfhub.parkourio.util.Config;

import static org.bukkit.Bukkit.getPluginManager;

/**
 * Основной класс плагина
 */
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
        ParkourLevels.reload();
        registerEvents(
                new MOTD(),
                new SpawnOnJoin(),
                new JoinLeaveMessages(),
                new Chat(),
                new TAB()
        );
        registerCommands(
                new PReloadCommand(),
                new WorldCommand(),
                new MetadataUtilCommand()
        );
    }

    @Override
    public void onDisable() {
    }

    /**
     * Получить экземпляр класса плагина
     * @return ParkourIO Plugin
     */
    public static ParkourIO getInstance() {
        return instance;
    }

    /**
     * Зарегистрировать мнонежство ивентов (слушателей)
     * @param listeners Экземпляры Listener
     */
    private void registerEvents(Listener ...listeners) {
        for (Listener listener : listeners) {
            getPluginManager().registerEvents(listener, this);
        }
    }

    /**
     * Зарагистрировать множество Cloud-команд
     * @param commands Cloud-команды
     */
    private void registerCommands(CloudCommand ...commands) {
        for (CloudCommand command : commands) {
            command.register(manager);
        }
    }
}
