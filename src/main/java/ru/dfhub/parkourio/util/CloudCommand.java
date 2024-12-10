package ru.dfhub.parkourio.util;

import org.bukkit.command.CommandSender;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.paper.LegacyPaperCommandManager;

/**
 * Основные методы для команд на основе Cloud<br>
 * Нужен для их упрощенной загрузки
 */
public interface CloudCommand {

    void register(LegacyPaperCommandManager<CommandSender> manager);
    void handle(CommandContext<CommandSender> ctx);
}
