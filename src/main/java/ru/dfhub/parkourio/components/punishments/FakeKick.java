package ru.dfhub.parkourio.components.punishments;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.bukkit.parser.PlayerParser;
import org.incendo.cloud.paper.LegacyPaperCommandManager;
import org.incendo.cloud.parser.standard.EnumParser;
import ru.dfhub.parkourio.util.CloudCommand;

import static ru.dfhub.parkourio.util.MessageManager.getMessage;

public class FakeKick implements CloudCommand {
    private enum Reason {
        TIMEOUT("Превышено время ожидания."),
        DISCONNECTED("Disconnected."),
        JAVA_IOE("Internal exception: java.io.IOException: Received string length longer than the maximum allowed (3035>256)");

        private final String title;

        Reason(String title) {
            this.title = title;
        }

        public Component getTitle() {
            return MiniMessage.miniMessage().deserialize("<white>%s</white>".formatted(this.title));
        }

        public String getTitleRaw() {
            return this.title;
        }
    }

    @Override
    public void register(LegacyPaperCommandManager<CommandSender> manager) {
        manager.command(manager
                .commandBuilder("fake-kick", "kick-fake")
                .permission("ru.dfhub.parkourio.punishments.fake-kick")
                .required("player", PlayerParser.playerParser())
                .required("reason", EnumParser.enumParser(Reason.class))
                .handler(ctx -> {
                    Player player = ctx.get("player");
                    Reason reason = ctx.getOrDefault("reason", Reason.TIMEOUT);
                    player.kick(reason.getTitle());
                    ctx.sender().sendMessage(
                            getMessage("punishments.fake-kick.to-admin")
                                    .replace("%player%", player.getName())
                                    .replace("%reason%", reason.getTitleRaw())
                    );
                })
        );
    }

}
