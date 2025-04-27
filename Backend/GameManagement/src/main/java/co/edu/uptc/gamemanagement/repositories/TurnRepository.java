package co.edu.uptc.gamemanagement.repositories;

import co.edu.uptc.gamemanagement.entities.Turn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TurnRepository extends JpaRepository<Turn,Integer> {
}
