package co.edu.uptc.gamemanagement.client;

import co.edu.uptc.gamemanagement.DTOs.StatsDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;
import org.springframework.stereotype.Component;

@Component
public class StadisticServiceClient {
    private final RestTemplate restTemplate;
    private final String statsServiceUrl;

    public StadisticServiceClient(RestTemplate restTemplate,
                                  @Value("${stats.service.url:http://ServiceStadistics/Stats/player}") String statsServiceUrl) {
        this.restTemplate = restTemplate;
        this.statsServiceUrl = statsServiceUrl;
    }

    public void sendStats(StatsDTO statsDTO) {
        try {
            System.out.println("Sending stats: " + statsDTO);
            HttpEntity<StatsDTO> request = new HttpEntity<>(statsDTO);
            restTemplate.postForEntity(statsServiceUrl, request, Void.class);
            System.out.println("Stats sent successfully");
        } catch (RestClientException e) {
            System.err.println("Failed to send stats: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

