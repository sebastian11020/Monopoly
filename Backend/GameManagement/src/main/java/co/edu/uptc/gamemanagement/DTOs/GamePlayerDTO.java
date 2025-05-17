package co.edu.uptc.gamemanagement.DTOs;

import co.edu.uptc.gamemanagement.entities.Game;
import co.edu.uptc.gamemanagement.entities.Piece;
import co.edu.uptc.gamemanagement.entities.Turn;
import co.edu.uptc.gamemanagement.enums.StateGame;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GamePlayerDTO {
    private int id;
    private GameDTO game;
    private PieceDTO piece;
    private TurnDTO turn;
    private int dice1;
    private int dice2;
    private String nickname;
    private int position;
    private int cash;
    private boolean state;
    private int numberOfPairs;
    private boolean isInJail;
    private int houses;
    private int hotels;
}
