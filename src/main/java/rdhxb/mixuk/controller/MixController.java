package rdhxb.mixuk.controller;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import rdhxb.mixuk.collectData.GetData;
import rdhxb.mixuk.repo.projection.CleanEnergy;
import rdhxb.mixuk.repo.projection.DayTotal;
import rdhxb.mixuk.dto.OptimalWindow;
import rdhxb.mixuk.service.MixService;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Validated
public class MixController {

    private final MixService service;
    private final GetData getData;

    @Value("${refresh.token}")
    private String refreshToken;

    @GetMapping("/dayMix")
    public List<DayTotal> getDatyTotalMix(){
        return service.groupIntervals();
    }


    @GetMapping("/window")
    public OptimalWindow getWindow(@RequestParam @Min(1) @Max(6) int hours){
        return service.optimalCleanWindow(hours);
    }

    @PostMapping("/refresh")
    public void refreshData(@RequestHeader("X-Token") String token){
        if (!refreshToken.equals(token)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        getData.scheduleDataCollection();
    }


}
