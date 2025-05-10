package co.edu.uptc.gamemanagement.services;

import co.edu.uptc.gamemanagement.DTOs.ChangeStateDTO;
import co.edu.uptc.gamemanagement.DTOs.ExitGameDTO;
import co.edu.uptc.gamemanagement.DTOs.GamePieceDTOFront;
import co.edu.uptc.gamemanagement.DTOs.GamePlayerDTOFront;
import co.edu.uptc.gamemanagement.entities.Game;
import co.edu.uptc.gamemanagement.entities.GamePlayer;
import co.edu.uptc.gamemanagement.entities.Turn;
import co.edu.uptc.gamemanagement.enums.StateGame;
import co.edu.uptc.gamemanagement.repositories.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class GameService {

    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private GamePlayerService gamePlayerService;
    @Autowired
    private PieceService pieceService;
    @Autowired
    private ServiceConsumer serviceConsumer;
    @Autowired
    private TurnService turnService;

    public boolean checkGame(int idGame) {
        return gameRepository.existsById(idGame);
    }

    @Transactional
    public HashMap<String, Object> createGame(String nickname) {
        HashMap<String, Object> response = new HashMap<>();
        if (serviceConsumer.validateExistenceNickNameUser(nickname)){
            GamePlayer gamePlayer = gamePlayerService.existGamePlayerInAGame(nickname);
            if (gamePlayer!=null) {
                response.put("success", false);
                response.put("codeGame", gamePlayer.getGame().getId());
                response.put("error", "El jugador ya encuentra registrado en una partida con el siguiente codigo: "+ gamePlayer.getGame().getId());
            }else {
                Game game = gameRepository.save(new Game(StateGame.EN_ESPERA,nickname));
                Turn turn = turnService.createTurn(game,game.getTurns().size()+1);
                game.getTurns().add(turn);
                response.put("success", true);
                response.put("confirm","Partida creada con exito");
                response.put("codeGame", game.getId());
                response.put("stateGame",game.getStateGame());
                response.put("gamePlayers", gamePlayerService.createGamePlayers(game,nickname,turn).get("gamePlayers"));
            }
        }else{
            response.put("success", false);
            response.put("error", "No se encontro un jugador con el siguiente nickName: "+ nickname);
        }
        return response;
    }

    @Transactional
    public HashMap<String, Object> joinGame(GamePlayerDTOFront gamePlayerDTOFront){
        HashMap<String, Object> response = new HashMap<>();
        Game game = gameRepository.findById(gamePlayerDTOFront.getIdGame());
        if (game != null) {
            if (serviceConsumer.validateExistenceNickNameUser(gamePlayerDTOFront.getNickName())){
                if (game.getStateGame().equals(StateGame.EN_ESPERA)){
                    Turn turn = turnService.createTurn(game,game.getTurns().size()+1);
                    response = stateGameWaiting(gamePlayerDTOFront,turn);
                }else if (game.getStateGame().equals(StateGame.JUGANDO)) {
                    response = stateGamePlaying(gamePlayerDTOFront);
                    response.put("stateGame",game.getStateGame());
                }else{
                    response.put("success", false);
                    response.put("error", "Esta partida ya ha terminado");
                }
            }else {
                response.put("success", false);
                response.put("error", "No se encontro un jugador con el siguiente nickName: "+ gamePlayerDTOFront.getNickName());
            }
        }else{
            response.put("success", false);
            response.put("error", "La partida no existe no existe");
        }
        return response;
    }

    private HashMap<String, Object> stateGamePlaying(GamePlayerDTOFront gamePlayerDTOFront) {
        HashMap<String, Object> response = new HashMap<>();
        GamePlayer gamePlayer = gamePlayerService.existPlayerInTheGame(gamePlayerDTOFront.getIdGame(),gamePlayerDTOFront.getNickName());
        if (gamePlayer!=null){
            response.put("success",true);
            response.put("confirm","Jugador reconectado con exito");
            response.put("gamePlayers", gamePlayerService.getGamePlayersInGame(gamePlayerDTOFront.getIdGame()));
        }else{
            response.put("success",false);
            response.put("error","No se encontro el jugador en la partida");
        }
        return response;
    }

    private HashMap<String, Object> stateGameWaiting(GamePlayerDTOFront gamePlayerDTOFront,Turn turn) {
        HashMap <String, Object> response = new HashMap<>();
        Game game = gameRepository.findById(gamePlayerDTOFront.getIdGame());
        GamePlayer gamePlayer = gamePlayerService.existGamePlayerInAGame(gamePlayerDTOFront.getNickName());
        if (gamePlayer!=null){
            if (gamePlayer.getGame().getId()==gamePlayerDTOFront.getIdGame()){
                response.put("success", true);
                response.put("confirm", "Te reconectaste a la sala de espera exitosamente");
                response.put("codeGame", game.getId());
                response.put("stateGame",game.getStateGame());
                response.put("gamePlayers", gamePlayerService.getGamePlayersInWaitingRoom(game.getId()));
            }else {
                response.put("success", false);
                response.put("codeGame", gamePlayer.getGame().getId());
                response.put("error", "El jugador ya encuentra registrado en una partida con el siguiente codigo: "+ gamePlayer.getGame().getId());
            }
        }else {
            response = gamePlayerService.createGamePlayers(game,gamePlayerDTOFront.getNickName(),turn);
            if ((Boolean) response.get("success")) {
                response.clear();
                response.put("success", true);
                response.put("confirm", "Te uniste exitosamente");
                response.put("codeGame", game.getId());
                response.put("stateGame",game.getStateGame());
                response.put("gamePlayers", gamePlayerService.getGamePlayersInWaitingRoom(game.getId()));
            }
        }
        return response;
    }

    @Transactional
    public HashMap<String, Object> SelectPieceGame(GamePieceDTOFront gamePieceDTOFront) {
         HashMap<String, Object> response = new HashMap<>();
         if (gamePlayerService.checkPieceGame(gamePieceDTOFront.getIdGame(), pieceService.getPiece(gamePieceDTOFront.getNamePiece()).getId())){
             response.put("success", false);
             response.put("error", "Esta ficha ya fue seleccionada por otro jugador");
         }else{
             response = gamePlayerService.SelectPieceGamePlayer(gamePieceDTOFront.getNickName(),gamePieceDTOFront.getIdGame(),pieceService.getPiece(gamePieceDTOFront.getNamePiece()));
         }
         return response;
    }

    public HashMap<String, Object> exitGame(ExitGameDTO exitGameDTO) {
        HashMap <String, Object> response = new HashMap<>();
        Game game = gameRepository.findById(exitGameDTO.getCodeGame());
        if (game.getNickName().equals(exitGameDTO.getNickName())){
            game.setStateGame(StateGame.FINALIZADO);
            gameRepository.save(game);
            response.put("success",true);
            response.put("confirm","Partida finalizada por que su creador se ha desconectado");
            response.put("stateGame",game.getStateGame());
            response.put("gamePlayers",gamePlayerService.getGamePlayersInWaitingRoom(exitGameDTO.getCodeGame()));
        }else{
            response = gamePlayerService.exitGamePlayerInGame(exitGameDTO);
            response.put("stateGame",game.getStateGame());
        }
        turnService.reOrderTurn(game);
        return response;
    }

    public HashMap<String, Object> changeStateGame(ChangeStateDTO changeStateDTO) {
        return gamePlayerService.changeStateGamePlayer(changeStateDTO);
    }

    public void startGameState(Game game){
        turnService.activeTurnInitial(game);
        if (game!=null){
            game.setStateGame(StateGame.JUGANDO);
            gameRepository.save(game);
        }
    }

    public HashMap<String, Object> startGame(int codeGame) {
        HashMap<String, Object> response = new HashMap<>();
        Game game = gameRepository.findById(codeGame);
        startGameState(game);
        if (Objects.equals(String.valueOf(game.getStateGame()), "JUGANDO")){
            response.put("success", true);
            response.put("confirm", "Partida iniciada con exito");
            response.put("codeGame", game.getId());
            response.put("stateGame",game.getStateGame());
            response.put("gamePlayers", gamePlayerService.getGamePlayersInGame(codeGame));
        }else{
            response.put("success",false);
            response.put("error","Esta partida no esta en juego");
        }
        return response;
    }

    private int[] rollDice() {
        int [] dice = new int[2];
        dice[0]= ThreadLocalRandom.current().nextInt(1, 7);
        dice[1]= ThreadLocalRandom.current().nextInt(1, 7);;
        return dice;
    }

    public HashMap<String, Object> rollDiceGamePlayer(int idGame) {
        HashMap <String, Object> response = new HashMap<>();
        response = gamePlayerService.TurnGamePlayer(idGame,findTurnActive(idGame),rollDice());
        turnService.nextTurn(gameRepository.findById(idGame));
        return response;
    }

    private int findTurnActive(int idGame){
        Game game = gameRepository.findById(idGame);
        Turn turn = game.getTurns().stream().filter(Turn::isActive).findFirst().orElse(null);
        if (turn!=null){
            return turn.getId();
        }else{
            return -1;
        }
    }

}
