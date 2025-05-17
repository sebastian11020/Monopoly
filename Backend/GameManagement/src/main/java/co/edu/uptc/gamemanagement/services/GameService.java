package co.edu.uptc.gamemanagement.services;

import co.edu.uptc.gamemanagement.DTOs.*;
import co.edu.uptc.gamemanagement.client.PropertyServiceClient;
import co.edu.uptc.gamemanagement.entities.Game;
import co.edu.uptc.gamemanagement.entities.GamePlayer;
import co.edu.uptc.gamemanagement.entities.GameProperties;
import co.edu.uptc.gamemanagement.entities.Turn;
import co.edu.uptc.gamemanagement.enums.StateGame;
import co.edu.uptc.gamemanagement.repositories.GamePropertyRepository;
import co.edu.uptc.gamemanagement.repositories.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
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
    private PropertyServiceClient propertyServiceClient;
    @Autowired
    private GamePropertyService gamePropertyService;
    @Autowired
    private TurnService turnService;
    @Autowired
    private GamePropertyRepository gamePropertyRepository;

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
            }else{
                Game game = gameRepository.save(new Game(StateGame.EN_ESPERA,nickname));
                game.setNumberHouses(32);
                game.setNumberHotels(12);
                List<GameProperties> gameProperties = (List<GameProperties>) gamePropertyService.createGameProperties(game,propertyServiceClient.getAllCards()).get("gameProperties");
                game.setGameProperties(gameProperties);
                Turn turn = turnService.createTurn(game,game.getTurns().size()+1);
                game.getTurns().add(turn);
                response.put("success", true);
                response.put("confirm","Partida creada con exito");
                response.put("codeGame", game.getId());
                response.put("stateGame",game.getStateGame());
                response.put("gamePlayers", gamePlayerService.createGamePlayers(game,nickname,turn).get("gamePlayers"));
                turnService.activeTurnInitial(game);
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
                    response = stateGameWaiting(gamePlayerDTOFront);
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

    private HashMap<String, Object> stateGameWaiting(GamePlayerDTOFront gamePlayerDTOFront) {
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
            Turn turn = turnService.createTurn(game,game.getTurns().size()+1);
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
        if (game!=null){
            game.setStateGame(StateGame.JUGANDO);
            gameRepository.save(game);
        }
    }

    @Transactional
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

    @Transactional
    public HashMap<String, Object> rollDiceGamePlayer(RollDiceDTO rollDiceDTO) {
        HashMap<String, Object> response = new HashMap<>();
        gamePlayerService.advancePosition(rollDiceDTO.getCodeGame(),findTurnActive(rollDiceDTO.getCodeGame()),new int[]{rollDiceDTO.getDice1(),rollDiceDTO.getDice2()});
        turnService.nextTurn(gameRepository.findById(rollDiceDTO.getCodeGame()));
        response = gamePlayerService.TurnGamePlayer(rollDiceDTO.getCodeGame());
        List<GamePlayerDTOPlaying> gamePlayerDTOPlayings = (List<GamePlayerDTOPlaying>) response.get("gamePlayers");
        setNamesCards(gamePlayerDTOPlayings);
        setStatePosition(gamePlayerDTOPlayings);
        setTypePosition(gamePlayerDTOPlayings);
        response.remove("gamePlayers");
        response.put("gamePlayers",gamePlayerDTOPlayings);
        return response;
    }

    private void setNamesCards(List<GamePlayerDTOPlaying> gamePlayerDTOPlayings){
        for (GamePlayerDTOPlaying gamePlayerDTOPlaying : gamePlayerDTOPlayings){
            gamePlayerDTOPlaying.setNamesCards(getCardsPlayer(gamePlayerDTOPlaying.getCodeGame(),gamePlayerDTOPlaying.getNickName()));
        }
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

    private List<String> getCardsPlayer(int idGame,String nickName){
        return propertyServiceClient.getNameCards(gamePropertyService.getIdCardsPlayer(idGame,nickName));
    }

    private void setStatePosition(List<GamePlayerDTOPlaying> gamePlayerDTOPlayings){
        for (GamePlayerDTOPlaying gamePlayerDTOPlaying : gamePlayerDTOPlayings){
            if (gamePlayerDTOPlaying.getTurn().isActive()){
                gamePlayerDTOPlaying.setStatePosition(gamePropertyService.getStateCard(gamePlayerDTOPlaying.getCodeGame(),gamePlayerDTOPlaying.getPosition()));
            }
        }
    }

    private void setTypePosition(List<GamePlayerDTOPlaying> gamePlayerDTOPlayings){
        for (GamePlayerDTOPlaying gamePlayerDTOPlaying : gamePlayerDTOPlayings){
            if (gamePlayerDTOPlaying.getTurn().isActive()){
                gamePlayerDTOPlaying.setType(gamePropertyService.getTypeCard(gamePlayerDTOPlaying.getCodeGame(),gamePlayerDTOPlaying.getPosition()));
            }
        }
    }

}
