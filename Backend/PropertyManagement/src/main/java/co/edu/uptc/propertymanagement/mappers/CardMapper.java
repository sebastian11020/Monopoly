package co.edu.uptc.propertymanagement.mappers;

import co.edu.uptc.propertymanagement.DTOs.CardDTO;
import co.edu.uptc.propertymanagement.entities.Card;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CardMapper {
    CardMapper INSTANCE = Mappers.getMapper(CardMapper.class);
    Card DTOtoCard(CardDTO propertyCardDTO);
    @Mapping(target = "type", expression = "java(card.getClass().getSimpleName())")
    CardDTO cardToDTO(Card card);
}
