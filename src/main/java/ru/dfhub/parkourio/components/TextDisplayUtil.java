package ru.dfhub.parkourio.components;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.incendo.cloud.Command;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.description.Description;
import org.incendo.cloud.paper.LegacyPaperCommandManager;
import org.incendo.cloud.parser.standard.EnumParser;
import org.incendo.cloud.parser.standard.StringParser;
import ru.dfhub.parkourio.util.CloudCommand;

import java.util.Arrays;
import java.util.List;

import static net.kyori.adventure.text.minimessage.MiniMessage.miniMessage;
import static ru.dfhub.parkourio.util.MessageManager.getMessage;

public class TextDisplayUtil implements CloudCommand {

    @Override
    public void register(LegacyPaperCommandManager<CommandSender> manager) {
        Command.Builder<CommandSender> builder = manager
                .commandBuilder("text-display-util", "td-util", "tdu")
                .permission("ru.dfhub.parkourio.command.text-display-util");

        manager.command(builder
                .literal("remove-closest")
                .handler(ctx -> Bukkit.dispatchCommand(ctx.sender(), "kill @n[type=minecraft:text_display]"))
        );

        manager.command(builder
                .literal("spawn")
                .required("alignment", EnumParser.enumParser(TextDisplay.TextAlignment.class))
                .required("billboard", EnumParser.enumParser(Display.Billboard.class))
                .required("text", StringParser.greedyStringParser())
                .handler(this::spawnHandler)
        );
    }

    private void spawnHandler(CommandContext<CommandSender> ctx) {
        if (!(ctx.sender() instanceof Player player)) return;

        player.getWorld().spawn(player.getLocation(), TextDisplay.class, entity -> {
            entity.text(miniMessage().deserialize(ctx.get("text")));
            entity.setAlignment(ctx.get("alignment"));
            entity.setBillboard(ctx.get("billboard"));
            entity.setBrightness(new Display.Brightness(15, 15));
        });
    }
}
