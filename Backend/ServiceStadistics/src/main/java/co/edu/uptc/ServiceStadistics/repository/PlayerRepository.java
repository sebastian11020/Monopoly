package co.edu.uptc.ServiceStadistics.repository;

import co.edu.uptc.ServiceStadistics.model.PlayerNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerRepository extends Neo4jRepository<PlayerNode, String> {
}