package co.edu.uptc.gamemanagement.repositories;

import co.edu.uptc.gamemanagement.entities.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameRepository extends JpaRepository<Game, Integer> {
    Game findById(int id);

    int id(int id);
}
