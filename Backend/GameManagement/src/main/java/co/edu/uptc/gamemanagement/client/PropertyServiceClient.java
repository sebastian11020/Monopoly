package co.edu.uptc.gamemanagement.client;

import co.edu.uptc.gamemanagement.DTOs.CardDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "PROPERTYMANAGEMENT")
public interface PropertyServiceClient {

    @GetMapping("/Cards/All")
    public List<CardDTO> getAllCards();
}
