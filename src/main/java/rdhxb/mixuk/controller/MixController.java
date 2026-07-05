package rdhxb.mixuk.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rdhxb.mixuk.entity.DayTotal;
import rdhxb.mixuk.service.MixService;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MixController {

    private final MixService service;

    @GetMapping("/dayMix")
    public List<DayTotal> getDatyTotalMix(){
        return service.groupIntervals();
    }


}
