package ru.dfhub.parkourio.components;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import ru.dfhub.parkourio.util.Config;

public class Chat implements Listener {

    @EventHandler
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

    private String getFormat() {
        return Config.getConfig().getJSONObject("chat").getString("chat-message-format");
    }
}
