package rdhxb.mixuk.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OptimalWindow {
    private LocalDateTime fromDate;
    private LocalDateTime toDate;
    private Double avgCleanEnergy;
}
