package ru.dfhub.parkourio.components;

import com.destroystokyo.paper.event.player.PlayerLaunchProjectileEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.metadata.FixedMetadataValue;
import ru.dfhub.parkourio.ParkourIO;
import ru.dfhub.parkourio.util.Metadata;

public class InventoryPrevent implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        e.getPlayer().setMetadata(Metadata.INVENTORY_PREVENT.value(), new FixedMetadataValue(ParkourIO.getInstance(), true));
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent e) {
        if (e.getPlayer().hasMetadata(Metadata.INVENTORY_PREVENT.value())) e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getWhoClicked().hasMetadata(Metadata.INVENTORY_PREVENT.value())) e.setCancelled(true);
    }

    @EventHandler
    public void onInventoryMove(InventoryMoveItemEvent e) {
        if (e.getSource().getViewers().getFirst().hasMetadata(Metadata.INVENTORY_PREVENT.value())) e.setCancelled(true);
    }

    @EventHandler
    public void onDrag(InventoryDragEvent e) {
        if (e.getWhoClicked().hasMetadata(Metadata.INVENTORY_PREVENT.value())) e.setCancelled(true);
    }

    @EventHandler
    public void onInventoryPickupItem(InventoryPickupItemEvent e) {
        if (e.getInventory().getViewers().getFirst().hasMetadata(Metadata.INVENTORY_PREVENT.value())) e.setCancelled(true);
    }

    @EventHandler
    public void onProjectileLaunch(PlayerLaunchProjectileEvent e) {
        if (e.getPlayer().hasMetadata(Metadata.INVENTORY_PREVENT.value())) e.setCancelled(true);
    }

    @EventHandler
    public void onHandItemSwap(PlayerSwapHandItemsEvent e) {
        if (e.getPlayer().hasMetadata(Metadata.INVENTORY_PREVENT.value())) e.setCancelled(true);
    }
}
