package co.edu.uptc.gamemanagement.mappers;

import co.edu.uptc.gamemanagement.DTOs.PieceDTO;
import co.edu.uptc.gamemanagement.entities.Piece;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PieceMapper {
    PieceMapper INSTANCE = Mappers.getMapper(PieceMapper.class);
    Piece DTOToPiece(PieceDTO pieceDTO);
    PieceDTO PieceToDTO(Piece piece);
}
