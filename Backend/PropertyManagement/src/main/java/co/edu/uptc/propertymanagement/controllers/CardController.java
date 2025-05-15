package co.edu.uptc.propertymanagement.controllers;

import co.edu.uptc.propertymanagement.DTOs.*;
import co.edu.uptc.propertymanagement.entities.Card;
import co.edu.uptc.propertymanagement.services.CardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/Cards")
public class CardController {

    @Autowired
    private CardService cardService;

    @PostMapping("/Create/TransportCard")
    public ResponseEntity<HashMap<String,Object>> createTransportCard(@RequestBody TransportDTO transportDTO) {
        return ResponseEntity.ok(cardService.createTransportCard(transportDTO));
    }

    @PostMapping("/Create/TaxesCard")
    public ResponseEntity<HashMap<String,Object>> createTaxesCard(@RequestBody TaxesCardDTO taxesCardDTO) {
        return ResponseEntity.ok(cardService.createTaxesCard(taxesCardDTO));
    }

    @PostMapping("/Create/PropertyCard")
    public ResponseEntity<HashMap<String,Object>> createPropertyCard(@RequestBody PropertyCardDTO propertyCardDTO) {
        return ResponseEntity.ok(cardService.createPropertyCard(propertyCardDTO));
    }

    @PostMapping("/Create/ServiceCard")
    public ResponseEntity<HashMap<String,Object>> createServiceCard(@RequestBody ServiceCardDTO serviceCardDTO) {
        return ResponseEntity.ok(cardService.createServiceCard(serviceCardDTO));
    }

    @PostMapping("/Create/Card")
    public ResponseEntity<HashMap<String,Object>> createCard(@RequestBody CardDTO cardDTO) {
        return ResponseEntity.ok(cardService.createCard(cardDTO));
    }

    @GetMapping("/All")
    public ResponseEntity<List<CardDTO>> getAllCards(){
        return ResponseEntity.ok(cardService.findAll());
    }

}
