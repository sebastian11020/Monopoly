package co.edu.uptc.propertymanagement.mappers;
import co.edu.uptc.propertymanagement.DTOs.PropertyGroupDTO;
import co.edu.uptc.propertymanagement.entities.PropertyGroup;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PropertyGroupMapper {
    PropertyGroupMapper INSTANCE = Mappers.getMapper(PropertyGroupMapper.class);
    PropertyGroup DTOtoPropertyGroup(PropertyGroupDTO propertyGroupDTO);
    PropertyGroupDTO PropertyGroupToDTO(PropertyGroup propertyGroup);
}
