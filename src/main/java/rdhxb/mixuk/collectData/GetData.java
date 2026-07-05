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
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class GetData {

    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();
    private final MixService service;


    public void getData() throws IOException, InterruptedException {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.carbonintensity.org.uk/generation/2026-01-20T12:00Z/2026-01-20T12:30Z"))
                .header("Content-Type", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        JsonNode mixes = mapper.readTree(response.body());
        JsonNode mixesData = mixes.path("data");



        ArrayList<Interval> intervals = new ArrayList<>();

        for (JsonNode mix : mixesData){
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


        service.saveData(intervals);


    }

    private static double getPct(JsonNode generationMix, String fuel){
        for (JsonNode entry : generationMix){
            if (entry.path("fuel").asString().equals(fuel)){
                return entry.path("perc").asDouble();
            }
        }
        return 0.0;
    }
}
