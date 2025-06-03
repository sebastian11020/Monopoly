package co.edu.uptc.ServiceStadistics.repository;

import co.edu.uptc.ServiceStadistics.model.GameNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameRepository extends Neo4jRepository<GameNode, String> {
}
