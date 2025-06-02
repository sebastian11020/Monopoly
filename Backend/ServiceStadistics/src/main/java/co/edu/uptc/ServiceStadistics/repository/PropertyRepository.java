package co.edu.uptc.ServiceStadistics.repository;

import co.edu.uptc.ServiceStadistics.model.PropertyNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PropertyRepository extends Neo4jRepository<PropertyNode, String> {
}
