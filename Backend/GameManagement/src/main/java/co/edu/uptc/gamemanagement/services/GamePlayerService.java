package co.edu.uptc.gamemanagement.services;

import co.edu.uptc.gamemanagement.DTOs.GamePieceDTOFront;
import co.edu.uptc.gamemanagement.entities.Game;
import co.edu.uptc.gamemanagement.entities.GamePlayer;
import co.edu.uptc.gamemanagement.entities.Piece;
import co.edu.uptc.gamemanagement.entities.Turn;
import co.edu.uptc.gamemanagement.repositories.GamePlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class GamePlayerService {

    @Autowired
    private GamePlayerRepository gamePlayerRepository;

    @Autowired
    private TurnService turnService;

    @Transactional
    public HashMap<String, Object> createGamePlayers(Game game, String nickName) {
        HashMap<String, Object> response = new HashMap<>();
        if (gamePlayerRepository.findByGame_Id(game.getId()).size()>=4){
            response.put("success", false);
            response.put("error","Partida llena");
            response.put("gamePlayer", null);
        }else {
            if (gamePlayerRepository.findByGame_IdAndNickname(game.getId(),nickName)!=null){
                response.put("success", true);
                response.put("confirm", "Jugador reconectado con exito");
                response.put("gamePlayers", getGamePlayers(game.getId()));
            }else {
                response.put("success", true);
                response.put("confirm", "Jugador conectado con exito");
                gamePlayerRepository.save(new GamePlayer(game,nickName,0,1500,
                        turnService.createTurn(game,gamePlayerRepository.findByGame_Id(game.getId()).size()+1)));
                response.put("gamePlayers", getGamePlayers(game.getId()));
            }
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
            response.put("confirm", "Ficha seleccionada");
            response.put("gamePlayer", new GamePieceDTOFront(idGame,gamePlayer.getNickname(),piece.getName()));
        }else {
            response.put("success", false);
            response.put("error", "No se encontro el jugador en la partida");
        }
        return response;
    }

    public List<GamePieceDTOFront> getGamePlayers(int idGame) {
        List<GamePieceDTOFront> gamePiece = new ArrayList<>();
        for (GamePlayer gamePlayer : gamePlayerRepository.findByGame_Id(idGame)){
            if (gamePlayer.getPiece()==null){
                gamePiece.add(new GamePieceDTOFront(idGame,gamePlayer.getNickname(),null));
            }else {
                gamePiece.add(new GamePieceDTOFront(idGame,gamePlayer.getNickname(),gamePlayer.getPiece().getName()));
            }
        }
        return gamePiece;
    }

    public boolean checkPieceGame(int idGame, int idPiece) {
        return gamePlayerRepository.existsByGame_IdAndPiece_Id(idGame,idPiece);
    }

}
