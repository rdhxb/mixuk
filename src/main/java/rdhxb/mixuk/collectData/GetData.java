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
            Interval interval = new Interval(
                    null,
                    mix.path("from").asString(),
                    mix.path("to").asString(),
                    getPct(genMix, "biomass"),
                    getPct(genMix, "coal"),
                    getPct(genMix, "imports"),
                    getPct(genMix, "gas"),
                    getPct(genMix, "nuclear"),
                    getPct(genMix, "other"),
                    getPct(genMix, "hydro"),
                    getPct(genMix, "solar"),
                    getPct(genMix, "wind")
            );

            intervals.add(interval);
            System.out.println(interval);
        }


//        System.out.println(intervals);
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
