package co.edu.uptc.gamemanagement.entities;

import co.edu.uptc.gamemanagement.DTOs.CardDTO;
import co.edu.uptc.gamemanagement.enums.StateCard;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class GameProperties {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @ManyToOne()
    private Game game;
    private String nickname;
    private long idCard;
    private long position;
    private String type;
    private StateCard stateCard;
    private int houses;
    private int hotels;
}
