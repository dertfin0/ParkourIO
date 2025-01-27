package ru.dfhub.parkourio.components.punishments;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.bukkit.parser.PlayerParser;
import org.incendo.cloud.paper.LegacyPaperCommandManager;
import ru.dfhub.parkourio.common.ParkourPlayer;
import ru.dfhub.parkourio.util.CloudCommand;

import static net.kyori.adventure.text.minimessage.MiniMessage.miniMessage;

public class UnMute implements CloudCommand {
    @Override
    public void register(LegacyPaperCommandManager<CommandSender> manager) {
        manager.command(manager
                .commandBuilder("unmute")
                .required("player", PlayerParser.playerParser())
                .permission("ru.dfhub.parkourio.punishments.unmute")
                .handler(ctx -> {
                    Player player = ctx.get("player");
                    int result = new ParkourPlayer(player).unmute();
                    if (result > 0) {
                        ctx.sender().sendMessage(miniMessage().deserialize(
                                "<green>Вы успешно размутили <aqua>%player%</aqua>!</green>".replace("%player%", player.getName())
                        ));
                    } else {
                        ctx.sender().sendMessage(miniMessage().deserialize(
                                "<red>У игрока <aqua>%player%</aqua> нет мутов!</red>".replace("%player%", player.getName())
                        ));
                    }

                    player.sendMessage(miniMessage().deserialize(
                            "<green>Администратор <aqua>%admin%</aqua> снял с вас мут! Теперь вы снова можете писать в чат</green>".replace("%admin%", ctx.sender().getName())
                    ));
                })
        );
    }
}
