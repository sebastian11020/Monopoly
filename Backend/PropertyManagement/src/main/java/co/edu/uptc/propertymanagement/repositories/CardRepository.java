package co.edu.uptc.propertymanagement.repositories;

import co.edu.uptc.propertymanagement.entities.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {
    boolean existsByName(String name);

    List<Card> findAllByIdIn(List<Long> idsCards);

    Card findCardById(long id);

    PropertyCard findPropertyCardById(long id);

    ServiceCard findServiceCardById(long id);

    TaxesCard findTaxesCardById(long id);

    TransportCard findTransportCardById(long id);
}
