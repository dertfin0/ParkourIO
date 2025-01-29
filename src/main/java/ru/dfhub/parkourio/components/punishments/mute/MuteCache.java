package ru.dfhub.parkourio.components.punishments.mute;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import ru.dfhub.parkourio.common.ParkourPlayer;
import ru.dfhub.parkourio.common.entity.Punishment;

import java.util.HashMap;
import java.util.Map;

/**
 * Кэширование мутов игроков для обеспечения быстрой
 * отправки сообщений в чат, без необходимости
 * ожидать ответ БД
 */
public class MuteCache {

    private static final Map<String, Punishment> muteCache = new HashMap<>();

    /**
     * Имеет ли игрок активный мут
     * @param player Имя игрока
     * @return Есть ли активный мут у игрока
     */
    public static boolean isMuted(String player) {
        if (muteCache.get(player) == null) return false;
        return muteCache.get(player).isActive();
    }

    public static void unmute(String player) {
        muteCache.remove(player);
    }

    public static void mute(Punishment punishment) {
        muteCache.put(punishment.getPlayer(), punishment);
    }

    public static class Handler implements Listener {
        @EventHandler(priority = EventPriority.HIGH)
        public void onJoin(PlayerJoinEvent e) {
            Punishment mute = new ParkourPlayer(e.getPlayer()).getActiveMute();
            muteCache.put(e.getPlayer().getName(), mute);
        }
    }
}
