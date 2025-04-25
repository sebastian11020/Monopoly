package co.edu.uptc.propertymanagement.repositories;

import co.edu.uptc.propertymanagement.entities.Property;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PropertyRepository extends JpaRepository<Property, Integer> {
    Property findByPosition(int position);
}
