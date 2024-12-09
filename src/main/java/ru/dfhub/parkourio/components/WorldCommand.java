package ru.dfhub.parkourio.components;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.generator.ChunkGenerator;
import org.incendo.cloud.Command;
import org.incendo.cloud.bukkit.parser.WorldParser;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.description.Description;
import org.incendo.cloud.paper.LegacyPaperCommandManager;
import org.incendo.cloud.parser.standard.EnumParser;
import org.incendo.cloud.parser.standard.StringParser;
import ru.dfhub.parkourio.util.CloudCommand;
import ru.dfhub.parkourio.util.EmptyChunkGenerator;

import java.io.File;

public class WorldCommand implements CloudCommand {
    @Override
    public void register(LegacyPaperCommandManager<CommandSender> manager) {
        Command.Builder<CommandSender> builder = manager.commandBuilder("world").permission("ru.dfhub.parkourio.command.world");

        manager.command(builder
                .literal("create")
                .required("name", StringParser.stringParser(), Description.of("Назване мира"))
                .required("type", EnumParser.enumParser(WorldType.class))
                .flag(manager.flagBuilder("no-generate-structures").withDescription(Description.of("Отключить генерацию структур")))
                .flag(manager.flagBuilder("empty").withDescription(Description.of("Создать пустой мир, без блоков")))
                .flag(manager.flagBuilder("tp").withDescription(Description.of("Телепортироваться после создания")))
                .handler(this::handleCreate)
        );
        manager.command(builder
                .literal("tp")
                .required("name", WorldParser.worldParser())
                .handler(this::handleTp)
        );
        manager.command(builder
                .literal("load")
                .required("name", StringParser.stringParser())
                .handler(this::handleLoad)
        );
    }

    @Override
    public void handle(CommandContext<CommandSender> ctx) {

    }

    private void handleCreate(CommandContext<CommandSender> ctx) {
        String name = "worlds/" + ctx.getOrDefault("name", "new_world");

        WorldCreator wc = new WorldCreator(name);
        wc.type(ctx.getOrDefault("type", WorldType.FLAT));
        wc.generateStructures(!ctx.flags().isPresent("no-generate-structures"));

        if (ctx.flags().isPresent("empty")) wc.generator(new EmptyChunkGenerator());

        Bukkit.createWorld(wc);

        if (ctx.flags().isPresent("tp") && (ctx.sender() instanceof Player player)) {
            player.teleport(new Location(
                    Bukkit.getWorld(name), 0, 0, 0
            ));
        }
    }

    private void handleTp(CommandContext<CommandSender> ctx) {
        if (!(ctx.sender() instanceof Player player)) return;

        player.teleport(new Location(
            ctx.getOrDefault("name", Bukkit.getWorld("world")), 0, 0, 0
        ));
    }

    private void handleLoad(CommandContext<CommandSender> ctx) {
        String name = "worlds/" + ctx.getOrDefault("name", "world_name");

        Bukkit.createWorld(new WorldCreator(name));
        ctx.sender().sendMessage(MiniMessage.miniMessage().deserialize(
                "<green>Мир загружен!</green>"
        ));
    }
}
