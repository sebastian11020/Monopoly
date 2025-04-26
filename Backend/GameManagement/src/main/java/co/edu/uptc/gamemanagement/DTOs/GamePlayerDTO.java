package co.edu.uptc.gamemanagement.DTOs;

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
    private String nickname;
    private int position;
    private int cash;

}
