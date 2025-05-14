package co.edu.uptc.gamemanagement.services;

import co.edu.uptc.gamemanagement.DTOs.ChangeStateDTO;
import co.edu.uptc.gamemanagement.DTOs.ExitGameDTO;
import co.edu.uptc.gamemanagement.DTOs.GamePieceDTOFront;
import co.edu.uptc.gamemanagement.DTOs.GamePlayerDTOPlaying;
import co.edu.uptc.gamemanagement.entities.Game;
import co.edu.uptc.gamemanagement.entities.GamePlayer;
import co.edu.uptc.gamemanagement.entities.Piece;
import co.edu.uptc.gamemanagement.entities.Turn;
import co.edu.uptc.gamemanagement.mappers.PieceMapper;
import co.edu.uptc.gamemanagement.mappers.TurnMapper;
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
                gamePlayerRepository.save(new GamePlayer(game,nickName,turn));
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

    public List<GamePlayerDTOPlaying> getGamePlayersInGame(int codeGame) {
        List<GamePlayerDTOPlaying> gamePlayerDTOPlayings = new ArrayList<>();
        for (GamePlayer gamePlayer : gamePlayerRepository.findByGame_Id(codeGame)){
            gamePlayerDTOPlayings.add(new GamePlayerDTOPlaying(gamePlayer.getGame().getId(),gamePlayer.getNickname()
                    ,gamePlayer.getDice1(),gamePlayer.getDice2(),gamePlayer.getPosition(),gamePlayer.getCash()
                    ,PieceMapper.INSTANCE.PieceToDTO(gamePlayer.getPiece()), TurnMapper.INSTANCE.TurnToDTO(gamePlayer.getTurn())));
        }
        return gamePlayerDTOPlayings;
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

    public HashMap<String,Object> exitGamePlayerInGame(ExitGameDTO exitGameDTO){
        HashMap<String,Object> response = new HashMap<>();
        GamePlayer gamePlayer = gamePlayerRepository.findByGame_IdAndNickname(exitGameDTO.getCodeGame(),exitGameDTO.getNickName());
        if (gamePlayer!=null) {
            gamePlayerRepository.delete(gamePlayer);
            response.put("success", true);
            response.put("confirm", "Jugador salio de la partida con exito");
            response.put("gamePlayers", getGamePlayersInWaitingRoom(exitGameDTO.getCodeGame()));
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

    public HashMap<String, Object> TurnGamePlayer(int idGame) {
        HashMap<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("confirm", "Turno actualizado");
        response.put("gamePlayers", getGamePlayersInGame(idGame));
        return response;
    }

    public void advancePosition(int idGame, int idTurn, int[] valueDice) {
        if (idTurn != -1) {
            GamePlayer gamePlayer = gamePlayerRepository.findByGame_IdAndTurn_Id(idGame, idTurn);
            if (gamePlayer != null) {
                gamePlayer.setDice1(valueDice[0]);
                gamePlayer.setDice2(valueDice[1]);
                checkPairs(gamePlayer);
                if (gamePlayer.isInJail()) {
                    exitJail(gamePlayer);
                } else {
                    int position = gamePlayer.getPosition();
                    position += gamePlayer.getDice1() + gamePlayer.getDice2();
                    if (gamePlayer.getNumberOfPairs() == 3) {
                        gamePlayer.setInJail(true);
                        gamePlayer.setPosition(10);
                        gamePlayer.setNumberOfPairs(0);
                    } else {
                        if (position <= 39) {
                            gamePlayer.setPosition(position);
                        } else {
                            gamePlayer.setPosition(position - 39);
                            gamePlayer.setCash(gamePlayer.getCash() + 200);
                        }
                    }
                }
                gamePlayerRepository.save(gamePlayer);
            }
        }
    }

    private void exitJail(GamePlayer gamePlayer){
        if (gamePlayer.isInJail()){
            if (gamePlayer.getDice1()==gamePlayer.getDice2()){
                gamePlayer.setInJail(false);
                gamePlayer.setPosition(gamePlayer.getPosition()+(gamePlayer.getDice1()+gamePlayer.getDice2()));
            }
        }
    }

    private void checkPairs(GamePlayer gamePlayer){
        if (gamePlayer.getDice1()==gamePlayer.getDice2()){
            gamePlayer.setNumberOfPairs(gamePlayer.getNumberOfPairs()+1);
        }else {
            gamePlayer.setNumberOfPairs(0);
        }
    }
}
