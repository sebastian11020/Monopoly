package co.edu.uptc.gamemanagement.repositories;

import co.edu.uptc.gamemanagement.entities.Game;
import co.edu.uptc.gamemanagement.entities.GameProperties;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GamePropertyRepository extends JpaRepository<GameProperties,Long> {
    List<GameProperties> findByGame(Game game);
}
