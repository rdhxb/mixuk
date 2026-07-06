package rdhxb.mixuk.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rdhxb.mixuk.dto.OptimalWindow;
import rdhxb.mixuk.repo.MixRepo;
import rdhxb.mixuk.repo.projection.CleanEnergy;


import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MixServiceTest {

    @Mock
    private MixRepo repo;

    @InjectMocks
    private MixService service;

    @Test
    void shouldFindBestWindowInTheMiddle() {
        LocalDateTime start = LocalDateTime.now(ZoneOffset.UTC).plusHours(1);
        List<CleanEnergy> data = List.of(
                fakeInterval(start,                     10.0),
                fakeInterval(start.plusMinutes(30),     20.0),
                fakeInterval(start.plusMinutes(60),     30.0),
                fakeInterval(start.plusMinutes(90),     80.0),
                fakeInterval(start.plusMinutes(120),    90.0),
                fakeInterval(start.plusMinutes(150),    85.0),
                fakeInterval(start.plusMinutes(180),    75.0),
                fakeInterval(start.plusMinutes(210),    40.0),
                fakeInterval(start.plusMinutes(240),    20.0),
                fakeInterval(start.plusMinutes(270),    10.0)
        );
        when(repo.findCleanEnergy()).thenReturn(data);

        OptimalWindow result = service.optimalCleanWindow(2);

        assertThat(result.getFromDate()).isEqualTo(start.plusMinutes(90));
        assertThat(result.getToDate()).isEqualTo(start.plusMinutes(210));
        assertThat(result.getAvgCleanEnergy()).isEqualTo(82.5);
    }

    @Test
    void shouldFindBestWindowAtStart() {
        LocalDateTime start = LocalDateTime.now(ZoneOffset.UTC).plusHours(1);
        List<CleanEnergy> data = List.of(
                fakeInterval(start,                     95.0),
                fakeInterval(start.plusMinutes(30),     90.0),
                fakeInterval(start.plusMinutes(60),     85.0),
                fakeInterval(start.plusMinutes(90),     80.0),
                fakeInterval(start.plusMinutes(120),    10.0),
                fakeInterval(start.plusMinutes(150),    5.0)
        );
        when(repo.findCleanEnergy()).thenReturn(data);

        OptimalWindow result = service.optimalCleanWindow(2);

        assertThat(result.getFromDate()).isEqualTo(start);
        assertThat(result.getToDate()).isEqualTo(start.plusMinutes(120));
        assertThat(result.getAvgCleanEnergy()).isEqualTo(87.5);
    }

    @Test
    void shouldFindBestWindowAtEnd() {
        LocalDateTime start = LocalDateTime.now(ZoneOffset.UTC).plusHours(1);
        List<CleanEnergy> data = List.of(
                fakeInterval(start,                     5.0),
                fakeInterval(start.plusMinutes(30),     10.0),
                fakeInterval(start.plusMinutes(60),     15.0),
                fakeInterval(start.plusMinutes(90),     20.0),
                fakeInterval(start.plusMinutes(120),    80.0),
                fakeInterval(start.plusMinutes(150),    90.0),
                fakeInterval(start.plusMinutes(180),    85.0),
                fakeInterval(start.plusMinutes(210),    95.0)
        );
        when(repo.findCleanEnergy()).thenReturn(data);

        OptimalWindow result = service.optimalCleanWindow(2);

        assertThat(result.getFromDate()).isEqualTo(start.plusMinutes(120));
        assertThat(result.getToDate()).isEqualTo(start.plusMinutes(240));
        assertThat(result.getAvgCleanEnergy()).isEqualTo(87.5);
    }

    @Test
    void shouldHandleOneHourWindow() {
        LocalDateTime start = LocalDateTime.now(ZoneOffset.UTC).plusHours(1);
        List<CleanEnergy> data = List.of(
                fakeInterval(start,                     30.0),
                fakeInterval(start.plusMinutes(30),     40.0),
                fakeInterval(start.plusMinutes(60),     90.0),
                fakeInterval(start.plusMinutes(90),     95.0),
                fakeInterval(start.plusMinutes(120),    20.0)
        );
        when(repo.findCleanEnergy()).thenReturn(data);

        OptimalWindow result = service.optimalCleanWindow(1);

        assertThat(result.getFromDate()).isEqualTo(start.plusMinutes(60));
        assertThat(result.getToDate()).isEqualTo(start.plusMinutes(120));
        assertThat(result.getAvgCleanEnergy()).isEqualTo(92.5);
    }

    @Test
    void shouldSkipWindowsInThePast() {
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        List<CleanEnergy> data = List.of(
                fakeInterval(now.minusHours(2),100.0),
                fakeInterval(now.minusHours(2).plusMinutes(30), 100.0),
                fakeInterval(now.minusHours(2).plusMinutes(60), 100.0),
                fakeInterval(now.minusHours(2).plusMinutes(90), 100.0),
                fakeInterval(now.plusHours(1),50.0),
                fakeInterval(now.plusHours(1).plusMinutes(30), 50.0),
                fakeInterval(now.plusHours(1).plusMinutes(60), 50.0),
                fakeInterval(now.plusHours(1).plusMinutes(90), 50.0)
        );
        when(repo.findCleanEnergy()).thenReturn(data);

        OptimalWindow result = service.optimalCleanWindow(2);

        assertThat(result.getAvgCleanEnergy()).isEqualTo(50.0);
        assertThat(result.getFromDate()).isEqualTo(now.plusHours(1));
    }


    private CleanEnergy fakeInterval(LocalDateTime fromTime, Double value) {
        return new CleanEnergy() {
            @Override
            public LocalDateTime getFromTime() {
                return fromTime;
            }
            @Override
            public LocalDateTime getToTime() {
                return fromTime.plusMinutes(30);
            }

            @Override
            public Double getCleanEnergy() {
                return value;
            }
        };
    }
}