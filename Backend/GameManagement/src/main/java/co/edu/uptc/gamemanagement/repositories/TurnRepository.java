package co.edu.uptc.gamemanagement.repositories;

import co.edu.uptc.gamemanagement.entities.Game;
import co.edu.uptc.gamemanagement.entities.Turn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TurnRepository extends JpaRepository<Turn,Integer> {
    List<Turn> findByGame(Game game);

    Turn findByGameAndActive(Game game, boolean b);
}
