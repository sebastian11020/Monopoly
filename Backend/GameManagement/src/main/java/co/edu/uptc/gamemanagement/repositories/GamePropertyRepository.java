package co.edu.uptc.gamemanagement.repositories;

import co.edu.uptc.gamemanagement.DTOs.CardDTO;
import co.edu.uptc.gamemanagement.entities.Game;
import co.edu.uptc.gamemanagement.entities.GameProperties;
import co.edu.uptc.gamemanagement.enums.StateCard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GamePropertyRepository extends JpaRepository<GameProperties,Long> {
    List<GameProperties> findByGame(Game game);

    List<GameProperties> findByGame_IdAndNickname(int gameId, String nickname);

    GameProperties findByGame_IdAndIdCard(int gameId, long idCard);
}
