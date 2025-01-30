package ru.dfhub.parkourio.util;

public enum Metadata {
    ON_PARKOUR_LEVEL("ru.dfhub.parkourio.parkour.on_parkour_level"),
    STARTED_AT("ru.dfhub.parkourio.parkour.started_at"),
    CHECKPOINT("ru.dfhub.parkourio.parkour.checkpoint"),
    INVENTORY_PREVENT("ru.dfhub.parkourio.inventory-prevent"),
    OPENED_PARKOUR_MENU("ru.dfhub.parkourio.opened-parkour-menu"),
    DISABLE_MUTE_MSG("ru.dfhub.parkourio.disable-mute-msg")
    ;

    private final String value;

    Metadata(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}
