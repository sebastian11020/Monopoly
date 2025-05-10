package co.edu.uptc.propertymanagement.mappers;

import co.edu.uptc.propertymanagement.DTOs.PropertyCardDTO;
import co.edu.uptc.propertymanagement.entities.PropertyCard;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PropertyCardMapper {
    PropertyCard INSTANCE = Mappers.getMapper(PropertyCard.class);
    PropertyCard DTOtoProperty(PropertyCardDTO propertyCardDTO);
    PropertyCardDTO propertyToDTO(PropertyCard property);
}
