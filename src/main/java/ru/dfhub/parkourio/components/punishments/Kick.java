package ru.dfhub.parkourio.components.punishments;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.bukkit.parser.PlayerParser;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.paper.LegacyPaperCommandManager;
import org.incendo.cloud.parser.standard.StringParser;
import ru.dfhub.parkourio.common.ParkourPlayer;
import ru.dfhub.parkourio.util.CloudCommand;
import ru.dfhub.parkourio.util.TempPlayerListCache;

import static net.kyori.adventure.text.minimessage.MiniMessage.miniMessage;
import static ru.dfhub.parkourio.util.MessageManager.getMessage;

public class Kick implements CloudCommand {
    @Override
    public void register(LegacyPaperCommandManager<CommandSender> manager) {
        manager.command(manager
                .commandBuilder("kick", "kick")
                .permission("ru.dfhub.parkourio.punishments.kick")
                .required("player", PlayerParser.playerParser(), new TempPlayerListCache.Suggestions())
                .required("reason", StringParser.greedyStringParser())
                .flag(manager.flagBuilder("silent").build())
                .handler(this::handler)
        );
    }

    private void handler(CommandContext<CommandSender> ctx) {
        Player player = ctx.get("player");

        boolean isSilent = ctx.getOrDefault("reason", "").contains("--silent");
        String reason = ctx.getOrDefault("reason", "Причина не указана").replace("--silent", "");

        new ParkourPlayer(player).kick(ctx.sender().getName(), reason);

        ctx.sender().sendMessage(miniMessage().deserialize(getMessage("punishments.kick.to-admin")));

        if (!isSilent) {
            ctx.sender().sendMessage(miniMessage().deserialize(getMessage("punishments.kick.broadcast")
                    .replace("%player%", player.getName())
                    .replace("%reason%", reason)
            ));
        }
    }
}
