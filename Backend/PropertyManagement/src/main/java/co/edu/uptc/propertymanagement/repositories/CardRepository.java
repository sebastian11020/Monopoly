package co.edu.uptc.propertymanagement.repositories;

import co.edu.uptc.propertymanagement.entities.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CardRepository extends JpaRepository<Card, Integer> {
    boolean existsByName(String name);
}
