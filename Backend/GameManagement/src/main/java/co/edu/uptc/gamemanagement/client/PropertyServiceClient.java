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
    PropertyCard getCard(@PathVariable long idCard);

    @GetMapping("/Cards/PropertyCard/{idCard}")
    PropertyCard getPropertyCard(@PathVariable long idCard);

    @GetMapping("/Cards/ServiceCard/{idCard}")
    PropertyCard getServiceCard(@PathVariable long idCard);

    @GetMapping("/Cards/TaxesCard/{idCard}")
    PropertyCard getTaxesCard(@PathVariable long idCard);

    @GetMapping("/Cards/TransportCard/{idCard}")
    PropertyCard getTransportCard(@PathVariable long idCard);

    @PostMapping("/Cards/PropertyCard/Rent")
    int getRentPropertyCard(PropertyCardDTORent propertyCardDTORent);

    @PostMapping("/Cards/ServiceCard/Rent")
    int getRentServiceCard(ServiceCardDTORent serviceCardDTORent);

    @PostMapping("/Cards/TaxesCard/Rent")
    int getRentTaxesCard(long idCard);

    @PostMapping("/Cards/TransportCard/Rent")
    int getRentTransportCard(TransportCardDTORent  transportCardDTORent);

}
