package co.edu.uptc.gamemanagement.client;

import co.edu.uptc.gamemanagement.DTOs.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@FeignClient(name = "PROPERTYMANAGEMENT")
public interface PropertyServiceClient {

    @GetMapping("/Cards/All")
    List<CardDTO> getAllCards();

    @PostMapping("/Cards/GetNamesCards")
    List<String> getNameCards(List<Long> ids);

    @GetMapping("/Cards/Card/{idCard}")
    GenericCard getCard(@PathVariable long idCard);

    @PostMapping("/Cards/Rent")
    Integer getRentCard(CardDTORent cardDTORent);

    @PostMapping("/Cards/PropertyBuilt")
    List<CardToBuiltDTO> getCardsToBuilt(List<Long> idsCards);

    @PostMapping("/Cards/CardBuilt")
    CardToBuiltDTO cardBuilt(Long idCard);

}
