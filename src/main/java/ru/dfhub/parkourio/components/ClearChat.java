package ru.dfhub.parkourio.components;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.paper.LegacyPaperCommandManager;
import ru.dfhub.parkourio.util.CloudCommand;

public class ClearChat implements CloudCommand {
    @Override
    public void register(LegacyPaperCommandManager<CommandSender> manager) {
        manager.command(manager
                .commandBuilder("clearchat", "chatclear")
                .permission("ru.dfhub.parkourio.command.clearchat")
                .handler(ctx -> {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        for (int i = 0; i < 200; i++) {
                            player.sendMessage("");
                        }
                    }
                })
        );
    }
}
