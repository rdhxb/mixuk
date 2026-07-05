package rdhxb.mixuk.service;

import lombok.RequiredArgsConstructor;
import rdhxb.mixuk.entity.CleanEnergy;
import rdhxb.mixuk.entity.DayTotal;
import rdhxb.mixuk.entity.Interval;
import rdhxb.mixuk.repo.MixRepo;

import java.util.List;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class MixService {

    private final MixRepo repo;


    public void saveData(List<Interval> intervals){
        repo.saveAll(intervals);
    }


    public List<DayTotal> groupIntervals(){
        return repo.groupIntervals();
    }




}
