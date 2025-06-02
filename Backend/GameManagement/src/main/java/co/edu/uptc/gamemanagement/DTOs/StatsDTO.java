package co.edu.uptc.gamemanagement.DTOs;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatsDTO {
    private String playerId;
    private String playerName;
    private int moneyWon;
    private java.time.LocalDateTime dateTime;
    private String gameId;
    private String nameWinner;


    private String propertyId;
    private String propertyName;
    private int acquiredCount;

}