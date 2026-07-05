package rdhxb.mixuk.repo.projection;

import java.time.LocalDate;

public interface DayTotal {
    LocalDate getDay();
    Double getBiomass();
    Double getCoal();
    Double getImports();
    Double getGas();
    Double getNuclear();
    Double getOther();
    Double getHydro();
    Double getSolar();
    Double getWind();
    Double getCleanEnergy();
}
