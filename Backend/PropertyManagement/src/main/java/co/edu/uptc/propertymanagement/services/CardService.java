package co.edu.uptc.propertymanagement.services;

import co.edu.uptc.propertymanagement.DTOs.PropertyCardDTO;
import co.edu.uptc.propertymanagement.DTOs.TransportDTO;
import co.edu.uptc.propertymanagement.entities.PropertyCard;
import co.edu.uptc.propertymanagement.mappers.PropertyCardMapper;
import co.edu.uptc.propertymanagement.repositories.CardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class CardService {

    @Autowired
    private CardRepository cardRepository;

    public HashMap<String,Object> createTransportCard(TransportDTO transportDTO) {
        HashMap<String,Object> response = new HashMap<>();

        return new HashMap<>();
    }

    public HashMap<String,Object> createPropertyCard(PropertyCardDTO propertyCardDTO) {
        HashMap<String,Object> response = new HashMap<>();
        if (cardRepository.existsByName(propertyCardDTO.getName())){
            cardRepository.save(new PropertyCard());
            response.put("success",true);
            response.put("confirm","Propiedad creada correctamente");
        }
        return new HashMap<>();
    }

    public HashMap<String,Object> updateTransportCard(TransportDTO transportDTO) {
        return  new HashMap<>();
    }
}
