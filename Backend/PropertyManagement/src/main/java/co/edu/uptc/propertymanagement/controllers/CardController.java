package co.edu.uptc.propertymanagement.controllers;

import co.edu.uptc.propertymanagement.DTOs.*;
import co.edu.uptc.propertymanagement.entities.*;
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
    public ResponseEntity<HashMap<String,Object>> createTransportCard(@RequestBody TransportCardDTO transportDTO) {
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

    @PostMapping("/GetNamesCards")
    public ResponseEntity<List<String>> getNameCards(@RequestBody List<Long> idsCards){
        return ResponseEntity.ok(cardService.getNamesCards(idsCards));
    }

    @GetMapping("/Card/{idCard}")
    public ResponseEntity<CardDTO> getCardById(@PathVariable Long idCard){
        return ResponseEntity.ok(cardService.findCardById(idCard));
    }

    @GetMapping("/PropertyCard/{idCard}")
    public ResponseEntity<PropertyCardDTO> getPropertyCardById(@PathVariable Long idCard){
        return ResponseEntity.ok(cardService.findPropertyCardById(idCard));
    }

    @GetMapping("/TaxesCard/{idCard}")
    public ResponseEntity<TaxesCardDTO> getTaxesCardById(@PathVariable Long idCard){
        return ResponseEntity.ok(cardService.findTaxesCardById(idCard));
    }

    @GetMapping("/TransportCard/{idCard}")
    public ResponseEntity<TransportCardDTO> getTransportCardById(@PathVariable Long idCard){
        return ResponseEntity.ok(cardService.findTransportCardById(idCard));
    }

    @GetMapping("/ServiceCard/{idCard}")
    public ResponseEntity<ServiceCardDTO> getServiceCardById(@PathVariable Long idCard){
        return ResponseEntity.ok(cardService.findServiceCardById(idCard));
    }

    @PostMapping("/PropertyCard/Rent")
    public ResponseEntity<Integer> getRentPropertyCardById(@RequestBody PropertyCardDTORent propertyCardDTORent){
        return ResponseEntity.ok(cardService.findByRentProperties(propertyCardDTORent));
    }

    @PostMapping("/TaxesCard/Rent")
    public ResponseEntity<Integer> getRentTaxesCardById(@RequestBody Long idCard){
        return ResponseEntity.ok(cardService.findByRentTaxes(idCard));
    }

    @PostMapping("/TransportCard/Rent")
    public ResponseEntity<Integer> getRentTransportCardById(@RequestBody TransportCardDTORent transportCardDTORent){
        return ResponseEntity.ok(cardService.findByRentTransport(transportCardDTORent));
    }

    @PostMapping("/ServiceCard/Rent")
    public ResponseEntity<Integer> getRentServiceCardById(@RequestBody ServiceCardDTORent serviceCardDTORent){
        return ResponseEntity.ok(cardService.findByMultiplicator(serviceCardDTORent));
    }

}
