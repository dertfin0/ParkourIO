package ru.dfhub.parkourio.common.dao;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.MutationQuery;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;
import ru.dfhub.parkourio.common.Database;
import ru.dfhub.parkourio.common.entity.Punishment;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class PunishmentsDAO {

    public static List<Punishment> getAllPunishments() {
        try (Session session = Database.openNewSession()) {
            return CompletableFuture.supplyAsync(() -> session.createQuery("FROM Punishment punishment", Punishment.class).getResultList()).get();
        } catch (Exception e) {
            return List.of();
        }
    }

    public static List<Punishment> getPunishmentsByPlayer(OfflinePlayer player) {
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
            session.persist(punishment);
            tx.commit();
            session.close();
        });
    }

    public static int unmute(OfflinePlayer player) {
        try (Session session = Database.openNewSession()) {
            Transaction tx = session.beginTransaction();
            // Тут обычный query вместо mutationQuery тк hql не саппортит лимиты, а тут важно заменить только одну строку
            NativeQuery<Punishment> query = session.createNativeQuery("UPDATE punishments p SET p.active = 0 WHERE p.player = :player AND p.type = 'MUTE' ORDER BY id DESC LIMIT 1", Punishment.class);
            query.setParameter("player", player.getName());
            int result = query.executeUpdate();
            tx.commit();

            return result;
        }
    }

    public static int unban(OfflinePlayer player) {
        try (Session session = Database.openNewSession()) {
            Transaction tx = session.beginTransaction();
            // Тут обычный query вместо mutationQuery тк hql не саппортит лимиты, а тут важно заменить только одну строку
            NativeQuery<Punishment> query = session.createNativeQuery("UPDATE punishments p SET p.active = 0 WHERE p.player = :player AND p.type = 'BAN' ORDER BY id DESC LIMIT 1", Punishment.class);
            query.setParameter("player", player.getName());
            int result = query.executeUpdate();
            tx.commit();

            return result;
        }
    }

    public static Set<String> getBannedPlayers() {
        try (Session session = Database.openNewSession()) {
            Query<Punishment> query = session.createQuery("FROM Punishment punishment WHERE punishment.type = 'BAN'", Punishment.class);
            return query.getResultList()
                    .stream()
                    .filter(Punishment::isActive)
                    .map(Punishment::getPlayer)
                    .collect(Collectors.toSet());
        } catch (Exception e) {
            return Set.of();
        }
    }

    public static Set<String> getMutedPlayers() {
        try (Session session = Database.openNewSession()) {
            Query<Punishment> query = session.createQuery("FROM Punishment punishment WHERE punishment.type = 'MUTE'", Punishment.class);
            return query.getResultList()
                    .stream()
                    .filter(Punishment::isActive)
                    .map(Punishment::getPlayer)
                    .collect(Collectors.toSet());
        } catch (Exception e) {
            return Set.of();
        }
    }
}
