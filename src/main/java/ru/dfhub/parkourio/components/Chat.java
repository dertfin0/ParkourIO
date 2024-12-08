package ru.dfhub.parkourio.components;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import ru.dfhub.parkourio.ParkourIO;
import ru.dfhub.parkourio.util.Config;

import java.util.concurrent.CompletableFuture;

public class Chat implements Listener {

    @EventHandler(priority = EventPriority.LOW)
    public void onMessageSent(AsyncChatEvent e) {
        Component newMessage = MiniMessage.miniMessage().deserialize(
                getFormat()
                        .replace("<player>", e.getPlayer().getName())
                        .replace("%player%", e.getPlayer().getName())
                        .replace("<message>", ((TextComponent)e.message()).content())
                        .replace("%message%", ((TextComponent)e.message()).content())
        );
        e.renderer((source, sourceDisplayName, message1, viewer) -> newMessage);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onMessageWithExclamationSymbol(AsyncChatEvent e) {
        String text = ((TextComponent) e.message()).content();
        if (!text.startsWith("!")) return;
        text = text.replaceFirst("!", "");
        e.message(Component.text(text));

        Bukkit.getScheduler().runTaskLaterAsynchronously(ParkourIO.getInstance(), new Runnable() {
            @Override
            public void run() {
                e.getPlayer().sendMessage(getExclamationPreventMessage());
                e.getPlayer().playSound(
                        e.getPlayer().getLocation(),
                        Sound.ENTITY_VILLAGER_NO,
                        0.3f, 1f
                );
                e.getPlayer().playSound(
                        e.getPlayer().getLocation(),
                        Sound.BLOCK_ANVIL_LAND,
                        0.01f, 1f
                );
            }
        }, 2); // Run after 0.1 second (2 ticks)
    }

    private String getFormat() {
        return Config.getConfig().getJSONObject("chat").getString("chat-message-format");
    }

    private Component getExclamationPreventMessage() {
        return MiniMessage.miniMessage().deserialize(
                Config.getConfig().getJSONObject("chat").getString("exclamation-prevent-message")
        );
    }
}
