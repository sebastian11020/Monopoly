package co.edu.uptc.propertymanagement.mappers;

import co.edu.uptc.propertymanagement.DTOs.RentDTO;
import co.edu.uptc.propertymanagement.entities.Rent;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface RentMapper {
    RentMapper INSTANCE = Mappers.getMapper(RentMapper.class);
    Rent DTOtoRent(RentDTO rentDTO);
    RentDTO RentToDTO(Rent rent);
}
