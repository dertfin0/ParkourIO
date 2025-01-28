package ru.dfhub.parkourio.components.punishments;

import net.kyori.adventure.text.BuildableComponent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.bukkit.parser.PlayerParser;
import org.incendo.cloud.paper.LegacyPaperCommandManager;
import org.incendo.cloud.parser.standard.StringParser;
import ru.dfhub.parkourio.common.ParkourPlayer;
import ru.dfhub.parkourio.util.CloudCommand;

import static net.kyori.adventure.text.minimessage.MiniMessage.miniMessage;

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
                                "<red>У игрока <aqua>%player%</aqua> нет мутов!</red>".replace("%player%", player.getPlayer().getName())
                        ));
                        return;
                    }

                    parkourPlayer.unmute();
                    ctx.sender().sendMessage(miniMessage().deserialize(
                            "<green>Вы успешно размутили <aqua>%player%</aqua>!</green>".replace("%player%", player.getPlayer().getName())
                    ));

                    if (player.isOnline()) {
                        player.getPlayer().sendMessage(miniMessage().deserialize(
                                "<green>Администратор <aqua>%admin%</aqua> снял с вас мут! Теперь вы снова можете писать в чат</green>".replace("%admin%", ctx.sender().getName())
                        ));
                    }
                })
        );
    }
}
