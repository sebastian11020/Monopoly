package co.edu.uptc.gamemanagement.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(
        uniqueConstraints = @UniqueConstraint(columnNames = {"game_id", "piece_id"})
)
public class GamePlayer {

    public GamePlayer(Game game,String nickname, Turn turn) {
        this.game = game;
        this.nickname = nickname;
        this.position = 0;
        this.turn = turn;
        this.cash = 1500;
        this.state = false;
        this.dice1=0;
        this.dice2=0;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JsonBackReference("game-player")
    @ToString.Exclude
    private Game game;

    @ManyToOne(fetch = FetchType.EAGER)
    @JsonBackReference("game-piece")
    private Piece piece;

    @OneToOne(cascade = CascadeType.ALL)
    private Turn turn;
    private int dice1;
    private int dice2;
    private String nickname;
    private int position;
    private int cash;
    private boolean state;

}
