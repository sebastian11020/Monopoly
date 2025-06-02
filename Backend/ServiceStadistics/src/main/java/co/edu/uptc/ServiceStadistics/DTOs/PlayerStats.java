package co.edu.uptc.ServiceStadistics.DTOs;

import lombok.*;



@Data
@AllArgsConstructor
@NoArgsConstructor

public class PlayerStats {
    private String playerId;
    private String playerName;
    private int moneyWon;
    private java.time.LocalDateTime dateTime;
    private String nameWinner;
    private String gameId;
    private String propertyId;
    private String propertyName;
    private int acquiredCount;

}