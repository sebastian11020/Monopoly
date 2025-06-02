package co.edu.uptc.gamemanagement.services;

import ch.qos.logback.core.net.SyslogOutputStream;
import co.edu.uptc.gamemanagement.DTOs.*;
import co.edu.uptc.gamemanagement.client.PropertyServiceClient;
import co.edu.uptc.gamemanagement.client.LogServiceClient;
import co.edu.uptc.gamemanagement.client.StadisticServiceClient;
import co.edu.uptc.gamemanagement.entities.Game;
import co.edu.uptc.gamemanagement.entities.GamePlayer;
import co.edu.uptc.gamemanagement.entities.GameProperties;
import co.edu.uptc.gamemanagement.entities.Turn;
import co.edu.uptc.gamemanagement.enums.StateCard;
import co.edu.uptc.gamemanagement.enums.StateGame;
import co.edu.uptc.gamemanagement.repositories.GamePropertyRepository;
import co.edu.uptc.gamemanagement.repositories.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

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
    @Autowired
    private LogServiceClient logServiceClient;
    @Autowired
    private StadisticServiceClient stadisticServiceClient;

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
                // Enviar log a LogManagement
                LogDTO logDTO = new LogDTO();
                logDTO.setGameId(String.valueOf(game.getId()));
                logDTO.setPlayer(nickname);
                logDTO.setAction("CREAR_PARTIDA");
                logDTO.setDetails("Partida creada por el usuario " + nickname);
                logDTO.setTimestamp(LocalDateTime.now());
                logDTO.setStateGame(game.getStateGame() != null ? game.getStateGame().name() : null);
                logDTO.setCreator(nickname);
                logServiceClient.sendLog(logDTO);
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
                // Log sencillo relevante al unirse a la partida
                LogDTO logDTO = new LogDTO();
                logDTO.setGameId(String.valueOf(game.getId()));
                logDTO.setPlayer(gamePlayerDTOFront.getNickName());
                logDTO.setAction("UNIRSE_PARTIDA");
                logDTO.setDetails("El usuario " + gamePlayerDTOFront.getNickName() + " se unió a la partida");
                logDTO.setTimestamp(java.time.LocalDateTime.now());
                logDTO.setStateGame(game.getStateGame() != null ? game.getStateGame().name() : null);
                logDTO.setCreator(game.getNickName());
                logServiceClient.sendLog(logDTO);
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
            // Log de reconexión exitosa
            LogDTO logDTO = new LogDTO();
            logDTO.setGameId(gamePlayer.getGame() != null ? String.valueOf(gamePlayer.getGame().getId()) : null);
            logDTO.setPlayer(gamePlayer.getNickname());
            logDTO.setAction("RECONEXION_JUGADOR");
            logDTO.setDetails("El usuario " + gamePlayer.getNickname() + " se reconectó a la partida");
            logDTO.setTimestamp(java.time.LocalDateTime.now());
            logDTO.setStateGame(gamePlayer.getGame() != null && gamePlayer.getGame().getStateGame() != null ? gamePlayer.getGame().getStateGame().name() : null);
            logDTO.setCreator(gamePlayer.getGame() != null ? gamePlayer.getGame().getNickName() : null);
            logServiceClient.sendLog(logDTO);
        }else{
            response.put("success",false);
            response.put("error","No se encontro el jugador en la partida");
            // Log de reconexión fallida
            LogDTO logDTO = new LogDTO();
            logDTO.setGameId(String.valueOf(gamePlayerDTOFront.getIdGame()));
            logDTO.setPlayer(gamePlayerDTOFront.getNickName());
            logDTO.setAction("RECONEXION_FALLIDA");
            logDTO.setDetails("Intento fallido de reconexión del usuario " + gamePlayerDTOFront.getNickName() + " a la partida");
            logDTO.setTimestamp(java.time.LocalDateTime.now());
            logDTO.setStateGame(null);
            logDTO.setCreator(null);
            logServiceClient.sendLog(logDTO);
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
                // Log de reconexión a sala de espera
                LogDTO logDTO = new LogDTO();
                logDTO.setGameId(game != null ? String.valueOf(game.getId()) : null);
                logDTO.setPlayer(gamePlayer.getNickname());
                logDTO.setAction("RECONEXION_SALA_ESPERA");
                logDTO.setDetails("El usuario " + gamePlayer.getNickname() + " se reconectó a la sala de espera de la partida");
                logDTO.setTimestamp(java.time.LocalDateTime.now());
                logDTO.setStateGame(game != null && game.getStateGame() != null ? game.getStateGame().name() : null);
                logDTO.setCreator(game != null ? game.getNickName() : null);
                logServiceClient.sendLog(logDTO);
            }else {
                response.put("success", false);
                response.put("codeGame", gamePlayer.getGame().getId());
                response.put("error", "El jugador ya encuentra registrado en una partida con el siguiente codigo: "+ gamePlayer.getGame().getId());
                // Log de reconexión a otra partida
                LogDTO logDTO = new LogDTO();
                logDTO.setGameId(gamePlayer.getGame() != null ? String.valueOf(gamePlayer.getGame().getId()) : null);
                logDTO.setPlayer(gamePlayer.getNickname());
                logDTO.setAction("RECONEXION_OTRA_PARTIDA");
                logDTO.setDetails("El usuario " + gamePlayer.getNickname() + " intentó unirse a otra sala de espera, pero ya está en la partida " + gamePlayer.getGame().getId());
                logDTO.setTimestamp(java.time.LocalDateTime.now());
                logDTO.setStateGame(gamePlayer.getGame() != null && gamePlayer.getGame().getStateGame() != null ? gamePlayer.getGame().getStateGame().name() : null);
                logDTO.setCreator(gamePlayer.getGame() != null ? gamePlayer.getGame().getNickName() : null);
                logServiceClient.sendLog(logDTO);
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
                // Log de unión exitosa a sala de espera
                LogDTO logDTO = new LogDTO();
                logDTO.setGameId(game != null ? String.valueOf(game.getId()) : null);
                logDTO.setPlayer(gamePlayerDTOFront.getNickName());
                logDTO.setAction("UNION_SALA_ESPERA");
                logDTO.setDetails("El usuario " + gamePlayerDTOFront.getNickName() + " se unió exitosamente a la sala de espera");
                logDTO.setTimestamp(java.time.LocalDateTime.now());
                logDTO.setStateGame(game != null && game.getStateGame() != null ? game.getStateGame().name() : null);
                logDTO.setCreator(game != null ? game.getNickName() : null);
                logServiceClient.sendLog(logDTO);
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
            // Log de intento fallido de selección de ficha
            LogDTO logDTO = new LogDTO();
            logDTO.setGameId(String.valueOf(gamePieceDTOFront.getIdGame()));
            logDTO.setPlayer(gamePieceDTOFront.getNickName());
            logDTO.setAction("SELECCION_FICHA_FALLIDA");
            logDTO.setDetails("El usuario " + gamePieceDTOFront.getNickName() + " intentó seleccionar la ficha '" + gamePieceDTOFront.getNamePiece() + "', pero ya estaba ocupada");
            logDTO.setTimestamp(java.time.LocalDateTime.now());
            logDTO.setStateGame(null);
            logDTO.setCreator(null);
            logServiceClient.sendLog(logDTO);
        }else{
            response = gamePlayerService.SelectPieceGamePlayer(gamePieceDTOFront.getNickName(),gamePieceDTOFront.getIdGame(),pieceService.getPiece(gamePieceDTOFront.getNamePiece()));
            // Log de selección exitosa de ficha
            LogDTO logDTO = new LogDTO();
            logDTO.setGameId(String.valueOf(gamePieceDTOFront.getIdGame()));
            logDTO.setPlayer(gamePieceDTOFront.getNickName());
            logDTO.setAction("SELECCION_FICHA_EXITOSA");
            logDTO.setDetails("El usuario " + gamePieceDTOFront.getNickName() + " seleccionó la ficha '" + gamePieceDTOFront.getNamePiece() + "' exitosamente");
            logDTO.setTimestamp(java.time.LocalDateTime.now());
            logDTO.setStateGame(null);
            logDTO.setCreator(null);
            logServiceClient.sendLog(logDTO);
        }
        return response;
    }

    public HashMap<String, Object> exitGame(ExitGameDTO exitGameDTO) {
        HashMap <String, Object> response = new HashMap<>();
        System.out.println("Sacando a :  "+exitGameDTO);
        Game game = gameRepository.findById(exitGameDTO.getCodeGame());
        if (game.getStateGame().equals(StateGame.EN_ESPERA)){
            System.out.println("Esta partida esta en espera");
            response = exitGameInWaitingRoom(exitGameDTO);
        }else if (game.getStateGame().equals(StateGame.JUGANDO)){
            System.out.println("Esta partida esta en juego");
            response = exitGameInGameView(exitGameDTO);
        }
        return response;
    }

    private HashMap<String, Object> exitGameInWaitingRoom(ExitGameDTO exitGameDTO) {
        HashMap <String, Object> response = new HashMap<>();
        Game game = gameRepository.findById(exitGameDTO.getCodeGame());
        if (game.getNickName().equals(exitGameDTO.getNickName())){
            List<GamePlayer> allPlayers = new ArrayList<>(game.getGamePlayers());
            game.setStateGame(StateGame.FINALIZADO);
            gameRepository.save(game);
            sendGameStats(allPlayers, game);
            response.put("success",true);
            response.put("confirm","Partida finalizada por que su creador se ha desconectado");
            response.put("stateGame",game.getStateGame());
            response.put("gamePlayers",gamePlayerService.getGamePlayersInWaitingRoom(exitGameDTO.getCodeGame()));
            LogDTO logDTO = new LogDTO();
            logDTO.setGameId(game != null ? String.valueOf(game.getId()) : null);
            logDTO.setPlayer(exitGameDTO.getNickName());
            logDTO.setAction("FINALIZAR_PARTIDA_CREADOR");
            logDTO.setDetails("El creador " + exitGameDTO.getNickName() + " finalizó la partida/desconectado");
            logDTO.setStateGame(game != null && game.getStateGame() != null ? game.getStateGame().name() : null);
            logDTO.setCreator(game != null ? game.getNickName() : null);
            logServiceClient.sendLog(logDTO);

        }else{
            response = gamePlayerService.exitGamePlayerInGame(exitGameDTO);
            response.put("stateGame",game.getStateGame());
            // Log: un jugador sale de la partida
            LogDTO logDTO = new LogDTO();
            logDTO.setGameId(game != null ? String.valueOf(game.getId()) : null);
            logDTO.setPlayer(exitGameDTO.getNickName());
            logDTO.setAction("SALIR_PARTIDA_JUGADOR");
            logDTO.setDetails("El jugador " + exitGameDTO.getNickName() + " salió de la partida");
            logDTO.setStateGame(game != null && game.getStateGame() != null ? game.getStateGame().name() : null);
            logDTO.setCreator(game != null ? game.getNickName() : null);
            logServiceClient.sendLog(logDTO);
        }
        turnService.reOrderTurn(game);
        return response;
    }

    private HashMap<String, Object> exitGameInGameView(ExitGameDTO exitGameDTO) {
        HashMap <String, Object> response = new HashMap<>();
        int idTurn = findTurnActive(exitGameDTO.getCodeGame());
        System.out.println("Id del turno actual: "+idTurn);
        GamePlayer gamePlayer = gamePlayerService.existPlayerInTheGame(exitGameDTO.getCodeGame(),exitGameDTO.getNickName());
        System.out.println("Id del turno del jugador que se va a desconectar: "+gamePlayer.getTurn().getId());
        if (gamePlayer.getTurn().getId()==idTurn){
            System.out.println("El jugador que se va a desconectar es el turno actual");
            turnService.nextTurn(gamePlayer.getGame());
        }
        System.out.println("El nuEVOo id de turno activo es:  "+ findTurnActive(gamePlayer.getGame().getId()));
        List<GamePlayer> allPlayers = new ArrayList<>(gameRepository.findById(exitGameDTO.getCodeGame()).getGamePlayers());
        response = gamePlayerService.exitGamePlayerInGame(exitGameDTO);
        System.out.println("Imprimiendo el response: de partidio en juego  "+response);
        List<GamePlayerDTO> gamePlayerDTOS = gamePlayerService.getGamePlayersInGame(exitGameDTO.getCodeGame());
        System.out.println("Imprimir la cantidad de jugadores en el juego: "+gamePlayerDTOS.size());
        Game game = gameRepository.findById(exitGameDTO.getCodeGame());
        if (gamePlayerDTOS.size()==1){
            System.out.println("Ingrese por que solo queda un jugador en el juego");
            System.out.println("Nickname del jugador: "+gamePlayerDTOS.getFirst());
            game.setWinnerNickName(gamePlayerDTOS.getFirst().getNickname());
            game.setStateGame(StateGame.FINALIZADO);
            System.out.println("Imprimiendo antes de guardar el juego:  "+ game);
            gameRepository.save(game);
            System.out.println("Imprimiendo despues de guardar el juego:  "+ game);
            sendGameStats(allPlayers, game);
            sendFinalGameLog(game);
        }
        response.put("stateGame",game.getStateGame());
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
            response.put("gamePlayers",getPlayerPlaying(codeGame));
            // Log de inicio exitoso de partida
            LogDTO logDTO = new LogDTO();
            logDTO.setGameId(game != null ? String.valueOf(game.getId()) : null);
            logDTO.setPlayer(game != null ? game.getNickName() : null);
            logDTO.setAction("INICIAR_PARTIDA_EXITOSA");
            logDTO.setDetails(game != null ? "La partida fue iniciada exitosamente por el usuario " + game.getNickName() : "Intento de inicio de partida no válido");
            logDTO.setStateGame(game != null && game.getStateGame() != null ? game.getStateGame().name() : null);
            logDTO.setCreator(game != null ? game.getNickName() : null);
            logServiceClient.sendLog(logDTO);
        }else{
            response.put("success",false);
            response.put("error","Esta partida no esta en juego");
            // Log de intento fallido de inicio de partida
            LogDTO logDTO = new LogDTO();
            logDTO.setGameId(game != null ? String.valueOf(game.getId()) : null);
            logDTO.setPlayer(game != null ? game.getNickName() : null);
            logDTO.setAction("INICIAR_PARTIDA_FALLIDA");
            logDTO.setDetails(game != null ? "Intento fallido de inicio de partida por el usuario " + game.getNickName() : "Intento de inicio de partida no válido");
            logDTO.setStateGame(game != null && game.getStateGame() != null ? game.getStateGame().name() : null);
            logDTO.setCreator(game != null ? game.getNickName() : null);
            logServiceClient.sendLog(logDTO);
        }
        return response;
    }

    public HashMap<String, Object> updateGame(int codeGame) {
        HashMap<String, Object> response = new HashMap<>();
        Game game = gameRepository.findById(codeGame);
        if (game!=null){
            response.put("success", true);
            response.put("confirm", "Informacion actualizada con exito");
            response.put("codeGame", game.getId());
            response.put("stateGame",game.getStateGame());
            response.put("gamePlayers",getPlayerPlaying(codeGame));
        }
        return response;
    }

    @Transactional
    public HashMap<String, Object> rollDiceGamePlayer(RollDiceDTO rollDiceDTO) {
        HashMap<String, Object> response = new HashMap<>();
        response = advancePosition(rollDiceDTO);
        // Log para el lanzamiento de dados
        GamePlayer gamePlayer = gamePlayerService.getGamePlayerInGame(rollDiceDTO.getCodeGame(), findTurnActive(rollDiceDTO.getCodeGame()));
        LogDTO logDTO = new LogDTO();
        logDTO.setGameId(String.valueOf(rollDiceDTO.getCodeGame()));
        logDTO.setPlayer(gamePlayer != null ? gamePlayer.getNickname() : null);
        logDTO.setAction("LANZAR_DADOS");
        logDTO.setDetails("El jugador " + (gamePlayer != null ? gamePlayer.getNickname() : "") + " lanzó los dados: [" + rollDiceDTO.getDice1() + ", " + rollDiceDTO.getDice2() + "]");
        logDTO.setTimestamp(java.time.LocalDateTime.now());
        logDTO.setStateGame(gamePlayer != null && gamePlayer.getGame() != null && gamePlayer.getGame().getStateGame() != null ? gamePlayer.getGame().getStateGame().name() : null);
        logDTO.setCreator(gamePlayer.getGame() != null ? gamePlayer.getGame().getNickName() : null);
        logServiceClient.sendLog(logDTO);
        return response;
    }

    @Transactional
    public HashMap<String,Object> advancePosition(RollDiceDTO rollDiceDTO){
        HashMap <String,Object> response = new HashMap<>();
        GamePlayer gamePlayer = gamePlayerService.getGamePlayerInGame(rollDiceDTO.getCodeGame(),
                findTurnActive(rollDiceDTO.getCodeGame()));
        if (gamePlayer!=null){
            gamePlayer.setDice1(rollDiceDTO.getDice1());
            gamePlayer.setDice2(rollDiceDTO.getDice2());
            checkPairs(gamePlayer);
            if (gamePlayer.isInJail()) {
                exitJail(gamePlayer);
            } else {
                int position = gamePlayer.getPosition();
                System.out.println("Posicion actual del jugador: "+ position);
                position += gamePlayer.getDice1() + gamePlayer.getDice2();
                System.out.println("Posicion final del jugador: "+ position);
                if (gamePlayer.getNumberOfPairs() == 3) {
                    gamePlayer.setInJail(true);
                    gamePlayer.setPosition(10);
                    response.put("ActionAdvance","El jugador "+gamePlayer.getNickname()+
                            " acaba de sacar su tercer par y sera redirigido a la carcel");
                } else {
                    if (position <= 39) {
                        gamePlayer.setPosition(position);
                    } else {
                        gamePlayer.setPosition(position - 40);
                        gamePlayer.setCash(gamePlayer.getCash() + 200);
                        response.put("ActionAdvance","El jugador "+gamePlayer.getNickname()+
                                " acaba de pasar por la salida y recibio $200");
                    }
                }
            }
            gamePlayerService.save(gamePlayer);
            response.put("success",true);
            response.put("confirm","Turno actualizado con exito.");
            response.put("message",verifyTypeCard(rollDiceDTO.getCodeGame()));
            response.put("codeGame", rollDiceDTO.getCodeGame());
            response.put("stateGame",gameRepository.findById(rollDiceDTO.getCodeGame()).getStateGame());
            gamePlayerService.save(gamePlayer);
            response.put("gamePlayers",getPlayerPlaying(rollDiceDTO.getCodeGame()));

            // Log para avance de posición
            LogDTO logDTO = new LogDTO();
            logDTO.setGameId(String.valueOf(rollDiceDTO.getCodeGame()));
            logDTO.setPlayer(gamePlayer.getNickname());
            logDTO.setAction("AVANZAR_POSICION");
            logDTO.setDetails("El jugador " + gamePlayer.getNickname() + " avanzó a la posición " + gamePlayer.getPosition());
            logDTO.setStateGame(gamePlayer.getGame() != null && gamePlayer.getGame().getStateGame() != null ? gamePlayer.getGame().getStateGame().name() : null);
            logDTO.setCreator(gamePlayer.getGame() != null ? gamePlayer.getGame().getNickName() : null);
            logServiceClient.sendLog(logDTO);
        }
        return response;
    }

    public void nextTurn(int codeGame){
        GamePlayer gamePlayer = gamePlayerService.getGamePlayerInGame(codeGame,findTurnActive(codeGame));
        if(gamePlayer.getDice1()!=gamePlayer.getDice2()){
            turnService.nextTurn(gameRepository.findById(codeGame));
        }else if (gamePlayer.getNumberOfPairs()==3){
            gamePlayer.setNumberOfPairs(0);
            gamePlayerService.save(gamePlayer);
            turnService.nextTurn(gameRepository.findById(codeGame));
        }
        // Log para cambio de turno
        Game game = gameRepository.findById(codeGame);
        GamePlayer nextPlayer = gamePlayerService.getGamePlayerInGame(codeGame, findTurnActive(codeGame));
        LogDTO logDTO = new LogDTO();
        logDTO.setGameId(String.valueOf(codeGame));
        logDTO.setPlayer(nextPlayer != null ? nextPlayer.getNickname() : null);
        logDTO.setAction("CAMBIO_TURNO");
        logDTO.setDetails("Es el turno del jugador " + (nextPlayer != null ? nextPlayer.getNickname() : ""));
        logDTO.setTimestamp(java.time.LocalDateTime.now());
        logDTO.setStateGame(game != null && game.getStateGame() != null ? game.getStateGame().name() : null);
        logDTO.setCreator(game != null ? game.getNickName() : null);
        logServiceClient.sendLog(logDTO);
    }

    public String  verifyTypeCard(int codeGame){
        GamePlayer gamePlayer = gamePlayerService.getGamePlayerInGame(codeGame,findTurnActive(codeGame));
        GenericCard genericCard = propertyServiceClient.getCard(gamePropertyService.getIdCard(codeGame,gamePlayer.getPosition()));
        return verifyStateCard(genericCard,gamePlayer);
    }

    public String verifyStateCard(GenericCard genericCard,GamePlayer gamePlayer){
        var state = gamePropertyService.getStateCard(gamePlayer.getGame().getId(),gamePlayer.getPosition());
        System.out.println("Estado de la carta: "+state);
        return switch (state){
            case StateCard.DISPONIBLE -> ("Quieres comprar la "+genericCard.getName()+ " por un precio de $"+genericCard.getPrice());
            case StateCard.COMPRADA -> statePurchase(gamePlayer);
            case StateCard.HIPOTECADA -> ("Esta propiedad se encuentra hipotecada");
            default -> verifyStateCardSpecial(genericCard,gamePlayer);
        };
    }

    private String statePurchase(GamePlayer gamePlayer) {
        String nickNameOwner = gamePropertyService.getNickNameOwnerCard(gamePlayer.getGame().getId(),gamePlayer.getPosition());
        String message = "";
        if (nickNameOwner.equals(gamePlayer.getNickname())){
            message = "Esta propiedad te pertenece.";
        }else {
            int rent = calculateRent(new PayRentDTO(gamePlayer.getGame().getId(),gamePlayer.getNickname()));
            message = "El jugador "+gamePlayer.getNickname()+" tiene que pagarle $"+rent+" a el jugador "+nickNameOwner;
        }
        return message;
    }

    private String verifyStateCardSpecial(GenericCard genericCard, GamePlayer gamePlayer) {
        String message = "";
        switch (genericCard.getName()){
            case "fortuna":
                gamePlayer.setPosition(gamePlayer.getPosition()+3);
                break;
            case "arca-comunal":
                gamePlayer.setPosition(gamePlayer.getPosition()-3);
                break;
            case "policia":
                message = ("El jugador "+gamePlayer.getNickname()+" fue capturado y trasladado a la carcel por la policia");
                gamePlayer.setInJail(true);
                gamePlayer.setPosition(10);
                break;
            case "salida":
                message = ("El jugador "+gamePlayer.getNickname()+" acaba de pasar por la salida y recibio $200");
                break;
            case "carcel":
                if (gamePlayer.isInJail()){
                    message = ("El jugador "+gamePlayer.getNickname()+" se encuentra en la carcel");
                }else {
                    message = ("El jugador "+gamePlayer.getNickname()+" esta de visita en la carcel");
                }
                break;
            case "free-parking":
                message = ("El jugador "+gamePlayer.getNickname()+" acaba de pasar por el parking libre");
                break;
            default:
                CardDTORent cardDTORent = new CardDTORent(genericCard.getId());
                message = "El jugador " + gamePlayer.getNickname() + " pago $" +
                    propertyServiceClient.getRentCard(cardDTORent)+ " de "+
                    genericCard.getName();
                gamePlayer.setCash(gamePlayer.getCash() - propertyServiceClient.getRentCard(cardDTORent));
        }
        gamePlayerService.save(gamePlayer);
        System.out.println("Mensaje que se va a enviar antes del if: " + message);
        if (message.isEmpty()){
            message = verifyTypeCard(gamePlayer.getGame().getId());
        }
        System.out.println("Mensaje que se va a enviar despues del if: " + message);
        return message;
    }

    private void exitJail(GamePlayer gamePlayer){
        boolean salioDeLaCarcel = false;
        if (gamePlayer.isInJail()){
            if (gamePlayer.getDice1()==gamePlayer.getDice2()){
                gamePlayer.setInJail(false);
                gamePlayer.setPosition(gamePlayer.getPosition()+(gamePlayer.getDice1()+gamePlayer.getDice2()));
                salioDeLaCarcel = true;
            }else {
                gamePlayer.setTurnCounter(gamePlayer.getTurnCounter()+1);
                if (gamePlayer.getTurnCounter()==3){
                    gamePlayer.setInJail(false);
                    gamePlayer.setPosition(gamePlayer.getPosition()+(gamePlayer.getDice1()+gamePlayer.getDice2()));
                    gamePlayer.setTurnCounter(0);
                    salioDeLaCarcel = true;
                }
                nextTurn(gamePlayer.getGame().getId());
            }
        }
        // Log para exitJail
        if (salioDeLaCarcel) {
            LogDTO logDTO = new LogDTO();
            logDTO.setGameId(gamePlayer.getGame() != null ? String.valueOf(gamePlayer.getGame().getId()) : null);
            logDTO.setPlayer(gamePlayer.getNickname());
            logDTO.setAction("SALIR_CARCEL");
            logDTO.setDetails("El jugador " + gamePlayer.getNickname() + " salió de la cárcel y ahora está en la posición " + gamePlayer.getPosition());
            logDTO.setTimestamp(java.time.LocalDateTime.now());
            logDTO.setStateGame(gamePlayer.getGame() != null && gamePlayer.getGame().getStateGame() != null ? gamePlayer.getGame().getStateGame().name() : null);
            logDTO.setCreator(gamePlayer.getGame() != null ? gamePlayer.getGame().getNickName() : null);
            logServiceClient.sendLog(logDTO);
        }
    }

    private void checkPairs(GamePlayer gamePlayer){
        boolean hizoPar = false;
        if (gamePlayer.getDice1()==gamePlayer.getDice2()){
            gamePlayer.setNumberOfPairs(gamePlayer.getNumberOfPairs()+1);
            hizoPar = true;
        }else {
            gamePlayer.setNumberOfPairs(0);
        }
        // Log para checkPairs
        if (hizoPar) {
            LogDTO logDTO = new LogDTO();
            logDTO.setGameId(gamePlayer.getGame() != null ? String.valueOf(gamePlayer.getGame().getId()) : null);
            logDTO.setPlayer(gamePlayer.getNickname());
            logDTO.setAction("DADO_PAR");
            logDTO.setDetails("El jugador " + gamePlayer.getNickname() + " sacó un par de dados: " + gamePlayer.getDice1() + ", " + gamePlayer.getDice2() + ". Número de pares consecutivos: " + gamePlayer.getNumberOfPairs());
            logDTO.setTimestamp(java.time.LocalDateTime.now());
            logDTO.setStateGame(gamePlayer.getGame() != null && gamePlayer.getGame().getStateGame() != null ? gamePlayer.getGame().getStateGame().name() : null);
            logDTO.setCreator(gamePlayer.getGame() != null ? gamePlayer.getGame().getNickName() : null);
            logServiceClient.sendLog(logDTO);
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

    private List<PropertiesDTO> getCardsPlayer(int idGame,String nickName){
        List<GenericCard> cards = propertyServiceClient.getNameCards(gamePropertyService.getIdCardsPlayer(idGame,nickName));
        List<PropertiesDTO> propertiesDTOS = new ArrayList<>();
        for (GenericCard card : cards){
            GameProperties gameProperties = gamePropertyService.getGamePropertyByIdGameAndIdProperty(idGame,card.getId());
            propertiesDTOS.add(new PropertiesDTO(card.getName(),gameProperties.getHouses(),gameProperties.getHotels()));
        }
        // Log para getCardsPlayer
        LogDTO logDTO = new LogDTO();
        logDTO.setGameId(String.valueOf(idGame));
        logDTO.setPlayer(nickName);
        logDTO.setAction("OBTENER_PROPIEDADES_JUGADOR");
        logDTO.setDetails("El jugador " + nickName + " consultó sus propiedades. Total: " + propertiesDTOS.size());
        logDTO.setTimestamp(java.time.LocalDateTime.now());
        logDTO.setStateGame(gameRepository.findById(idGame) != null && gameRepository.findById(idGame).getStateGame() != null ? gameRepository.findById(idGame).getStateGame().name() : null);
        logDTO.setCreator(gameRepository.findById(idGame) != null ? gameRepository.findById(idGame).getNickName() : null);
        logServiceClient.sendLog(logDTO);
        return propertiesDTOS;
    }

    private List<GamePlayerDTOPlaying> getPlayerPlaying(int codeGame){
        List<GamePlayerDTOPlaying> gamePlayerDTOPlayings = new ArrayList<>();
        for (GamePlayerDTO gamePlayerDTO : gamePlayerService.getGamePlayersInGame(codeGame)){
            gamePlayerDTOPlayings.add(new GamePlayerDTOPlaying(gamePlayerDTO.getGame().getId(),gamePlayerDTO.getNickname(),
                    gamePlayerDTO.getDice1(),gamePlayerDTO.getDice2(),gamePlayerDTO.getPosition(),gamePlayerDTO.getCash(),
                    gamePlayerDTO.getPiece(),gamePlayerDTO.getTurn(),
                    getCardsPlayer(gamePlayerDTO.getGame().getId(),gamePlayerDTO.getNickname()),
                    gamePropertyService.getTypeCard(gamePlayerDTO.getGame().getId(),gamePlayerDTO.getPosition()),
                    gamePropertyService.getStateCard(gamePlayerDTO.getGame().getId(),gamePlayerDTO.getPosition())
                    ,gamePlayerDTO.isInJail()));
        }
        return gamePlayerDTOPlayings;
    }

    @Transactional
    public HashMap<String,Object> buy(BuyPropertyDTO buyPropertyDTO){
        HashMap <String,Object> response = new HashMap<>();
        GamePlayer gamePlayer = gamePlayerService.existPlayerInTheGame(buyPropertyDTO.getCodeGame(),buyPropertyDTO.getNickName());
        GameProperties gameProperties = gamePropertyService.getGameProperties(buyPropertyDTO.getCodeGame(),gamePlayer.getPosition());
        GenericCard propertyCard = propertyServiceClient.getCard(gamePropertyService.getIdCard(buyPropertyDTO.getCodeGame(),gamePlayer.getPosition()));
        String logMessage = "";
        boolean compraExitosa = false;
        if (buyPropertyDTO.isBuy()) {
            if (gameProperties.getStateCard().equals(StateCard.DISPONIBLE)){
                if (propertyCard.getPrice() > gamePlayer.getCash()){
                    response.put("success",false);
                    response.put("message","No tienes suficientes dinero para comprar esta propiedad");
                    response.put("nickName",gamePlayer.getNickname());
                    logMessage = "El jugador " + gamePlayer.getNickname() + " intentó comprar la propiedad '" + propertyCard.getName() + "' (ID: " + propertyCard.getId() + ") por $" + propertyCard.getPrice() + ", pero no tenía suficiente dinero ($" + gamePlayer.getCash() + ").";
                }else {
                    response.put("success",true);
                    gamePropertyService.buyProperty(gameProperties,buyPropertyDTO.getNickName());
                    response.put("message","El jugador "+gamePlayer.getNickname()+" compro "+ propertyCard.getName()+" por un precio de $"+propertyCard.getPrice());
                    gamePlayer.setCash(gamePlayer.getCash()-propertyCard.getPrice());
                    gamePlayerService.save(gamePlayer);
                    response.put("nickName",gamePlayer.getNickname());
                    compraExitosa = true;
                    logMessage = "El jugador " + gamePlayer.getNickname() + " compró la propiedad '" + propertyCard.getName() + "' (ID: " + propertyCard.getId() + ") por $" + propertyCard.getPrice() + ". Dinero restante: $" + gamePlayer.getCash() + ".";
                }
            }
        }else {
            response.put("success",true);
            response.put("message","El jugador "+gamePlayer.getNickname()+" no compro "+ propertyCard.getName());
            response.put("nickName",gamePlayer.getNickname());
        }
        return response;
    }

    @Transactional
    public HashMap<String, Object> pay(PayRentDTO payRentDTO) {
        HashMap<String, Object> response = new HashMap<>();
        GamePlayer gamePlayer = gamePlayerService.existPlayerInTheGame(payRentDTO.getCodeGame(), payRentDTO.getNickName());
        String nickNameOwner = gamePropertyService.getNickNameOwnerCard(gamePlayer.getGame().getId(),gamePlayer.getPosition());
        GamePlayer gamePlayerOwner = gamePlayerService.getGamePlayerOwner(payRentDTO.getCodeGame(),nickNameOwner);
        int rent = calculateRent(payRentDTO);
        boolean pagoExitoso = false;
        String logMessage;
        if (rent > gamePlayer.getCash()) {
            response.put("success", false);
            response.put("message", "El jugador " + gamePlayer.getNickname() +
                    " no tiene suficientes dinero para pagar la renta");
            logMessage = "El jugador " + gamePlayer.getNickname() + " intentó pagar la renta de $" + rent + ", pero no tenía suficiente dinero ($" + gamePlayer.getCash() + ").";
        } else {
            gamePlayer.setCash(gamePlayer.getCash() - rent);
            gamePlayerService.save(gamePlayer);
            gamePlayerOwner.setCash(gamePlayerOwner.getCash() + rent);
            gamePlayerService.save(gamePlayerOwner);
            response.put("success", true);
            response.put("message", "El jugador "+gamePlayer.getNickname()+" le pago $"+ rent+
                    " a el jugador "+ nickNameOwner);
            pagoExitoso = true;
            logMessage = "El jugador " + gamePlayer.getNickname() + " pagó la renta de $" + rent + " al jugador " + nickNameOwner + ". Dinero restante: $" + gamePlayer.getCash() + ".";
        }
        // Log para pay
        LogDTO logDTO = new LogDTO();
        logDTO.setGameId(gamePlayer.getGame() != null ? String.valueOf(gamePlayer.getGame().getId()) : null);
        logDTO.setPlayer(gamePlayer.getNickname());
        logDTO.setAction(pagoExitoso ? "PAGO_RENTA_EXITOSO" : "PAGO_RENTA_FALLIDO");
        logDTO.setDetails(logMessage);
        logDTO.setTimestamp(java.time.LocalDateTime.now());
        logDTO.setStateGame(gamePlayer.getGame() != null && gamePlayer.getGame().getStateGame() != null ? gamePlayer.getGame().getStateGame().name() : null);
        logDTO.setCreator(gamePlayer.getGame() != null ? gamePlayer.getGame().getNickName() : null);
        logServiceClient.sendLog(logDTO);
        return response;
    }

    private int calculateRent(PayRentDTO payRentDTO){
        GamePlayer gamePlayer = gamePlayerService.existPlayerInTheGame(payRentDTO.getCodeGame(), payRentDTO.getNickName());
        GameProperties gameProperties = gamePropertyService.getGameProperties(payRentDTO.getCodeGame(), gamePlayer.getPosition());
        return switch (gameProperties.getType()){
            case "TRANSPORT" -> propertyServiceClient.getRentCard(new CardDTORent(gameProperties.getIdCard(),
                    gamePropertyService.numberOfTransport(payRentDTO.getCodeGame(),gameProperties.getNickname())));
            case "SERVICE" -> propertyServiceClient.getRentCard(new CardDTORent(gameProperties.getIdCard(),
                    gamePropertyService.isOwnerOfAllService(payRentDTO.getCodeGame(),payRentDTO.getNickName())))
                    *(gamePlayer.getDice1()+gamePlayer.getDice2());
            default -> propertyServiceClient.getRentCard(new CardDTORent(gameProperties.getIdCard(),
                    gameProperties.getHouses(),gameProperties.getHotels()));
        };
    }

    public List<CardToBuiltDTO> cardToBuiltDTOS(PayRentDTO payRentDTO){
        return propertyServiceClient.getCardsToBuilt(gamePropertyService.getIdCardsPlayer(payRentDTO.getCodeGame(),
                payRentDTO.getNickName()));
    }

    public List<SellDTO> getCardsSellBuilt(PayRentDTO payRentDTO){
        System.out.println("Imprimeiendo la informacion entrante: "+ payRentDTO);
        List<CardToBuiltDTO>  cardToBuiltDTOS = propertyServiceClient.getCardsToBuilt(gamePropertyService.getIdCardsPlayer(payRentDTO.getCodeGame(),
                payRentDTO.getNickName()));
        System.out.println("cardToBuiltDTOS: "+ cardToBuiltDTOS.size());
        List<SellDTO> sellDTOS = new ArrayList<>();
        for (CardToBuiltDTO card:cardToBuiltDTOS){
            System.out.println("Imprimiendo la tajeta: "+ card);
            GameProperties gameProperties = gamePropertyService.getGamePropertyByIdGameAndIdProperty(payRentDTO.getCodeGame(),card.getIdCard());
            System.out.println("Imprimiendo la propiedad: " + gameProperties);
            if (gameProperties.getHouses()>0){
                sellDTOS.add(new SellDTO(card.getIdCard(),card.getName(),gameProperties.getHouses(),gameProperties.getHotels()));
            }
        }
        return sellDTOS;
    }

    @Transactional
    public HashMap<String, Object> builtProperty(BuiltPropertyDTO builtPropertyDTO) {
        HashMap<String, Object> response = new HashMap<>();
        GamePlayer gamePlayer = gamePlayerService.existPlayerInTheGame(builtPropertyDTO.getCodeGame(), builtPropertyDTO.getNickName());
        boolean construccionExitosa = false;
        String logMessage = "";
        if (gamePlayer.getTurn().isActive()){
            CardToBuiltDTO cardBuilt = propertyServiceClient.cardBuilt(builtPropertyDTO.getIdCard());
            GameProperties gameProperties = gamePropertyService.getGamePropertyByIdGameAndIdProperty(builtPropertyDTO.getCodeGame(),
                    builtPropertyDTO.getIdCard());
            Game game = gameRepository.findById(builtPropertyDTO.getCodeGame());
               if (gameProperties.getNickname().equals(builtPropertyDTO.getNickName())){
                   if (gameProperties.getHouses()<4){
                       if (gamePlayer.getCash()>=cardBuilt.getPriceHouses()){
                           if (game.getNumberHouses()>0){
                               gameProperties.setHouses(gameProperties.getHouses()+1);
                               game.setNumberHouses(game.getNumberHouses()-1);
                               response.put("success",true);
                               response.put("message","Se construyó una casa con exito en la propiedad "
                                       +cardBuilt.getName() + ", ahora tiene un total de "+gameProperties.getHouses()+" casas");
                               gamePlayer.setCash(gamePlayer.getCash()-cardBuilt.getPriceHouses());
                           }else {
                               response.put("success",false);
                               response.put("message","No quedan casas disponibles para construir en esta propiedad" + cardBuilt.getName());
                           }
                       }else{
                           response.put("success",false);
                           response.put("message","No tienes suficientes dinero para construir una casa en esta propiedad" + cardBuilt.getName());
                       }
                   }else if (gameProperties.getHotels()<1){
                       if (gamePlayer.getCash()>=cardBuilt.getPriceHotels()){
                           if (game.getNumberHotels()>0){
                               response.put("success",true);
                               game.setNumberHouses(game.getNumberHotels()-1);
                               game.setNumberHotels(game.getNumberHouses()+4);
                               gameProperties.setHotels(gameProperties.getHotels()+1);
                               response.put("message","Se construyó una hotel con exito en la propiedad "
                                       +cardBuilt.getName());
                               gamePlayer.setCash(gamePlayer.getCash()-cardBuilt.getPriceHotels());
                           }else {
                               response.put("success",false);
                               response.put("message","No quedan hoteles disponibles para construir en esta propiedad" + cardBuilt.getName());
                           }
                       }else{
                           response.put("success",false);
                           response.put("message","No tienes suficientes dinero para construir un hotel en esta propiedad" + cardBuilt.getName());
                       }
                   }else {
                       response.put("success",false);
                       response.put("message","No se puede construir mas en esta propiedad "+
                               cardBuilt.getName());
                   }
                   gameRepository.save(game);
                   gamePlayerService.save(gamePlayer);
                   gamePropertyService.save(gameProperties);
               }
        }else {
            response.put("success",false);
            response.put("message","No puedes construir porque ya pasaste tu turno");
            logMessage = "El jugador " + builtPropertyDTO.getNickName() + " intentó construir fuera de su turno.";
        }
        // Log para builtProperty
        LogDTO logDTO = new LogDTO();
        logDTO.setGameId(String.valueOf(builtPropertyDTO.getCodeGame()));
        logDTO.setPlayer(builtPropertyDTO.getNickName());
        logDTO.setAction(construccionExitosa ? "CONSTRUCCION_EXITOSA" : "CONSTRUCCION_FALLIDA");
        logDTO.setDetails(logMessage);
        logDTO.setTimestamp(java.time.LocalDateTime.now());
        Game gameLog = gameRepository.findById(builtPropertyDTO.getCodeGame());
        logDTO.setStateGame(gameLog != null && gameLog.getStateGame() != null ? gameLog.getStateGame().name() : null);
        logDTO.setCreator(gameLog != null ? gameLog.getNickName() : null);
        logServiceClient.sendLog(logDTO);
        return response;
    }

    public HashMap<String,Object> sell(SellDTOFront sellDTOFront){
        HashMap<String,Object> response = new HashMap<>();
        boolean ventaExitosa = false;
        String logMessage = "";
        GameProperties gameProperties = gamePropertyService.getGamePropertyByIdGameAndIdProperty(sellDTOFront.getCodeGame(),sellDTOFront.getIdCard());
        if (gameProperties.getNickname().equals(sellDTOFront.getNickName())){
            GamePlayer gamePlayer = gamePlayerService.existPlayerInTheGame(sellDTOFront.getCodeGame(),sellDTOFront.getNickName());
            CardToBuiltDTO  cardToBuiltDTO = propertyServiceClient.cardBuilt(sellDTOFront.getIdCard());
            Game game = gameRepository.findById(sellDTOFront.getCodeGame());
            if (gameProperties.getHotels()>=sellDTOFront.getNumberHotels()){
                game.setNumberHotels(game.getNumberHotels()+sellDTOFront.getNumberHotels());
                gameProperties.setHotels(gameProperties.getHotels()-sellDTOFront.getNumberHotels());
                gamePlayer.setCash(gamePlayer.getCash()+(sellDTOFront.getNumberHotels())*(cardToBuiltDTO.getPriceHotels()/2));
                if (gameProperties.getHouses()>=sellDTOFront.getNumberHouses()){
                    game.setNumberHotels(game.getNumberHouses()+sellDTOFront.getNumberHouses());
                    gameProperties.setHouses(gameProperties.getHouses()-sellDTOFront.getNumberHouses());
                    gamePlayer.setCash(gamePlayer.getCash()+(sellDTOFront.getNumberHouses())*(cardToBuiltDTO.getPriceHouses()/2));
                }
                response.put("success",true);
                response.put("message","Venta exitosa");
                ventaExitosa = true;
                logMessage = "El jugador " + gamePlayer.getNickname() + " vendió casas/hoteles en la propiedad '" + cardToBuiltDTO.getName() + "'. Casas vendidas: " + sellDTOFront.getNumberHouses() + ", hoteles vendidos: " + sellDTOFront.getNumberHotels() + ". Dinero actual: $" + gamePlayer.getCash() + ".";
            }else if (gameProperties.getHotels()==0){
                response.put("success",true);
                response.put("message","Venta exitosa");
                game.setNumberHotels(game.getNumberHouses()+sellDTOFront.getNumberHouses());
                gameProperties.setHouses(gameProperties.getHouses()-sellDTOFront.getNumberHouses());
                gamePlayer.setCash(gamePlayer.getCash()+(sellDTOFront.getNumberHouses())*(cardToBuiltDTO.getPriceHouses()/2));
                ventaExitosa = true;
                logMessage = "El jugador " + gamePlayer.getNickname() + " vendió casas en la propiedad '" + cardToBuiltDTO.getName() + "'. Casas vendidas: " + sellDTOFront.getNumberHouses() + ". Dinero actual: $" + gamePlayer.getCash() + ".";
            }else {
                response.put("success",false);
                response.put("message","Para vender tus casas primero debes vender tus hoteles");
                logMessage = "El jugador " + gamePlayer.getNickname() + " intentó vender casas en '" + cardToBuiltDTO.getName() + "', pero aún tiene hoteles.";
            }
            gameRepository.save(game);
            gamePropertyService.save(gameProperties);
            gamePlayerService.save(gamePlayer);
        } else {
            logMessage = "El jugador " + sellDTOFront.getNickName() + " intentó vender en una propiedad que no le pertenece.";
        }
        // Log para sell
        LogDTO logDTO = new LogDTO();
        logDTO.setGameId(String.valueOf(sellDTOFront.getCodeGame()));
        logDTO.setPlayer(sellDTOFront.getNickName());
        logDTO.setAction(ventaExitosa ? "VENTA_EXITOSA" : "VENTA_FALLIDA");
        logDTO.setDetails(logMessage);
        logDTO.setTimestamp(java.time.LocalDateTime.now());
        Game gameLog = gameRepository.findById(sellDTOFront.getCodeGame());
        logDTO.setStateGame(gameLog != null && gameLog.getStateGame() != null ? gameLog.getStateGame().name() : null);
        logDTO.setCreator(gameLog != null ? gameLog.getNickName() : null);
        logServiceClient.sendLog(logDTO);
        return response;
    }

    public List<GenericCard> getMortgageProperties(PayRentDTO payRentDTO) {
        List<GenericCard> cards = propertyServiceClient.getCardsByIds(gamePropertyService.getIdCardsPlayer(payRentDTO.getCodeGame(), payRentDTO.getNickName()));
        for (int i = 0; i < cards.size(); i++) {
            GameProperties gameProperties = gamePropertyService.getGamePropertyByIdGameAndIdProperty(payRentDTO.getCodeGame(), cards.get(i).getId());
            if (gameProperties.getStateCard().equals(StateCard.HIPOTECADA)) {
                cards.remove(i);
                i--;
            } else {
                if (gameProperties.getHouses() != 0 || gameProperties.getHotels() != 0) {
                    cards.remove(i);
                    i--;
                }
            }
        }
        return cards;
    }

    public HashMap<String,Object> mortgageProperties(MortgagePropertyDTO mortgagePropertyDTO){
        HashMap<String,Object> response = new HashMap<>();
        boolean hipotecaExitosa = false;
        String logMessage = "";
        GenericCard genericCard = propertyServiceClient.getCard(mortgagePropertyDTO.getIdCard());
        GamePlayer gamePlayer = gamePlayerService.existPlayerInTheGame(mortgagePropertyDTO.getCodeGame(),mortgagePropertyDTO.getNickName());
        GameProperties gameProperties = gamePropertyService.getGamePropertyByIdGameAndIdProperty(mortgagePropertyDTO.getCodeGame(),mortgagePropertyDTO.getIdCard());
        if (gameProperties!=null){
            response.put("success",true);
            response.put("message","El jugador "+gamePlayer.getNickname()+" hipoteco "+genericCard.getName()+" y recibio $"+ genericCard.getPrice()/2);
            gameProperties.setStateCard(StateCard.HIPOTECADA);
            gamePlayer.setCash(gamePlayer.getCash()+(genericCard.getPrice()/2));
            gamePropertyService.save(gameProperties);
            hipotecaExitosa = true;
            logMessage = "El jugador " + gamePlayer.getNickname() + " hipotecó la propiedad '" + genericCard.getName() + "' y recibió $" + (genericCard.getPrice()/2) + ". Dinero actual: $" + gamePlayer.getCash() + ".";
        }else {
            response.put("success",false);
            response.put("message","No se logro hipotecar la propiedad "+genericCard.getName());
            logMessage = "El jugador " + mortgagePropertyDTO.getNickName() + " intentó hipotecar la propiedad '" + genericCard.getName() + "', pero no fue posible.";
        }
        gamePropertyService.save(gameProperties);
        gamePlayerService.save(gamePlayer);
        // Log para mortgageProperties
        LogDTO logDTO = new LogDTO();
        logDTO.setGameId(String.valueOf(mortgagePropertyDTO.getCodeGame()));
        logDTO.setPlayer(mortgagePropertyDTO.getNickName());
        logDTO.setAction(hipotecaExitosa ? "HIPOTECA_EXITOSA" : "HIPOTECA_FALLIDA");
        logDTO.setDetails(logMessage);
        logDTO.setTimestamp(java.time.LocalDateTime.now());
        Game gameLog = gameRepository.findById(mortgagePropertyDTO.getCodeGame());
        logDTO.setStateGame(gameLog != null && gameLog.getStateGame() != null ? gameLog.getStateGame().name() : null);
        logDTO.setCreator(gameLog != null ? gameLog.getNickName() : null);
        logServiceClient.sendLog(logDTO);
        return response;
    }

    private void sendGameStats(List<GamePlayer> allPlayers, Game game) {
        String nameWinner = game.getWinnerNickName();
        for (GamePlayer player : allPlayers) {
            StatsDTO statsDTO = new StatsDTO();
            statsDTO.setPlayerId(String.valueOf(player.getId()));
            statsDTO.setPlayerName(player.getNickname());
            statsDTO.setMoneyWon(player.getCash());
            statsDTO.setDateTime(java.time.LocalDateTime.now());
            statsDTO.setGameId(String.valueOf(game.getId()));
            statsDTO.setPropertyId(null);
            statsDTO.setPropertyName(null);
            statsDTO.setAcquiredCount(0);
            statsDTO.setNameWinner(nameWinner); // Nuevo campo
            stadisticServiceClient.sendStats(statsDTO);
            List<GameProperties> acquiredProperties = game.getGameProperties().stream()
                    .filter(prop -> player.getNickname().equals(prop.getNickname()))
                    .toList();
            for (GameProperties prop : acquiredProperties) {
                StatsDTO propertyStats = new StatsDTO();
                propertyStats.setPlayerId(String.valueOf(player.getId()));
                propertyStats.setPlayerName(player.getNickname());
                propertyStats.setMoneyWon(player.getCash());
                propertyStats.setDateTime(java.time.LocalDateTime.now());
                propertyStats.setGameId(String.valueOf(game.getId()));
                propertyStats.setPropertyId(String.valueOf(prop.getIdCard()));
                String propertyName = propertyServiceClient.getCard(prop.getIdCard()).getName();
                propertyStats.setPropertyName(propertyName);
                propertyStats.setAcquiredCount(1);
                propertyStats.setNameWinner(nameWinner); // Nuevo campo
                stadisticServiceClient.sendStats(propertyStats);
            }
        }
    }

    public void sendFinalGameLog(Game game) {
        LogDTO logDTO = new LogDTO();
        logDTO.setGameId(String.valueOf(game.getId()));
        logDTO.setAction("FINALIZAR_PARTIDA");
        logDTO.setDetails("Partida finalizada. Información completa de la partida.");
        logDTO.setTimestamp(java.time.LocalDateTime.now());
        logDTO.setStateGame(game.getStateGame() != null ? game.getStateGame().name() : null);
        logDTO.setCreator(game.getNickName());

        // Players info
        List<Map<String, Object>> gamePlayers = new ArrayList<>();
        for (GamePlayer player : game.getGamePlayers()) {
            Map<String, Object> playerInfo = new HashMap<>();
            playerInfo.put("nickname", player.getNickname());
            playerInfo.put("cash", player.getCash());
            playerInfo.put("position", player.getPosition());
            playerInfo.put("piece", player.getPiece() != null ? player.getPiece().getName() : null);
            playerInfo.put("inJail", player.isInJail());
            // Usa getCardsPlayer para obtener las propiedades del jugador
            playerInfo.put("properties", getCardsPlayer(game.getId(), player.getNickname()));
            playerInfo.put("turn", player.getTurn() != null ? player.getTurn().getId() : null);
            gamePlayers.add(playerInfo);
        }
        logDTO.setGamePlayers(gamePlayers);

        // Properties info
        List<Map<String, Object>> gameProperties = new ArrayList<>();
        for (GameProperties prop : game.getGameProperties()) {
            Map<String, Object> propInfo = new HashMap<>();
            propInfo.put("id", prop.getId());
            // Usa propertyServiceClient para obtener el nombre de la propiedad
            String propertyName = propertyServiceClient.getCard(prop.getIdCard()).getName();
            propInfo.put("name", propertyName);
            propInfo.put("owner", prop.getNickname());
            propInfo.put("houses", prop.getHouses());
            propInfo.put("hotels", prop.getHotels());
            propInfo.put("state", prop.getStateCard() != null ? prop.getStateCard().name() : null);
            gameProperties.add(propInfo);
        }
        logDTO.setGameProperties(gameProperties);

        logServiceClient.sendLog(logDTO);
    }
}
