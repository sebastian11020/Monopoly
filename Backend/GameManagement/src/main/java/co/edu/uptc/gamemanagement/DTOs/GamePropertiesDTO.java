package co.edu.uptc.gamemanagement.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GamePropertiesDTO {
    private int id;
    private GameDTO game;
    private String nickname;
    private int idProperty;
}
