package co.edu.uptc.gamemanagement.entities;

import co.edu.uptc.gamemanagement.entities.keyEmbedded.GamePlayerKey;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class GamePlayer {

    @EmbeddedId
    private GamePlayerKey idGamePlayerKey;

    @ManyToOne()
    @MapsId("idGame")
    private Game game;

    @ManyToOne()
    @MapsId("idPiece")
    private Piece piece;

    private int idPlayer;
    private int position;
    private int cash;
}
