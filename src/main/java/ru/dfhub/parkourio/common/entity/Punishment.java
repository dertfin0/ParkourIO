package ru.dfhub.parkourio.common.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.dfhub.parkourio.util.PunishmentType;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "punishments")
public class Punishment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "from_admin")
    private String fromAdmin;

    private String player;

    @Enumerated(value = EnumType.STRING)
    private PunishmentType type;

    @Column(name = "starts_at")
    private long startsAt;

    @Column(name = "duration")
    private long duration;

    private String reason;

    private int active;

    public boolean isActive() {
        return System.currentTimeMillis() > startsAt + duration && active == 1;
    }
}
