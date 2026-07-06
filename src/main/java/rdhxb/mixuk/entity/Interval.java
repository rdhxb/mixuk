package rdhxb.mixuk.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.time.LocalDateTime;

@Entity
@Table(name = "intervals")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Interval {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "from_time", unique = true)
    private LocalDateTime from;
    @Column(name = "to_time")
    private LocalDateTime to;
    private Double biomass_pct;
    private Double coal_pct;
    private Double imports_pct;
    private Double gas_pct;
    private Double nuclear_pct;
    private Double other_pct;
    private Double hydro_pct;
    private Double solar_pct;
    private Double wind_pct;
}
