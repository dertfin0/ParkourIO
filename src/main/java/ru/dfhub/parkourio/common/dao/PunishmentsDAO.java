package ru.dfhub.parkourio.common.dao;

import org.bukkit.entity.Player;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import ru.dfhub.parkourio.common.Database;
import ru.dfhub.parkourio.common.entity.Punishment;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class PunishmentsDAO {

    public static List<Punishment> getAllPunishments() {
        try (Session session = Database.openNewSession()) {
            return CompletableFuture.supplyAsync(() -> session.createQuery("FROM Punishment punishment", Punishment.class).getResultList()).get();
        } catch (Exception e) {
            return List.of();
        }
    }

    public static List<Punishment> getPunishmentsByPlayer(Player player) {
        try (Session session = Database.openNewSession()) {
            Query<Punishment> query = session.createQuery("FROM Punishment punishment WHERE player = :player", Punishment.class);
            query.setParameter("player", player.getName());
            return CompletableFuture.supplyAsync(query::getResultList).get();
        } catch (Exception e) {
            return List.of();
        }
    }

    public static void savePunishment(Punishment punishment) {
        CompletableFuture.runAsync(() -> {
            Session session = Database.openNewSession();
            Transaction tx = session.beginTransaction();
            Database.getSession().persist(punishment);
            tx.commit();
            session.close();
        });

    }
}
