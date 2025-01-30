package ru.dfhub.parkourio.components.punishments.mute;

import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.paper.LegacyPaperCommandManager;
import ru.dfhub.parkourio.ParkourIO;
import ru.dfhub.parkourio.util.CloudCommand;
import ru.dfhub.parkourio.util.Metadata;

import static net.kyori.adventure.text.minimessage.MiniMessage.miniMessage;

public class ShowMutedMsg implements CloudCommand {
    @Override
    public void register(LegacyPaperCommandManager<CommandSender> manager) {
        manager.command(manager
                .commandBuilder("show-muted-msg")
                .permission("ru.dfhub.parkourio.punishments.show-muted-msg")
                .handler(this::handler)
        );
    }

    private void handler(CommandContext<CommandSender> ctx) {
        Player sender = (Player) ctx.sender();

        if (sender.hasMetadata(Metadata.DISABLE_MUTE_MSG.value())) {
            sender.removeMetadata(Metadata.DISABLE_MUTE_MSG.value(), ParkourIO.getInstance());
            sender.sendMessage(MiniMessage.miniMessage().deserialize(
                    "<yellow>Отображение сообщений в мьюте <green>включены</green>!"
            ));
        } else {
            sender.setMetadata(Metadata.DISABLE_MUTE_MSG.value(), new FixedMetadataValue(ParkourIO.getInstance(), true));
            sender.sendMessage(MiniMessage.miniMessage().deserialize(
                    "<yellow>Отображение сообщений в мьюте <red>выключены</red>!"
            ));
        }
    }

    public static void handle(String player, String message) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (!p.hasPermission("ru.dfhub.parkourio.punishments.show-muted-msg")) continue;
            if (isDisabled(p)) continue;

            p.sendMessage(miniMessage().deserialize(
                    "<red>[Mute]</red> <gray>%player% - %msg%</gray>"
                            .replace("%player%", player)
                            .replace("%msg%", message)
            ));
        }
    }

    private static boolean isDisabled(Player admin) {
        return admin.hasMetadata(Metadata.DISABLE_MUTE_MSG.value());
    }
}
