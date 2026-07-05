package rdhxb.mixuk.repo.projection;

import java.time.LocalDateTime;

public interface CleanEnergy {
    Long getId();
    LocalDateTime getFromTime();
    LocalDateTime getToTime();
    Double getCleanEnergy();
}
