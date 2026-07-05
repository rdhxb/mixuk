package rdhxb.mixuk.collectData;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import rdhxb.mixuk.entity.Interval;
import rdhxb.mixuk.service.MixService;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class GetData {

    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();
    private final MixService service;

//   today until +2 days. Runs daily using CRON at 00:00 to collect 3 days of data.
    private LocalDateTime from = LocalDateTime.now(ZoneOffset.UTC).toLocalDate().atStartOfDay();
    private LocalDateTime to = LocalDateTime.now(ZoneOffset.UTC).plusDays(3).toLocalDate().atStartOfDay();


    public void getData() throws IOException, InterruptedException {

//        format dateTime to match API date format API expects ISO 8601 format with 'Z' suffix (UTC)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm'Z'");
        String fromStr = from.format(formatter);
        String toStr = to.format(formatter);

//        Create URL to API
        String URL = "https://api.carbonintensity.org.uk/generation/" + fromStr + "/" + toStr;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URL))
                .header("Content-Type", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        JsonNode mixes = mapper.readTree(response.body());

//        get to data root in json tree
        JsonNode mixesData = mixes.path("data");


        ArrayList<Interval> intervals = new ArrayList<>();

//        go through response tree to create intervals that we define in entity Interval
        for (JsonNode mix : mixesData){
            LocalDateTime intervalFrom = OffsetDateTime.parse(mix.path("from").asString()).toLocalDateTime();

            if (intervalFrom.isBefore(from)) {
                continue;
            }

            JsonNode genMix = mix.path("generationmix");

            Interval interval = new Interval();
            interval.setFrom(OffsetDateTime.parse(mix.path("from").asString()).toLocalDateTime());
            interval.setTo(OffsetDateTime.parse(mix.path("to").asString()).toLocalDateTime());

            interval.setBiomass_pct(getPct(genMix, "biomass"));
            interval.setCoal_pct(getPct(genMix, "coal"));
            interval.setImports_pct(getPct(genMix, "imports"));
            interval.setGas_pct(getPct(genMix, "gas"));
            interval.setNuclear_pct(getPct(genMix, "nuclear"));
            interval.setOther_pct(getPct(genMix, "other"));
            interval.setHydro_pct(getPct(genMix, "hydro"));
            interval.setSolar_pct(getPct(genMix, "solar"));
            interval.setWind_pct(getPct(genMix, "wind"));

            intervals.add(interval);
        }


//        save all data to database could be faster. Is n + 1, but we got not that many data only 3 days shouldn't be a big deal
        service.saveData(intervals);


    }

//    get percent from the json tree going through generationMix -> fuel (biomass) -> perc
    private static double getPct(JsonNode generationMix, String fuel){
        for (JsonNode entry : generationMix){
            if (entry.path("fuel").asString().equals(fuel)){
                return entry.path("perc").asDouble();
            }
        }
        return 0.0;
    }
}
