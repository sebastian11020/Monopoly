package co.edu.uptc.playermanagment.mappers;

import co.edu.uptc.playermanagment.DTOs.UserDTO;
import co.edu.uptc.playermanagment.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);
    UserDTO userToDTO(User user);
    User DTOToUser(UserDTO user);
}
