package co.edu.uptc.gamemanagement.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExitGameDTO {
    private int codeGame;
    private String nickName;
}
