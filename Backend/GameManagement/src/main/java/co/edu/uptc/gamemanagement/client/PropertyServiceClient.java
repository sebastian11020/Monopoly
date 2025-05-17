package co.edu.uptc.gamemanagement.client;

import co.edu.uptc.gamemanagement.DTOs.CardDTO;
import co.edu.uptc.gamemanagement.DTOs.PropertyCard;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@FeignClient(name = "PROPERTYMANAGEMENT")
public interface PropertyServiceClient {

    @GetMapping("/Cards/All")
    List<CardDTO> getAllCards();

    @PostMapping("/Cards/GetNamesCards")
    List<String> getNameCards(List<Long> ids);

    @PostMapping("/Cards/InfoCard")
    PropertyCard getPropertyCard(long idCard);
}
