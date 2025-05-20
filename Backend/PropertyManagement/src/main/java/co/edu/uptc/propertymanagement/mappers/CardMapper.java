package co.edu.uptc.propertymanagement.mappers;

import co.edu.uptc.propertymanagement.DTOs.*;
import co.edu.uptc.propertymanagement.entities.*;
import org.hibernate.query.sql.spi.ParameterOccurrence;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CardMapper {
    CardMapper INSTANCE = Mappers.getMapper(CardMapper.class);
    Card DTOtoCard(CardDTO propertyCardDTO);
    @Mapping(target = "type", source = "type")
    CardDTO cardToDTO(Card card);
    PropertyCardDTO cardToPropertyCardDTO(PropertyCard card);
    PropertyCard propertyCardDTOToCard(PropertyCardDTO propertyCardDTO);
    TransportCardDTO cardToTransportCardDTO(TransportCard card);
    TransportCard transportCardDTOToCard(TransportCardDTO transportCardDTO);
    ServiceCardDTO cardToServiceCardDTO(ServiceCard card);
    ServiceCard serviceCardDTOToCard(ServiceCardDTO serviceCardDTO);
    TaxesCardDTO cardToTaxesCardDTO(TaxesCard card);
    TaxesCard taxesCardDTOToCard(TaxesCardDTO taxesCardDTO);
    GenericCardDTO propertyCardToGenericCardDTO(PropertyCard propertyCard);
    GenericCardDTO transportCardToGenericCardDTO(TransportCard transportCard);
    GenericCardDTO serviceCardToGenericCardDTO(ServiceCard serviceCard);
    GenericCardDTO taxesCardToGenericCardDTO(TaxesCard taxesCard);
    GenericCardDTO cardToGenericCardDTO(Card card);
}
