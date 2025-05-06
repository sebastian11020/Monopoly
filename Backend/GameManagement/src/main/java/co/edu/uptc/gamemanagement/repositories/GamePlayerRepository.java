package co.edu.uptc.gamemanagement.repositories;

import co.edu.uptc.gamemanagement.entities.Game;
import co.edu.uptc.gamemanagement.entities.GamePlayer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GamePlayerRepository extends JpaRepository<GamePlayer, Integer> {
    List<GamePlayer> findByCashBefore(int cashBefore);

    List<GamePlayer> findByGame_Id(int gameId);

    List<GamePlayer> findByGame(Game game);

    boolean existsByGame_IdAndPiece_Id(int gameId, int pieceId);

    GamePlayer findByGame_IdAndNickname(int gameId, String nickname);

    @Query("SELECT gp FROM GamePlayer gp WHERE gp.nickname = :nickname AND gp.game.stateGame IN :statuses")
    GamePlayer findByNicknameAndGameStatus(@Param("nickname") String nickname, @Param("statuses") List<String> statuses);

    GamePlayer findByTurn_Id(int turnId);
}
