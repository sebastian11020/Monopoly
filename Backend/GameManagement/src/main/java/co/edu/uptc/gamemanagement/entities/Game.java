package co.edu.uptc.gamemanagement.entities;

import co.edu.uptc.gamemanagement.enums.StateGame;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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
    @JsonManagedReference("game-player")
    private List<GamePlayer> gamePlayers;

    @OneToMany(mappedBy = "game")
    @JsonManagedReference("game-properties")
    private List<GameProperties> gameProperties;

    @OneToMany(mappedBy = "game")
    @JsonManagedReference("game-turn")
    private List<Turn> turns;
}
