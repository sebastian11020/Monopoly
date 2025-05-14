package co.edu.uptc.propertymanagement.services;

import co.edu.uptc.propertymanagement.DTOs.PropertyGroupDTO;
import co.edu.uptc.propertymanagement.entities.PropertyGroup;
import co.edu.uptc.propertymanagement.mappers.PropertyGroupMapper;
import co.edu.uptc.propertymanagement.repositories.PropertyGroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class PropertyGroupService {

    @Autowired
    private PropertyGroupRepository propertyGroupRepository;

    public HashMap<String,Object> createPropertyGroup(PropertyGroupDTO propertyGroupDTO) {
        HashMap<String, Object> map = new HashMap<>();
        if (!propertyGroupRepository.existsByColor(propertyGroupDTO.getColor())){
            propertyGroupRepository.save(PropertyGroupMapper.INSTANCE.DTOtoPropertyGroup(propertyGroupDTO));
            map.put("success", true);
            map.put("confim", "Se creo correctamente el grupo");
        }else {
            map.put("success", false);
            map.put("error","Ya existe un grupo con el color " + propertyGroupDTO.getColor());
        }
        return map;
    }

    public PropertyGroup findById(int id) {
        return propertyGroupRepository.findById(id).orElse(null);
    }

}
