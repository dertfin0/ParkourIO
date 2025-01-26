package ru.dfhub.parkourio.common.dao;

import org.bukkit.entity.Player;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import ru.dfhub.parkourio.common.Database;
import ru.dfhub.parkourio.common.entity.Punishment;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class PunishmentsDAO {

    public static List<Punishment> getAllPunishments() {
        try {
            return CompletableFuture.supplyAsync(() -> Database.getSession().createQuery("FROM Punishment punishment", Punishment.class).getResultList()).get();
        } catch (Exception e) {
            return List.of();
        }
    }

    public static List<Punishment> getPunishmentsByPlayer(Player player) {
        try {
            Query<Punishment> query = Database.getSession().createQuery("FROM Punishment punishment WHERE player = :player", Punishment.class);
            query.setParameter("player", player.getName());
            return CompletableFuture.supplyAsync(query::getResultList).get();
        } catch (Exception e) {
            return List.of();
        }
    }

    public static void savePunishment(Punishment punishment) {
        CompletableFuture.runAsync(() -> {
            Transaction tx = Database.getSession().beginTransaction();
            Database.getSession().persist(punishment);
            tx.commit();
        });

    }
}
