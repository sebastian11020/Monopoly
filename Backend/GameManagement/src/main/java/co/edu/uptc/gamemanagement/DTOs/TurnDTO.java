package co.edu.uptc.gamemanagement.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TurnDTO {
    private int id;
    private GameDTO game;
    private int turn;
    private boolean active;
}
