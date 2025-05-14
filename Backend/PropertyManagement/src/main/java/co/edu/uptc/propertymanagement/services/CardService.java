package co.edu.uptc.propertymanagement.services;

import co.edu.uptc.propertymanagement.DTOs.*;
import co.edu.uptc.propertymanagement.entities.*;
import co.edu.uptc.propertymanagement.mappers.CardMapper;
import co.edu.uptc.propertymanagement.mappers.PropertyGroupMapper;
import co.edu.uptc.propertymanagement.repositories.CardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;

@Service
public class CardService {

    @Autowired
    private PropertyGroupService propertyGroupService;
    @Autowired
    private CardRepository cardRepository;

    public HashMap<String,Object> createTransportCard(TransportDTO transportDTO) {
        HashMap<String,Object> response = new HashMap<>();
        if (!cardRepository.existsByName(transportDTO.getName())){
            TransportCard transportCard = new TransportCard();
            transportCard.setName(transportDTO.getName());
            transportCard.setPosition(transportDTO.getPosition());
            transportCard.setPrice(transportDTO.getPrice());
            transportCard.setMortgagePrice(transportDTO.getMortgagePrice());
            transportCard.setRents(transportDTO.getRents());
            cardRepository.save(transportCard);
            response.put("success",true);
            response.put("confirm","Transporte creado correctamente");
        }else{
            response.put("success",false);
            response.put("error","Ya existe un transporte con el nombre " + transportDTO.getName());
        }
        return response;
    }

    @Transactional
    public HashMap<String,Object> createPropertyCard(PropertyCardDTO propertyCardDTO) {
        HashMap<String,Object> response = new HashMap<>();
        if (!cardRepository.existsByName(propertyCardDTO.getName())){
            PropertyCard propertyCard = new PropertyCard();
            propertyCard.setName(propertyCardDTO.getName());
            propertyCard.setPosition(propertyCardDTO.getPosition());
            propertyCard.setPrice(propertyCardDTO.getPrice());
            propertyCard.setMortgagePrice(propertyCardDTO.getMortgagePrice());
            propertyCard.setRents(propertyCardDTO.getRents());
            propertyCard.setPriceHouse(propertyCardDTO.getPriceHouse());
            propertyCard.setPriceHotel(propertyCardDTO.getPriceHotel());
            propertyCard.setGroup(propertyGroupService.findById(propertyCardDTO.getGroup().getId()));
            cardRepository.save(propertyCard);
            response.put("success",true);
            response.put("confirm","Propiedad creada correctamente");
        }else {
            response.put("success",false);
            response.put("error","Ya existe una propiedad con el nombre " + propertyCardDTO.getName());
        }
        return response;
    }

    public HashMap<String,Object> createServiceCard(ServiceCardDTO serviceCardDTO) {
        HashMap<String,Object> response = new HashMap<>();
        if (!cardRepository.existsByName(serviceCardDTO.getName())){
            ServiceCard serviceCard = new ServiceCard();
            serviceCard.setName(serviceCardDTO.getName());
            serviceCard.setPosition(serviceCardDTO.getPosition());
            serviceCard.setPrice(serviceCardDTO.getPrice());
            serviceCard.setMortgagePrice(serviceCardDTO.getMortgagePrice());
            cardRepository.save(serviceCard);
            response.put("success",true);
            response.put("confirm","Servicio creado correctamente");
        }else {
            response.put("success",false);
            response.put("error","Ya existe un servicio con el nombre " + serviceCardDTO.getName());
        }
        return response;
    }

    public HashMap<String,Object> createTaxesCard(TaxesCardDTO taxesCardDTO) {
        HashMap<String,Object> response = new HashMap<>();
        if(!cardRepository.existsByName(taxesCardDTO.getName())){
            TaxesCard taxesCard = new TaxesCard();
            taxesCard.setName(taxesCardDTO.getName());
            taxesCard.setPosition(taxesCardDTO.getPosition());
            taxesCard.setRent(taxesCardDTO.getRent());
            cardRepository.save(taxesCard);
            response.put("success",true);
            response.put("confirm","Impuesto creado correctamente");
        }else{
            response.put("success",false);
            response.put("error","Ya existe un impuesto con el nombre " + taxesCardDTO.getName());
        }
        return response;
    }

    public HashMap<String, Object> createCard(CardDTO cardDTO) {
        HashMap<String, Object> response = new HashMap<>();
        Card card = new Card();
        card.setName(cardDTO.getName());
        card.setPosition(cardDTO.getPosition());
        cardRepository.save(card);
        response.put("success", true);
        response.put("confirm", "Carta creada correctamente");
        return response;
    }
}
