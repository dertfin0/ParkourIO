package ru.dfhub.parkourio.common.entity;

import jakarta.persistence.*;
import lombok.*;
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

    @Column(name = "from_admin", nullable = false)
    private String fromAdmin;

    @Column(nullable = false)
    private String player;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private PunishmentType type;

    @Column(name = "starts_at", nullable = false)
    private long startsAt;

    @Column(name = "duration", nullable = false)
    private long duration;

    @Column(nullable = false)
    private String reason;

    @Column(nullable = false)
    private int active;

    public boolean isActive() {
        return System.currentTimeMillis() < startsAt + duration && active == 1 || ( duration == -1 && active == 1);
    }
}
