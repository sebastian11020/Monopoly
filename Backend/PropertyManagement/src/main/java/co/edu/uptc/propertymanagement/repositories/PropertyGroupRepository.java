package co.edu.uptc.propertymanagement.repositories;

import co.edu.uptc.propertymanagement.entities.PropertyGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PropertyGroupRepository extends JpaRepository<PropertyGroup, Integer> {
    boolean existsByColor(String color);

    PropertyGroup findPropertyGroupByColor(String color);
}
