package ru.dfhub.parkourio.common;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import ru.dfhub.parkourio.common.dao.PunishmentsDAO;
import ru.dfhub.parkourio.common.entity.Punishment;
import ru.dfhub.parkourio.components.punishments.mute.MuteCache;
import ru.dfhub.parkourio.util.PunishmentType;
import ru.dfhub.parkourio.util.TimeParser;

import javax.annotation.Nullable;
import java.util.List;

import static net.kyori.adventure.text.minimessage.MiniMessage.miniMessage;

public class ParkourPlayer {

    @Getter
    private final OfflinePlayer player;

    public ParkourPlayer(OfflinePlayer player) {
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
        return MuteCache.isMuted(player.getName());
        // return getActiveMute() != null; // Old non-cached version
    }

    public void ban(String from, long duration, String reason) {
        PunishmentsDAO.savePunishment(Punishment
                .builder()
                .player(this.player.getName())
                .fromAdmin(from)
                .type(PunishmentType.BAN)
                .startsAt(System.currentTimeMillis())
                .duration(duration)
                .reason(reason)
                .active(1)
                .build()
        );

        if (player.isOnline()) {
            player.getPlayer().kick(miniMessage().deserialize("""
                <red><b>Ваш аккаунт заблокирован!</b></red>
            
                Администратор %admin% забанил ваш аккаунт по причине:
                <aqua>%reason%</aqua>
                %time%
            """
                    .trim()
                    .replace("%admin%", from)
                    .replace("%reason%", reason)
                    .replace("%time%", duration == -1 ? "Блокировка выдана <red>навсегда</red>, но данное решение может пересмотреть администратор" :
                            "До конца блокировки осталось: <aqua>" + TimeParser.longToString(duration) + "</aqua>"
                    )
            ));
        }

    }

    public void mute(String from, long duration, String reason) {
        Punishment punishment = Punishment
                .builder()
                .player(this.player.getName())
                .fromAdmin(from)
                .type(PunishmentType.MUTE)
                .startsAt(System.currentTimeMillis())
                .duration(duration)
                .reason(reason)
                .active(1)
                .build();

        PunishmentsDAO.savePunishment(punishment);
        MuteCache.mute(punishment);
    }

    public int unban() { return PunishmentsDAO.unban(this.player); }

    public int unmute() {
        MuteCache.unmute(player.getName());
        return PunishmentsDAO.unmute(this.player);
    }
}
