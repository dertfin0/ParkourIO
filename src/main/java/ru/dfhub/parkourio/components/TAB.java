package ru.dfhub.parkourio.components;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import ru.dfhub.parkourio.util.Config;

import java.util.List;

public class TAB implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Bukkit.getServer().sendPlayerListHeaderAndFooter(getHeader(), getFooter());
    }

    private Component getHeader() {
        Component header = Component.empty();
        List<String> lines = Config.getStringList(Config.getConfig().getJSONObject("tab").getJSONArray("header"));
        for (String line : lines) {
            header = header.append(MiniMessage.miniMessage().deserialize(line)).append(Component.text("\n"));
        }
        return header;
    }

    private Component getFooter() {
        Component footer = Component.empty();
        List<String> lines = Config.getStringList(Config.getConfig().getJSONObject("tab").getJSONArray("footer"));
        for (String line : lines) {
            footer = footer.append(MiniMessage.miniMessage().deserialize(line)).append(Component.text("\n"));
        }
        return footer;
    }
}
