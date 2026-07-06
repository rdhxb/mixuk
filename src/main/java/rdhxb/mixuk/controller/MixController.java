package rdhxb.mixuk.controller;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
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

    @GetMapping("/dayMix")
    public List<DayTotal> getDatyTotalMix(){
        return service.groupIntervals();
    }

    @GetMapping("/cleanIntervals")
    public List<CleanEnergy> getCleanIntervals(){
        return service.getCleanIntervals();
    }

    @GetMapping("/window")
    public OptimalWindow getWindow(@RequestParam @Min(1) @Max(6) int hours){
        return service.optimalCleanWindow(hours);
    }


}
