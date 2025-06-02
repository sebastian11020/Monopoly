package co.edu.uptc.gamemanagement.client;

import co.edu.uptc.gamemanagement.DTOs.LogDTO;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class LogServiceClient {
    private final RestTemplate restTemplate;

    public LogServiceClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void sendLog(LogDTO logDTO) {
        String url = "http://LogManagement" + "/Logs";
        HttpEntity<LogDTO> request = new HttpEntity<>(logDTO );
        restTemplate.postForEntity(url, request, Void.class);
    }
}

