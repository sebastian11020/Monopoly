package co.edu.uptc.gamemanagement.entities;

import co.edu.uptc.gamemanagement.enums.StateGame;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Enumerated(EnumType.STRING)
    private StateGame game;

    @OneToMany(mappedBy = "game")
    private List<GamePlayer> gamePlayers;

    @OneToMany(mappedBy = "game")
    private List<GameProperties> gameProperties;
}
