package co.edu.uptc.propertymanagement.mappers;

import co.edu.uptc.propertymanagement.DTOs.PropertyDTO;
import co.edu.uptc.propertymanagement.entities.Property;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PropertyMapper {
    PropertyMapper INSTANCE = Mappers.getMapper(PropertyMapper.class);
    Property DTOtoProperty(PropertyDTO propertyDTO);
    PropertyDTO propertyToDTO(Property property);
}
