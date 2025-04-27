package co.edu.uptc.gamemanagement.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Turn {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne()
    private Game game;
    @OneToOne()
    private GamePlayer player;
    private int turn;
    private boolean active;
}
