package co.edu.uptc.gamemanagement.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RollDiceDTO {
    private int codeGame;
    private int dice1;
    private int dice2;
}
