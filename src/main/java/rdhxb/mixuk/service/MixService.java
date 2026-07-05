package rdhxb.mixuk.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import rdhxb.mixuk.repo.projection.CleanEnergy;
import rdhxb.mixuk.repo.projection.DayTotal;
import rdhxb.mixuk.entity.Interval;
import rdhxb.mixuk.dto.OptimalWindow;
import rdhxb.mixuk.repo.MixRepo;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MixService {

    private final MixRepo repo;


    public void saveData(List<Interval> intervals){
        repo.saveAll(intervals);
    }


    public List<DayTotal> groupIntervals(){
        return repo.groupIntervals();
    }

    public List<CleanEnergy> getCleanIntervals(){
        return repo.findCleanEnergy();
    }


//    Sliding window algorithm to calculate best window for user with most amount of clear energy
    public OptimalWindow optimalCleanWindow(int hours) {
        List<CleanEnergy> cleanEnergies = repo.findCleanEnergy();

        int intervals = hours * 2;
        double max = -1;
        double avg = 0.0;
        List<CleanEnergy> bestWindow = new ArrayList<>();

        for (int i = 0; i <= cleanEnergies.size() - intervals; i++) {
            double cleanSum = 0.0;
            List<CleanEnergy> cleanWindow = new ArrayList<>();

            for (int k = i; k < i + intervals; k++) {
                cleanSum += cleanEnergies.get(k).getCleanEnergy();
                cleanWindow.add(cleanEnergies.get(k));
            }

//            starting time must be in the future
            if (cleanSum > max && !cleanWindow.get(0).getFromTime().isBefore(LocalDateTime.now(ZoneOffset.UTC))) {
                max = cleanSum;
                bestWindow = cleanWindow;
                avg = max / intervals;
            }
        }

        if (bestWindow.isEmpty()){
            return null;
        }

        OptimalWindow optimalWindow = new OptimalWindow();
        optimalWindow.setFromDate(bestWindow.getFirst().getFromTime());
        optimalWindow.setToDate(bestWindow.getLast().getToTime());
        optimalWindow.setAvgCleanEnergy(avg);


        return optimalWindow;
    }




}
