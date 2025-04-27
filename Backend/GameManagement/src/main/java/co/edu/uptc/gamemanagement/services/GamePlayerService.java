package co.edu.uptc.gamemanagement.services;

import co.edu.uptc.gamemanagement.DTOs.GamePlayerDTOFront;
import co.edu.uptc.gamemanagement.entities.Game;
import co.edu.uptc.gamemanagement.entities.GamePlayer;
import co.edu.uptc.gamemanagement.mappers.PieceMapper;
import co.edu.uptc.gamemanagement.repositories.GamePlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class GamePlayerService {

    @Autowired
    private GamePlayerRepository gamePlayerRepository;

    public HashMap<String, Object> createGamePlayers(Game game, String nickName) {
        HashMap<String, Object> response = new HashMap<>();
        if (gamePlayerRepository.findByGame_Id(game.getId()).size()>=4){
            response.put("success", false);
            response.put("error","Partida llena");
            response.put("gamePlayer", null);
        }else {
            response.put("success", true);
            response.put("confirm", "Jugador creado con exito");
            response.put("gamePlayer", gamePlayerRepository.save(new GamePlayer(game,nickName,0,1500)));
        }
        return response;
    }

    public GamePlayer searchGamePlayer(String nickName, int idGame) {
        return gamePlayerRepository.findByGame_IdAndNickname(idGame,nickName);
    }

    public boolean checkPieceGame(int idGame, int idPiece) {
        return gamePlayerRepository.existsByGame_IdAndPiece_Id(idGame,idPiece);
    }

}
