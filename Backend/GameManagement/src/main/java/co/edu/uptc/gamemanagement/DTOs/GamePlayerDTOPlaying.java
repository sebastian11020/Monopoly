package co.edu.uptc.gamemanagement.DTOs;

import co.edu.uptc.gamemanagement.enums.StateCard;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GamePlayerDTOPlaying {
    private int codeGame;
    private String nickName;
    private int dice1;
    private int dice2;
    private int position;
    private int cash;
    private PieceDTO piece;
    private TurnDTO turn;
    private List<String> namesCards;
    private String type;
    private StateCard statePosition;
    private boolean jail;

    public GamePlayerDTOPlaying(int codeGame, String nickName, int dice1, int dice2, int position, int cash, PieceDTO piece, TurnDTO turn) {
        this.codeGame = codeGame;
        this.nickName = nickName;
        this.dice1 = dice1;
        this.dice2 = dice2;
        this.position = position;
        this.cash = cash;
        this.piece = piece;
        this.turn = turn;
    }
}
