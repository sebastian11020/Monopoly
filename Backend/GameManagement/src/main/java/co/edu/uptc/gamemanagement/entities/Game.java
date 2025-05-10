package co.edu.uptc.gamemanagement.entities;

import co.edu.uptc.gamemanagement.enums.StateGame;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Game {

    public Game(StateGame stateGame,String nickName) {
        this.stateGame = stateGame;
        this.nickName = nickName;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Enumerated(EnumType.STRING)
    private StateGame stateGame;

    private String nickName;

    @OneToMany(mappedBy = "game",fetch = FetchType.EAGER)
    @JsonManagedReference("game-player")
    @ToString.Exclude
    private List<GamePlayer> gamePlayers = new ArrayList<>();

    @OneToMany(mappedBy = "game",fetch = FetchType.EAGER)
    @JsonManagedReference("game-properties")
    @ToString.Exclude
    private List<GameProperties> gameProperties = new ArrayList<>();

    @OneToMany(mappedBy = "game", fetch = FetchType.EAGER)
    @JsonManagedReference("game-turn")
    private List<Turn> turns = new ArrayList<>();

    private String winnerNickName;
}
