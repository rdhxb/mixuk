package rdhxb.mixuk.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import rdhxb.mixuk.repo.projection.CleanEnergy;
import rdhxb.mixuk.repo.projection.DayTotal;
import rdhxb.mixuk.entity.Interval;

import java.util.List;

public interface MixRepo extends JpaRepository<Interval, Long> {

//    clean energy in intervals for sliding window
    @Query(value = """
            SELECT id,
                   from_time,
                   to_time,
                   (biomass_pct + nuclear_pct + hydro_pct + wind_pct + solar_pct) AS clean_energy
            FROM intervals
            """, nativeQuery = true)
    List<CleanEnergy> findCleanEnergy();


//    group intervals by day and get AVG from every source + AVG clean energy
    @Query(value = """
        SELECT DATE(from_time)         AS day,
               AVG(biomass_pct)        AS biomass,
               AVG(coal_pct)           AS coal,
               AVG(imports_pct)        AS imports,
               AVG(gas_pct)            AS gas,
               AVG(nuclear_pct)        AS nuclear,
               AVG(other_pct)          AS other,
               AVG(hydro_pct)          AS hydro,
               AVG(solar_pct)          AS solar,
               AVG(wind_pct)           AS wind,
               AVG(biomass_pct + nuclear_pct + hydro_pct + wind_pct + solar_pct)      AS cleanEnergy
        FROM intervals
        GROUP BY DATE(from_time)
        ORDER BY DATE(from_time)
        """, nativeQuery = true)
    List<DayTotal> groupIntervals();




}
