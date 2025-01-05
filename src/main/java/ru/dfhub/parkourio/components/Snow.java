package ru.dfhub.parkourio.components;

import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import ru.dfhub.parkourio.util.Config;

public class Snow extends Thread {

    boolean isStopped = false;

    @Override
    public void run() {
        while (!isStopped) {
            if (!Config.getConfig().optBoolean("enable-snow", false)) return;

            try { Thread.sleep(350); } catch (InterruptedException e) {}

            for (Player p : Bukkit.getOnlinePlayers()) {
                p.getWorld().spawnParticle(Particle.SNOWFLAKE, p.getLocation(), 50, 16, 16, 16, 0.075);
            }
        }
    }

    public void setStopped(boolean isStopped) {
        this.isStopped = isStopped;
    }
}
