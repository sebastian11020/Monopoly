package co.edu.uptc.propertymanagement.mappers;

import co.edu.uptc.propertymanagement.DTOs.CardDTO;
import co.edu.uptc.propertymanagement.DTOs.PropertyCardDTO;
import co.edu.uptc.propertymanagement.entities.Card;
import co.edu.uptc.propertymanagement.entities.PropertyCard;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CardMapper {
    Card INSTANCE = Mappers.getMapper(Card.class);
    Card DTOtoProperty(CardDTO propertyCardDTO);
    CardDTO propertyToDTO(Card property);
}
