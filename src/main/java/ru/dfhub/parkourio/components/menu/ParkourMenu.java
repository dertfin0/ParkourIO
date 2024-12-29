package ru.dfhub.parkourio.components.menu;

import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import ru.dfhub.parkourio.ParkourIO;
import ru.dfhub.parkourio.components.parkour_level.ParkourLevel;
import ru.dfhub.parkourio.components.parkour_level.ParkourLevels;
import ru.dfhub.parkourio.util.Metadata;

public class ParkourMenu {

    private Inventory inventory;

    @SuppressWarnings("deprecation")
    public ParkourMenu() {
        inventory = Bukkit.createInventory(null, 27, "Уровни паркура");

        for (ParkourLevel level : ParkourLevels.getLevels()) {
            inventory.addItem(level.getIcon());
        }
    }

    public Inventory getInventory() {
        return inventory;
    }

    public static class Handler implements Listener {

        @EventHandler
        public void onInteract(PlayerInteractEvent e) {
            if (e.getAction() != Action.RIGHT_CLICK_AIR && e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
            if (!(e.getPlayer().getInventory().getItemInMainHand() instanceof ItemStack item)) return;
            PlainTextComponentSerializer serializer = PlainTextComponentSerializer.builder().build();
            if (serializer.serialize(item.displayName()).equals("[Паркур-карты]")) {
                e.getPlayer().openInventory(new ParkourMenu().getInventory());
                e.getPlayer().setMetadata(Metadata.OPENED_PARKOUR_MENU.value(), new FixedMetadataValue(ParkourIO.getInstance(), true));
            }
        }

        @EventHandler
        public void onInventoryClose(InventoryCloseEvent e) {
            e.getPlayer().removeMetadata(Metadata.OPENED_PARKOUR_MENU.value(), ParkourIO.getInstance());
        }

        @EventHandler(priority = EventPriority.LOWEST)
        public void onInventoryClick(InventoryClickEvent e) {
            if (!e.getWhoClicked().hasMetadata(Metadata.OPENED_PARKOUR_MENU.value())) return;
            if (!(e.getWhoClicked() instanceof Player player)) return;

            if (e.getSlot() > ParkourLevels.getLevels().size()) return;
            player.chat("/parkour tp %s".formatted(e.getSlot()));
         }
    }
}
