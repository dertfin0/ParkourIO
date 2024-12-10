package ru.dfhub.parkourio.components;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import ru.dfhub.parkourio.ParkourIO;
import ru.dfhub.parkourio.util.Config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;

/**
 * Слушатель событий, отвечающий за формат чата, цензур-фильтр и др
 */
public class Chat implements Listener {

    private final String[] censoredWords = getCensoredWords();

    /**
     * Изменение формата чата
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onMessageSent(AsyncChatEvent e) {
        Component newMessage = MiniMessage.miniMessage().deserialize(
                getFormat()
                        .replace("<player>", e.getPlayer().getName())
                        .replace("%player%", e.getPlayer().getName())
                        .replace("<message>", ((TextComponent)e.message()).content())
                        .replace("%message%", ((TextComponent)e.message()).content())
        );
        e.renderer((source, sourceDisplayName, message1, viewer) -> newMessage);
    }

    /**
     * Удаление восклицательных знаков в начале сообщения
     */
    @EventHandler(priority = EventPriority.LOW)
    public void onMessageWithExclamationSymbol(AsyncChatEvent e) {
        String text = ((TextComponent) e.message()).content();
        if (!text.startsWith("!")) return;
        text = text.replaceFirst("!", "");
        e.message(Component.text(text));

        Bukkit.getScheduler().runTaskLaterAsynchronously(ParkourIO.getInstance(), new Runnable() {
            @Override
            public void run() {
                e.getPlayer().sendMessage(getExclamationPreventMessage());
                playBadSound(e.getPlayer());
            }
        }, 2); // Run after 0.1 second (2 ticks)
    }

    /**
     * Предотвращение отправки сообщений с цензурными словами
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onMessageCensored(AsyncChatEvent e) {
        String rawText = getRawMessage(((TextComponent) e.message()).content());
        LinkedList<String> wordsFound = new LinkedList<>();
        for (String cw : censoredWords) {
            if (rawText.contains(cw)) {
                wordsFound.add(cw);
            }
        }
        if (wordsFound.isEmpty()) return; // If not contains censored words
        e.setCancelled(true);

        StringBuilder hoverWordsBuilder = new StringBuilder();
        for (String word : wordsFound) {
            hoverWordsBuilder.append(word).append(", ");
        }
        String hoverWords = hoverWordsBuilder.toString();
        hoverWords = hoverWords.substring(0, hoverWords.length() - 2);


        e.getPlayer().sendMessage(MiniMessage.miniMessage().deserialize(
                Config.getConfig().getJSONObject("chat").getString("censored-message-warn-player")
                        .replace("%words%", hoverWords)
        ));
        playBadSound(e.getPlayer());
    }

    private String getFormat() {
        return Config.getConfig().getJSONObject("chat").getString("chat-message-format");
    }

    private Component getExclamationPreventMessage() {
        return MiniMessage.miniMessage().deserialize(
                Config.getConfig().getJSONObject("chat").getString("exclamation-prevent-message")
        );
    }

    private String[] getCensoredWords() {
        try {
            return Files.readString(Paths.get("plugins/ParkourIO/censor-list.txt"))
                    .split("\n");
        } catch (IOException e) {
            return new String[] {};
        }
    }

    /**
     * Получить сообщение без спец-символов
     * @param originalMessage Оригинальное сообщение
     * @return Сообщение без спец-символов
     */
    private String getRawMessage(String originalMessage) {
        String symbols = "!@#$%^&*()_+={}[]\"':;<>.,?/|\\`~ ";
        String rawMessage = originalMessage;
        for (char symbol : symbols.toCharArray()) {
            rawMessage = rawMessage.replace(String.valueOf(symbol), "");
        }
        return rawMessage;
    }

    /**
     * Проиграть игроку звук о плохом действии
     * @param player Игрок
     */
    private void playBadSound(Player player) {
        player.playSound(
                player.getLocation(),
                Sound.ENTITY_VILLAGER_NO,
                0.3f, 1f
        );
        player.playSound(
                player.getLocation(),
                Sound.BLOCK_ANVIL_LAND,
                0.01f, 1f
        );
    }
}
