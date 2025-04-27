package co.edu.uptc.gamemanagement.entities;

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
@Table(
        uniqueConstraints = @UniqueConstraint(columnNames = {"game_id", "piece_id"})
)
public class GamePlayer {

    public GamePlayer(Game game,String nickname, int position, int cash,Turn turn) {
        this.game = game;
        this.nickname = nickname;
        this.position = position;
        this.cash = cash;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JsonBackReference("game-player")
    private Game game;

    @ManyToOne(fetch = FetchType.EAGER)
    @JsonBackReference("game-piece")
    private Piece piece;

    @OneToOne(cascade = CascadeType.ALL)
    private Turn turns;

    private String nickname;
    private int position;
    private int cash;
}
