package co.edu.uptc.gamemanagement.mappers;

import co.edu.uptc.gamemanagement.DTOs.PieceDTO;
import co.edu.uptc.gamemanagement.entities.Piece;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-04-29T23:02:55-0500",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 23.0.2 (Amazon.com Inc.)"
)
public class PieceMapperImpl implements PieceMapper {

    @Override
    public Piece DTOToPiece(PieceDTO pieceDTO) {
        if ( pieceDTO == null ) {
            return null;
        }

        Piece piece = new Piece();

        piece.setId( pieceDTO.getId() );
        piece.setName( pieceDTO.getName() );

        return piece;
    }

    @Override
    public PieceDTO PieceToDTO(Piece piece) {
        if ( piece == null ) {
            return null;
        }

        PieceDTO pieceDTO = new PieceDTO();

        pieceDTO.setId( piece.getId() );
        pieceDTO.setName( piece.getName() );

        return pieceDTO;
    }
}
