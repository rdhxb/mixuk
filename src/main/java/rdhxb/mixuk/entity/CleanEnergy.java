package rdhxb.mixuk.entity;

import java.time.LocalDateTime;

public interface CleanEnergy {
    Long getId();
    LocalDateTime getFromTime();
    LocalDateTime getToTime();
    Double getCleanEnergy();
}
