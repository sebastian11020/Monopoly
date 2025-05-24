package co.edu.uptc.gamemanagement.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SellDTOFront {
    private int codeGame;
    private String nickName;
    private Long idCard;
    private int numberHouses;
    private int numberHotels;
}
