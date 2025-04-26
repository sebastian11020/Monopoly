package co.edu.uptc.gamemanagement.repositories;

import co.edu.uptc.gamemanagement.entities.Piece;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PieceRepository extends JpaRepository<Piece, Integer> {
    boolean existsByName(String name);

    Piece findById(int id);
}
