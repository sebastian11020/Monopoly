package co.edu.uptc.gamemanagement.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GamePieceDTOFront {
    private int idGame;
    private String nickName;
    private String namePiece;
    private boolean state;
}
