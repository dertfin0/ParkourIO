package ru.dfhub.parkourio.components;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockGrowEvent;

public class DisableBlockGrow implements Listener {

    @EventHandler
    public void onGrow(BlockGrowEvent e) {
        e.setCancelled(true);
    }
}
