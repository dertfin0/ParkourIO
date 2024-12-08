package ru.dfhub.parkourio.components;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;
import org.json.JSONObject;
import ru.dfhub.parkourio.util.Config;
import java.util.List;
import java.util.Random;

public class MOTD implements Listener {

    @EventHandler
    public void onPing(ServerListPingEvent e) {
        e.setMaxPlayers(
                getSettings().optInt("max-players", 256)
        );
        e.motd(getRandomMotd());
    }

    private static JSONObject getSettings() {
        return Config.getConfig().getJSONObject("motd");
    }

    private static Component getRandomMotd() {
        List<String> motds = Config.getStringList(getSettings().getJSONArray("motds"));
        int i = new Random().nextInt(motds.size());
        return MiniMessage.miniMessage().deserialize(
                motds.get(i)
                    .replace("<br>", "\n")
        );
    }
}
