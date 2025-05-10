package co.edu.uptc.gamemanagement.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
}
