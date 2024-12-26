package ru.dfhub.parkourio.util;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Класс для работы с красивым отображением иконок в меню
 */
public class MenuFormatter {

    /**
     * Слоты для размещения предметов под каждое их количество. Рассчитано на меню с 27 слотами.
     */
    private static Map<Integer, List<Integer>> format = new HashMap<>() {{
        put(0, List.of(0));
        put(1, List.of(13));
        put(2, List.of(12, 14));
        put(3, List.of(11, 13, 15));
        put(4, List.of(10, 12, 14, 16));
        put(5, List.of(11, 12, 14, 14, 15));
        put(6, List.of(11, 12, 14, 14, 15, 22));
        put(7, List.of(10, 11, 12, 14, 14, 15, 16));
        put(8, List.of(4, 11, 12, 14, 14, 15, 21, 23));
        put(9, List.of(3, 5, 11, 12, 14, 14, 15, 21, 23));
        put(10, List.of(3, 4, 5, 11, 12, 14, 14, 15, 21, 23));
        put(11, List.of(3, 4, 5, 11, 12, 13, 14, 15, 21, 22, 23));
        put(12, List.of(2, 3, 4, 5, 6, 11, 12, 13, 14, 15, 21, 23));
        put(13, List.of(2, 3, 4, 5, 6, 11, 12, 13, 14, 15, 21, 22, 23));
        put(14, List.of(2, 3, 4, 5, 6, 11, 12, 13, 14, 15, 20, 21, 22, 23));
        put(15, List.of(2, 3, 4, 5, 6, 11, 12, 13, 14, 15, 20, 21, 22, 23, 24));
        put(16, List.of(2, 3, 4, 5, 6, 10, 11, 12, 13, 14, 15, 20, 21, 22, 23));
        put(17, List.of(2, 3, 4, 5, 6, 10, 11, 12, 13, 14, 15, 16, 20, 21, 22, 23, 25));
    }}; // < Кол-во предметов , Слоты в которых должны быть предметы >

    public static List<Integer> getFormat(int items) {
        return format.get(items);
    }

    public static Inventory getFormattedInventory(List<ItemStack> items) {
        Iterator<Integer> menuFormat = format.get(items.size()).iterator();

        boolean isAmplified = items.size() > 27;
        Inventory inventory = Bukkit.createInventory(null, isAmplified ? 45 : 27);

        if (items.size() > 17) {
            inventory.setContents(items.toArray(ItemStack[]::new));
            return inventory;
        }

        for (ItemStack item : items) {
            inventory.setItem(
                    isAmplified ? menuFormat.next() + 9 : menuFormat.next(),
                    item
            );
        }

        return inventory;
    }
}
