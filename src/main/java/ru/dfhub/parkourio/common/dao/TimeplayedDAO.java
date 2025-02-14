package ru.dfhub.parkourio.common.dao;

import org.bukkit.Bukkit;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.MutationQuery;
import org.hibernate.query.Query;
import ru.dfhub.parkourio.ParkourIO;
import ru.dfhub.parkourio.common.Database;
import ru.dfhub.parkourio.common.entity.Timeplayed;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.stream.Collectors;


public class TimeplayedDAO {

    /**
     * Получить наигранное время игрока
     * @param player Игрок
     * @return Наигранное время в миллисекундах или 0, если игрок ни разу не заходил или возникла ошибка
     */
    public static long getTime(String player) {
        try (Session session = Database.openNewSession()) {
            Query<Timeplayed> query = session.createQuery("FROM Timeplayed t WHERE t.player = :player ORDER BY time DESC LIMIT 1", Timeplayed.class);
            query.setParameter("player", player);
            return query.getSingleResult().getTime();
        } catch (Exception e) {
            return 0L;
        }
    }

    public static void updateTime(String player, long newTime) {CompletableFuture.runAsync(() -> {
        try (Session session = Database.openNewSession()) {
            Transaction tx = session.beginTransaction();
            MutationQuery query = session.createMutationQuery("UPDATE Punishments p SET p.time = :time WHERE p.player = :player");
            query.setParameter("player", player);
            query.setParameter("time", newTime);
            query.executeUpdate();
            tx.commit();
        } catch (Exception e) {
            ParkourIO.getInstance().getLogger().log(Level.WARNING, "Can't update timeplayed!\n" + e.getMessage());
        }
    });}

    public static void setTime(String player, long time) {CompletableFuture.runAsync(() -> {
        try (Session session = Database.openNewSession()) {
            Transaction tx = session.beginTransaction();
            Timeplayed row = Timeplayed.builder()
                    .player(player)
                    .time(time)
                    .build();
            session.persist(row);
            tx.commit();
        } catch (Exception e) {
            ParkourIO.getInstance().getLogger().log(Level.WARNING, "Can't set timeplayed!\n" + e.getMessage());
        }
    });}

    public static Set<String> getPlayers() {
        try (Session session = Database.openNewSession()) {
            Query<Timeplayed> query = session.createQuery("FROM Timeplayed", Timeplayed.class);
            return query.getResultList().stream().map(Timeplayed::getPlayer).collect(Collectors.toSet());
        } catch (Exception e) {
            return Set.of();
        }
    }
}
