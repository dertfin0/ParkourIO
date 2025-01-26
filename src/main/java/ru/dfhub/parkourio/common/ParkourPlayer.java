package ru.dfhub.parkourio.common;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import ru.dfhub.parkourio.common.dao.PunishmentsDAO;
import ru.dfhub.parkourio.common.entity.Punishment;
import ru.dfhub.parkourio.util.PunishmentType;

import javax.annotation.Nullable;
import java.util.List;

public class ParkourPlayer {

    @Getter
    private final Player player;

    public ParkourPlayer(Player player) {
        this.player = player;
    }

    public ParkourPlayer(String player) {
        this.player = Bukkit.getPlayer(player);
    }

    public boolean isOnline() {
        return player.isOnline();
    }

    public List<Punishment> getPunishments() {
        return PunishmentsDAO.getPunishmentsByPlayer(player);
    }

    @Nullable
    public Punishment getActiveBan() {
        List<Punishment> punishments = this.getPunishments().stream()
                .filter(it -> it.getType() == PunishmentType.BAN)
                .filter(Punishment::isActive)
                .toList();
        return punishments.isEmpty() ? null : punishments.getLast();
    }

    @Nullable
    public Punishment getActiveMute() {
        List<Punishment> punishments = this.getPunishments().stream()
                .filter(it -> it.getType() == PunishmentType.MUTE)
                .filter(Punishment::isActive)
                .toList();
        return punishments.isEmpty() ? null : punishments.getLast();
    }

    public boolean hasActiveBan() {
        return getActiveBan() != null;
    }

    public boolean hasActiveMute() {
        return getActiveMute() != null;
    }

    public void ban(String from, long duration, String reason) {
        PunishmentsDAO.savePunishment(Punishment
                .builder()
                .fromAdmin(from)
                .type(PunishmentType.BAN)
                .startsAt(System.currentTimeMillis())
                .duration(duration)
                .reason(reason)
                .active(1)
                .build()
        );
    }

    public void mute(String from, long duration, String reason) {
        PunishmentsDAO.savePunishment(Punishment
                .builder()
                .fromAdmin(from)
                .type(PunishmentType.MUTE)
                .startsAt(System.currentTimeMillis())
                .duration(duration)
                .reason(reason)
                .active(1)
                .build()
        );
    }
}
