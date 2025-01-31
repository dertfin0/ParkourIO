package ru.dfhub.parkourio;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.WorldCreator;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.paper.LegacyPaperCommandManager;
import ru.dfhub.parkourio.common.Database;
import ru.dfhub.parkourio.common.entity.Punishment;
import ru.dfhub.parkourio.components.*;
import ru.dfhub.parkourio.components.menu.AboutMenu;
import ru.dfhub.parkourio.components.menu.ParkourMenu;
import ru.dfhub.parkourio.components.parkour.ParkourCommand;
import ru.dfhub.parkourio.components.parkour.ParkourHandler;
import ru.dfhub.parkourio.components.parkour.ParkourItems;
import ru.dfhub.parkourio.components.parkour_level.ParkourLevels;
import ru.dfhub.parkourio.components.punishments.FakeKick;
import ru.dfhub.parkourio.components.punishments.Kick;
import ru.dfhub.parkourio.components.punishments.ban.Ban;
import ru.dfhub.parkourio.components.punishments.ban.UnBan;
import ru.dfhub.parkourio.components.punishments.mute.Mute;
import ru.dfhub.parkourio.components.punishments.mute.MuteCache;
import ru.dfhub.parkourio.components.punishments.mute.ShowMutedMsg;
import ru.dfhub.parkourio.components.punishments.mute.UnMute;
import ru.dfhub.parkourio.components.spawn.SpawnHandler;
import ru.dfhub.parkourio.util.CloudCommand;
import ru.dfhub.parkourio.util.Config;
import ru.dfhub.parkourio.util.MessageManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.bukkit.Bukkit.getPluginManager;

/**
 * Основной класс плагина
 */
public final class ParkourIO extends JavaPlugin {

    @Getter
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
                new ParkourMenu.Handler(),
                new ParkourItems.Handler(),
                new Mute.Handler(),
                new Ban.Handler(),
                new MuteCache.Handler()
        );
        registerCommands(
                new PReloadCommand(),
                new WorldCommand(),
                new MetadataUtilCommand(),
                new ParkourCommand(),
                new SpawnCommand(),
                new Mute(),
                new UnMute(),
                new Ban(),
                new UnBan(),
                new ClearChat(),
                new MetadataUtilE(),
                new ShowMutedMsg(),
                new FakeKick(),
                new Kick()
        );
        registerSpawnWorld();
        ru.dfhub.DFPaperLib.enable();

        snow = new Snow();
        ExecutorService es = Executors.newVirtualThreadPerTaskExecutor();
        //es.submit(new ParticleManager());
        es.submit(snow);
        Database.init(Punishment.class);
        MessageManager.init();
    }

    @Override
    public void onDisable() {
        snow.setStopped(true);
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

    /**
     * Зарегистрировать мир спавна
     */
    private void registerSpawnWorld() {
        Bukkit.createWorld(new WorldCreator(
                Config.getConfig().getJSONObject("spawn-location").getString("world")
        ));
    }
}
