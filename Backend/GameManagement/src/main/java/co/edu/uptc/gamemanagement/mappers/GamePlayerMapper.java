package co.edu.uptc.gamemanagement.mappers;

import co.edu.uptc.gamemanagement.DTOs.GamePlayerDTO;
import co.edu.uptc.gamemanagement.entities.GamePlayer;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface GamePlayerMapper {
    GamePlayerMapper INSTANCE = Mappers.getMapper(GamePlayerMapper.class);
    GamePlayer DTOToGamePlayer(GamePlayerDTO gamePlayerDTO);
    GamePlayerDTO gamePlayerToDTO(GamePlayer gamePlayer);
}
