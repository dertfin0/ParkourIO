package ru.dfhub.parkourio.util;

import org.bukkit.command.CommandSender;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.paper.LegacyPaperCommandManager;

public interface CloudCommand {

    void register(LegacyPaperCommandManager<CommandSender> manager);
    void handle(CommandContext<CommandSender> ctx);

}
