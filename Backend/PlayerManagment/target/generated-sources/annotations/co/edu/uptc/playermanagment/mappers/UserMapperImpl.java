package co.edu.uptc.playermanagment.mappers;

import co.edu.uptc.playermanagment.DTOs.UserDTO;
import co.edu.uptc.playermanagment.entities.User;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-04-25T21:24:19-0500",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 23.0.2 (Amazon.com Inc.)"
)
public class UserMapperImpl implements UserMapper {

    @Override
    public UserDTO userToDTO(User user) {
        if ( user == null ) {
            return null;
        }

        UserDTO userDTO = new UserDTO();

        userDTO.setNickname( user.getNickname() );
        userDTO.setPassword( user.getPassword() );
        userDTO.setEmail( user.getEmail() );

        return userDTO;
    }

    @Override
    public User DTOToUser(UserDTO user) {
        if ( user == null ) {
            return null;
        }

        User user1 = new User();

        user1.setNickname( user.getNickname() );
        user1.setPassword( user.getPassword() );
        user1.setEmail( user.getEmail() );

        return user1;
    }
}
