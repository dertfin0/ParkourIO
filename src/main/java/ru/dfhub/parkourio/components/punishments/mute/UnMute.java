package ru.dfhub.parkourio.components.punishments.mute;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.paper.LegacyPaperCommandManager;
import org.incendo.cloud.parser.standard.StringParser;
import ru.dfhub.parkourio.common.ParkourPlayer;
import ru.dfhub.parkourio.util.CloudCommand;

import static net.kyori.adventure.text.minimessage.MiniMessage.miniMessage;
import static ru.dfhub.parkourio.util.MessageManager.getMessage;

public class UnMute implements CloudCommand {
    @Override
    public void register(LegacyPaperCommandManager<CommandSender> manager) {
        manager.command(manager
                .commandBuilder("unmute")
                .required("player", StringParser.stringParser())
                .permission("ru.dfhub.parkourio.punishments.unmute")
                .handler(ctx -> {
                    OfflinePlayer player = Bukkit.getOfflinePlayer(ctx.getOrDefault("player", "null"));
                    ParkourPlayer parkourPlayer = new ParkourPlayer(player);

                    if (!parkourPlayer.hasActiveMute()) {
                        ctx.sender().sendMessage(miniMessage().deserialize(
                               getMessage("punishments.mute.unmute.hasnt-mutes").formatted(player.getPlayer().getName())
                        ));
                        return;
                    }

                    parkourPlayer.unmute();
                    ctx.sender().sendMessage(miniMessage().deserialize(
                            getMessage("punishments.mute.unmute.unmuted").formatted(player.getPlayer().getName())
                    ));

                    if (player.isOnline()) {
                        player.getPlayer().sendMessage(miniMessage().deserialize(
                                getMessage("punishments.mute.unmute.unmuted-to-player").replace("%admin%", ctx.sender().getName())
                        ));
                    }
                })
        );
    }
}
