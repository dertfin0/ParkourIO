package ru.dfhub.parkourio.util;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static net.kyori.adventure.text.minimessage.MiniMessage.miniMessage;

public class IconItems {

    /**
     * Сереализует JSON-объект в игровой предмет<br>
     * Подробнее про структуру требуемого JSON-объекта в README.md
     * @param json JSONObject
     * @return Minecraft ItemStack
     */
    public static ItemStack serialize(JSONObject json) {
        ItemStack itemStack = new ItemStack(
                Material.getMaterial(json.optString("material", "STONE").toUpperCase().replace(" ", "_")),
                json.optInt("amount", 1)
        );
        ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.displayName(miniMessage().deserialize("<!i>" + json.optString("name", " ")));
        itemMeta.lore(getLore(json.getJSONArray("lore")));
        if (json.optBoolean("enchanted", false)) {
            itemMeta.addEnchant(Enchantment.KNOCKBACK, 1, false);
            itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    /**
     * Создает ItemStack без нужды в написании монотонного кода
     * @param material Material
     * @param amount Amount (or 1)
     * @param name Display name (or " ")
     * @param lore Lore (or empty)
     * @param enchanted Is item enchanted (false by default)
     * @return Minecraft ItemStack
     */
    public static ItemStack serialize(Material material, int amount, String name, List<String> lore, boolean enchanted) {
        ItemStack itemStack = new ItemStack(
                Optional.of(material).orElse(Material.STONE),
                Optional.of(amount).orElse(1)
        );
        ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.displayName(miniMessage().deserialize("<!i>" + Optional.of(name).orElse(" ")));
        itemMeta.lore(getLore(lore));
        if (Optional.of(enchanted).orElse(false)) {
            itemMeta.addEnchant(Enchantment.KNOCKBACK, 1, false);
            itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    /**
     * Преобразовывает каждую строку JSONArray в MiniMessage
     * и собирает их в массив
     * @param loreArray JSONArray
     * @return Форматированное описание предмета
     */
    private static List<Component> getLore(JSONArray loreArray) {
        List<Component> lore = new ArrayList<>();
        if (loreArray.isEmpty()) return new ArrayList<>();

        for (Object obj : loreArray) {
            lore.add(
                    miniMessage().deserialize((String) obj)
            );
        }
        return lore;
    }

    /**
     * Преобразовывает каждую строку  в MiniMessage
     * и собирает их в массив
     * @param loreArray String list
     * @return Форматированное описание предмета
     */
    private static List<Component> getLore(List<String> loreArray) {
        List<Component> lore = new ArrayList<>();
        if (loreArray.isEmpty()) return new ArrayList<>();

        for (String str : loreArray) {
            lore.add(
                    miniMessage().deserialize(str)
            );
        }
        return lore;
    }

}
