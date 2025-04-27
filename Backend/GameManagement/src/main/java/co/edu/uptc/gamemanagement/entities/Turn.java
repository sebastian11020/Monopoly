package co.edu.uptc.gamemanagement.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Turn {

    public Turn(Game game, int turn, boolean active) {
        this.game = game;
        this.turn = turn;
        this.active = active;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @ManyToOne()
    @JsonBackReference("turn-game")
    private Game game;
    private int turn;
    private boolean active;
}
