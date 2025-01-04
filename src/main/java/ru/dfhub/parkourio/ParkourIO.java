package ru.dfhub.parkourio;

import org.bukkit.Bukkit;
import org.bukkit.WorldCreator;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.paper.LegacyPaperCommandManager;
import ru.dfhub.parkourio.components.*;
import ru.dfhub.parkourio.components.menu.AboutMenu;
import ru.dfhub.parkourio.components.menu.ParkourMenu;
import ru.dfhub.parkourio.components.parkour.ParkourHandler;
import ru.dfhub.parkourio.components.parkour_level.ParkourLevels;
import ru.dfhub.parkourio.components.spawn.SpawnHandler;
import ru.dfhub.parkourio.util.CloudCommand;
import ru.dfhub.parkourio.util.Config;

import java.util.ServiceLoader;

import static org.bukkit.Bukkit.getPluginManager;

/**
 * Основной класс плагина
 */
public final class ParkourIO extends JavaPlugin {

    private static ParkourIO instance;
    private static LegacyPaperCommandManager<CommandSender> manager;
    private static Snow snow;

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
                new SpawnHandler(),
                new JoinLeaveMessages(),
                new Chat(),
                new TAB(),
                new ParkourHandler(),
                new DisableDamage(),
                new DisableBlockGrow(),
                new InventoryPrevent(),
                new AboutMenu.Handler(),
                new ParkourMenu.Handler()
        );
        ServiceLoader.load(CloudCommand.class).forEach(it -> {
            getSLF4JLogger().info("Trying to register CloudCommand {}", it.getClass().getSimpleName());
            try {
                it.register(manager);
                getSLF4JLogger().info("Successfully registered {}", it.getClass().getSimpleName());
            } catch (Exception e) {
                getSLF4JLogger().error("Error while registering {}: {}", it.getClass().getSimpleName(), e.getMessage());
            }
        });
        registerSpawnWorld();
        ru.dfhub.DFPaperLib.enable();

        snow = new Snow();
        Thread.ofVirtual().start(snow);
    }

    @Override
    public void onDisable() {
        snow.setStopped(true);
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
     * Зарегистрировать мир спавна
     */
    private void registerSpawnWorld() {
        Bukkit.createWorld(new WorldCreator(
                Config.getConfig().getJSONObject("spawn-location").getString("world")
        ));
    }
}
