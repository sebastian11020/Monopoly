package co.edu.uptc.gamemanagement.services;

import co.edu.uptc.gamemanagement.entities.Game;
import co.edu.uptc.gamemanagement.entities.GamePlayer;
import co.edu.uptc.gamemanagement.entities.Piece;
import co.edu.uptc.gamemanagement.entities.Turn;
import co.edu.uptc.gamemanagement.repositories.GamePlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;

@Service
public class GamePlayerService {

    @Autowired
    private GamePlayerRepository gamePlayerRepository;

    @Transactional
    public HashMap<String, Object> createGamePlayers(Game game, String nickName) {
        HashMap<String, Object> response = new HashMap<>();
        if (gamePlayerRepository.findByGame_Id(game.getId()).size()>=4){
            response.put("success", false);
            response.put("error","Partida llena");
            response.put("gamePlayer", null);
        }else {
            response.put("success", true);
            response.put("confirm", "Jugador creado con exito");
            GamePlayer gamePlayer = gamePlayerRepository.save(new GamePlayer(game,nickName,0,1500,
                    new Turn(game,gamePlayerRepository.findByGame_Id(game.getId()).size()+1,false)));
            response.put("gamePlayer", gamePlayer);
        }
        return response;
    }

    public HashMap<String,Object> SelectPieceGamePlayer(String nickName, int idGame, Piece piece) {
        HashMap<String,Object> response = new HashMap<>();
        GamePlayer gamePlayer = gamePlayerRepository.findByGame_IdAndNickname(idGame,nickName);
        if (gamePlayer != null) {
            gamePlayer.setPiece(piece);
            gamePlayerRepository.save(gamePlayer);
            response.put("success", true);
            response.put("gamePlayer", gamePlayer);
        }else {
            response.put("success", false);
            response.put("error", "No se encontro el jugador en la partida");
        }
        return response;
    }

    public boolean checkPieceGame(int idGame, int idPiece) {
        return gamePlayerRepository.existsByGame_IdAndPiece_Id(idGame,idPiece);
    }

}
