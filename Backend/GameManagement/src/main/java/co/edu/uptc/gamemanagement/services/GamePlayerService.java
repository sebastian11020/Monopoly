package co.edu.uptc.gamemanagement.services;

import co.edu.uptc.gamemanagement.DTOs.*;
import co.edu.uptc.gamemanagement.entities.Game;
import co.edu.uptc.gamemanagement.entities.GamePlayer;
import co.edu.uptc.gamemanagement.entities.Piece;
import co.edu.uptc.gamemanagement.entities.Turn;
import co.edu.uptc.gamemanagement.mappers.GamePlayerMapper;
import co.edu.uptc.gamemanagement.repositories.GamePlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@Service
public class GamePlayerService {

    @Autowired
    private GamePlayerRepository gamePlayerRepository;

    @Transactional
    public HashMap<String, Object> createGamePlayers(Game game, String nickName,Turn turn) {
        HashMap<String, Object> response = new HashMap<>();
        if (gamePlayerRepository.findByGame_Id(game.getId()).size()>=4){
            response.put("success", false);
            response.put("error","Partida llena");
            response.put("gamePlayer", null);
        }else {
            if (gamePlayerRepository.findByGame_IdAndNickname(game.getId(),nickName)!=null){
                response.put("success", true);
                response.put("confirm", "Jugador reconectado con exito");
                response.put("gamePlayers", getGamePlayersInWaitingRoom(game.getId()));
            }else {
                response.put("success", true);
                response.put("confirm", "Jugador conectado con exito");
                GamePlayer gamePlayer = gamePlayerRepository.save(new GamePlayer(game,nickName,turn));
                System.out.println("Jugador creado: "+gamePlayer);
                response.put("gamePlayers", getGamePlayersInWaitingRoom(game.getId()));
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
            response.put("gamePlayer", new GamePieceDTOFront(idGame,gamePlayer.getNickname(),piece.getName(),gamePlayer.isState()));
        }else {
            response.put("success", false);
            response.put("error", "No se encontro el jugador en la partida");
        }
        return response;
    }

    public List<GamePieceDTOFront> getGamePlayersInWaitingRoom(int idGame) {
        List<GamePieceDTOFront> gamePiece = new ArrayList<>();
        for (GamePlayer gamePlayer : gamePlayerRepository.findByGame_Id(idGame)){
            if (gamePlayer.getPiece()==null){
                gamePiece.add(new GamePieceDTOFront(idGame,gamePlayer.getNickname(),null,gamePlayer.isState()));
            }else {
                gamePiece.add(new GamePieceDTOFront(idGame,gamePlayer.getNickname(),gamePlayer.getPiece().getName(),gamePlayer.isState()));
            }
        }
        return gamePiece;
    }

    public List<GamePlayerDTO> getGamePlayersInGame(int codeGame) {
        return gamePlayerRepository.findByGame_Id(codeGame).stream().map(GamePlayerMapper.INSTANCE::gamePlayerToDTO).toList();
    }

    public boolean checkPieceGame(int idGame, int idPiece) {
        return gamePlayerRepository.existsByGame_IdAndPiece_Id(idGame,idPiece);
    }

    public GamePlayer existGamePlayerInAGame(String nickName) {
        return gamePlayerRepository.findByNicknameAndGameStatus(nickName, Arrays.asList("EN_ESPERA","JUGANDO"));
    }

    public GamePlayer existPlayerInTheGame(int idGame, String nickName) {
        return gamePlayerRepository.findByGame_IdAndNickname(idGame,nickName);
    }

    public HashMap<String, Object> exitGamePlayerInGame(ExitGameDTO exitGameDTO) {
        HashMap<String, Object> response = new HashMap<>();
        GamePlayer gamePlayer = gamePlayerRepository.findByGame_IdAndNickname(exitGameDTO.getCodeGame(), exitGameDTO.getNickName());
        if (gamePlayer != null) {
            switch (gamePlayer.getGame().getStateGame()) {
                case EN_ESPERA:
                    gamePlayerRepository.delete(gamePlayer);
                    response.put("success", true);
                    response.put("confirm", "El jugador" + gamePlayer.getNickname() + "salio de la sala de espera con exito");
                    response.put("gamePlayers", getGamePlayersInWaitingRoom(exitGameDTO.getCodeGame()));
                    break;
                case JUGANDO:
                    gamePlayerRepository.delete(gamePlayer);
                    response.put("success", true);
                    response.put("confirm", "El jugador" + gamePlayer.getNickname() + "salio de la partida con exito");
                    response.put("gamePlayers", getGamePlayersInGame(exitGameDTO.getCodeGame()));
                    break;
            }
        }
        return response;
    }



    public HashMap<String,Object> changeStateGamePlayer(ChangeStateDTO changeStateDTO){
        HashMap<String,Object> response = new HashMap<>();
        GamePlayer gamePlayer = gamePlayerRepository.findByGame_IdAndNickname(changeStateDTO.getCodeGame(),changeStateDTO.getNickName());
        if (gamePlayer!=null) {
            gamePlayer.setState(changeStateDTO.isState());
            gamePlayerRepository.save(gamePlayer);
            response.put("success", true);
            response.put("confirm", "Estado del jugador cambiado con exito");
            response.put("gamePlayers", getGamePlayersInWaitingRoom(changeStateDTO.getCodeGame()));
        }else {
            response.put("success",false);
            response.put("error","No se encontro el jugador en la partida");
        }
        return response;
    }

    public HashMap<String, Object> turnGamePlayer(GamePlayer gamePlayer) {
        HashMap<String, Object> response = new HashMap<>();
        if (gamePlayer!=null){
            gamePlayerRepository.save(gamePlayer);
            response.put("success", true);
            response.put("confirm", "Turno actualizado");
        }else {
            response.put("success", false);
            response.put("error", "No se pudo actualizar el turno, intente nuevamente");
        }
        return response;
    }

    public void save(GamePlayer gamePlayer){
        gamePlayerRepository.save(gamePlayer);
    }

    public GamePlayer getGamePlayerInGame(int idGame, int idTurn){
        return gamePlayerRepository.findByGame_IdAndTurn_Id(idGame, idTurn);
    }

    public GamePlayer getGamePlayerOwner(int idGame, String nickname){
        return gamePlayerRepository.findByGame_IdAndNickname(idGame,nickname);
    }
}
