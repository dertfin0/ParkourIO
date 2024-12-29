package ru.dfhub.parkourio.components;

import org.bukkit.*;
import org.bukkit.entity.Player;

public class ParticleManager extends Thread {

    private static final double[][] adminParticles = new double[][] {
            {0.500, 0.000},
            {0.476, 0.155},
            {0.405, 0.294},
            {0.294, 0.405},
            {0.155, 0.476},
            {0.000, 0.500},
            {-0.155, 0.476},
            {-0.294, 0.405},
            {-0.405, 0.294},
            {-0.476, 0.155},
            {-0.500, 0.000},
            {-0.476, -0.155},
            {-0.405, -0.294},
            {-0.294, -0.405},
            {-0.155, -0.476},
            {-0.000, -0.500},
            {0.155, -0.476},
            {0.294, -0.405},
            {0.405, -0.294},
            {0.476, -0.155}
    };

    public ParticleManager() {
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(150);
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (!p.hasMetadata("ru.dfhub.parkourio.particles.admin.1")) continue;

                    World world = p.getWorld();
                    Location loc = p.getLocation();
                    for (double[] cords : adminParticles) {
                        world.spawnParticle(Particle.DUST, loc.x() + cords[0], loc.y(), loc.z() + cords[1], 1, 0, 0, 0, 0, new Particle.DustOptions(Color.RED, 1));
                    }
                }

                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (!p.hasMetadata("ru.dfhub.parkourio.particles.admin.2")) continue;

                    p.getWorld().spawnParticle(Particle.DUST, p.getLocation(), 1, 1, 2, 1, 0, new Particle.DustOptions(Color.RED, 1));
                }
            } catch (InterruptedException e) {}
        }
    }
}
