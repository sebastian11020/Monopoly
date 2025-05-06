package co.edu.uptc.gamemanagement.mappers;

import co.edu.uptc.gamemanagement.DTOs.TurnDTO;
import co.edu.uptc.gamemanagement.entities.Turn;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface TurnMapper {
    TurnMapper INSTANCE = Mappers.getMapper(TurnMapper.class);
    Turn DTOToTurn(TurnDTO turnDTO);
    TurnDTO TurnToDTO(Turn turn);
}
