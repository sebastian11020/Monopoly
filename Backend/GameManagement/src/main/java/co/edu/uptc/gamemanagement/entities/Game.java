package co.edu.uptc.gamemanagement.entities;

import co.edu.uptc.gamemanagement.enums.StateGame;
import com.fasterxml.jackson.annotation.JsonBackReference;
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

    public Game(StateGame stateGame) {
        this.stateGame = stateGame;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Enumerated(EnumType.STRING)
    private StateGame stateGame;

    @OneToMany(mappedBy = "game")
    @JsonBackReference
    private List<GamePlayer> gamePlayers;

    @OneToMany(mappedBy = "game")
    private List<GameProperties> gameProperties;

    @OneToMany(mappedBy = "game")
    private List<Turn> turns;
}
