package co.edu.uptc.propertymanagement.services;

import co.edu.uptc.propertymanagement.DTOs.PropertyDTO;
import co.edu.uptc.propertymanagement.entities.Property;
import co.edu.uptc.propertymanagement.mappers.PropertyMapper;
import co.edu.uptc.propertymanagement.repositories.PropertyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class PropertyService {

    @Autowired
    private PropertyRepository propertyRepository;

    public Map<String, Object> createProperty(PropertyDTO propertyDTO) {
        Map<String, Object> response = new HashMap<>();
        if (!propertyRepository.existsById(propertyDTO.getId())) {
            propertyRepository.save(PropertyMapper.INSTANCE.DTOtoProperty(propertyDTO));
            response.put("success",true);
            response.put("confirm", "Propiedad creada con exito");
        }
        return response;
    }

    public Map<String, Object> searchProperty(int position) {
        Map<String, Object> response = new HashMap<>();
        Property property = propertyRepository.findByPosition(position);
        if (property != null) {
            response.put("success", true);
            response.put("property", property);
        }else {
            response.put("success", false);
            response.put("error", "No se encontro una propiedad en la posicion "+position);
        }
        return response;
    }

}
