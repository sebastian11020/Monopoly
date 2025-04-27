package co.edu.uptc.gamemanagement.entities;

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

    public GamePlayer(Game game,String nickname, int position, int cash) {
        this.game = game;
        this.nickname = nickname;
        this.position = position;
        this.cash = cash;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne()
    @JsonManagedReference
    private Game game;

    @ManyToOne()
    @JsonManagedReference
    private Piece piece;

    @OneToOne(mappedBy = "player",cascade = CascadeType.ALL)
    private Turn turns;

    private String nickname;
    private int position;
    private int cash;
}
