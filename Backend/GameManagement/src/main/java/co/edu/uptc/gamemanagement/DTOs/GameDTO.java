package co.edu.uptc.gamemanagement.DTOs;

import co.edu.uptc.gamemanagement.enums.StateGame;
import com.google.firebase.database.annotations.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GameDTO {
    private int id;
    private StateGame stateGame;
}
