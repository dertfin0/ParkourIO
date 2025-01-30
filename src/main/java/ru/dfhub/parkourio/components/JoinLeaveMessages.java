package ru.dfhub.parkourio.components;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import ru.dfhub.parkourio.util.Config;

import static ru.dfhub.parkourio.util.MessageManager.getMessage;

/**
 * Слушатель, отвечающий за сообщения входа/выхода игрков
 */
public class JoinLeaveMessages implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        e.joinMessage(getJoinMessage(e.getPlayer().getName()));
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e) {
        e.quitMessage(getLeaveMessage(e.getPlayer().getName()));
    }

    private Component getJoinMessage(String playerName) {
        return MiniMessage.miniMessage().deserialize(
                getMessage("join-leave-messages.join")
                        .replace("%player%", playerName)
                        .replace("<player>", playerName)
        );
    }

    private Component getLeaveMessage(String playerName) {
        return MiniMessage.miniMessage().deserialize(
                getMessage("join-leave-messages.leave")
                        .replace("%player%", playerName)
                        .replace("<player>", playerName)
        );
    }
}
