package ru.dfhub.parkourio.components.menu;

import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.plain.PlainComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.json.JSONArray;
import ru.dfhub.parkourio.util.Config;
import ru.dfhub.parkourio.util.IconItems;
import ru.dfhub.parkourio.util.MenuFormatter;

import java.util.ArrayList;
import java.util.List;

public class AboutMenu {

    private static Inventory inventory;

    // non-static
    // берет предметы через конфиг при помощи IconItems и расставляет их через MenuFormatter
    // отдельным методом возвращает меню со списком фич
    public AboutMenu() {
        List<ItemStack> items = new ArrayList<>();

        JSONArray itemsJson = Config.getConfig().getJSONArray("about-menu-items");
        for (int i = 0; i < itemsJson.length(); i++) {
            items.add(IconItems.serialize(itemsJson.getJSONObject(i)));
        }

        inventory = MenuFormatter.getFormattedInventory(items);
    }

    public Inventory getInventory() {
        return inventory;
    }

    public static class Handler implements Listener {


        @EventHandler
        public void onInteract(PlayerInteractEvent e) {
            if (!(e.getPlayer().getInventory().getItemInMainHand() instanceof ItemStack item)) return;
            PlainTextComponentSerializer serializer = PlainTextComponentSerializer.builder().build();
            if (serializer.serialize(item.displayName()).equals("[О сервере]")) {
                e.getPlayer().openInventory(new AboutMenu().getInventory());
            }
        }
    }
}
