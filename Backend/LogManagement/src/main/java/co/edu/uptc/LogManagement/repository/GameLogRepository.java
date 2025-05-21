package co.edu.uptc.LogManagement.repository;

import co.edu.uptc.LogManagement.model.GameLog;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameLogRepository extends MongoRepository<GameLog, String> {
    List<GameLog> findByGameId(String gameId);
    List<GameLog> findByPlayer(String player);
}
