package co.edu.uptc.gamemanagement.services;

import co.edu.uptc.gamemanagement.DTOs.*;
import co.edu.uptc.gamemanagement.client.PropertyServiceClient;
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

import java.util.ArrayList;
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
            response.put("gamePlayers",getPlayerPlaying(codeGame));
        }else{
            response.put("success",false);
            response.put("error","Esta partida no esta en juego");
        }
        return response;
    }

    @Transactional
    public HashMap<String, Object> rollDiceGamePlayer(RollDiceDTO rollDiceDTO) {
        HashMap<String, Object> response = new HashMap<>();
        response = advancePosition(rollDiceDTO);
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
                position += gamePlayer.getDice1() + gamePlayer.getDice2();
                if (gamePlayer.getNumberOfPairs() == 3) {
                    gamePlayer.setInJail(true);
                    gamePlayer.setPosition(10);
                    gamePlayer.setNumberOfPairs(0);
                    response.put("ActionAdvance","El jugador "+gamePlayer.getNickname()+" acaba de sacar su tercer par y sera redirigido a la carcel");
                } else {
                    if (position <= 39) {
                        gamePlayer.setPosition(position);
                    } else {
                        gamePlayer.setPosition(position - 39);
                        gamePlayer.setCash(gamePlayer.getCash() + 200);
                        response.put("ActionAdvance","El jugador "+gamePlayer.getNickname()+" acaba de pasar por la salida y recibio $200");
                    }
                }
            }
            gamePlayerService.turnGamePlayer(gamePlayer);
            response.putAll(verifyTypeCard(rollDiceDTO.getCodeGame(),gamePlayer));
            response.put("codeGame", rollDiceDTO.getCodeGame());
            response.put("stateGame",gameRepository.findById(rollDiceDTO.getCodeGame()).getStateGame());
            response.put("gamePlayers",getPlayerPlaying(rollDiceDTO.getCodeGame()));
        }
        return response;
    }

    private HashMap<String,Object> verifyTypeCard(int codeGame,GamePlayer gamePlayer){
        HashMap <String,Object> response = new HashMap<>();
        System.out.println("Tipo de carta: "+gamePropertyService.getTypeCard(codeGame,gamePlayer.getPosition()));
        switch (gamePropertyService.getTypeCard(codeGame,gamePlayer.getPosition())){
            case "Card":
                System.out.println("Dentro de Card");
                System.out.println("Nombre de la tarjeta tipo card: "+propertyServiceClient.getCard(gamePropertyService.getIdCard(codeGame,gamePlayer.getPosition())).getName());
                switch (propertyServiceClient.getCard(gamePropertyService.getIdCard(codeGame,gamePlayer.getPosition())).getName()){
                    case "fortuna":
                        response.put("message","El jugador "+gamePlayer.getNickname() +" cayó en la posicion: "+gamePlayer.getPosition()+", avanza 3 casillas mas.");
                        gamePlayer.setPosition(gamePlayer.getPosition()+3);
                        turnService.nextTurn(gameRepository.findById(codeGame));
                        break;
                    case "arca-comunal":
                        response.put("message","El jugador "+gamePlayer.getNickname()+" cayó en la posicion: "+gamePlayer.getPosition()+", retroce 3 casillas.");
                        gamePlayer.setPosition(gamePlayer.getPosition()-3);
                        turnService.nextTurn(gameRepository.findById(codeGame));
                        break;
                    case "policia":
                        response.put("message","El jugador "+gamePlayer.getNickname()+" fue capturado y trasladado a la carcel por la policia");
                        gamePlayer.setInJail(true);
                        gamePlayer.setPosition(10);
                        turnService.nextTurn(gameRepository.findById(codeGame));
                        break;
                    case "salida":
                        response.put("message","El jugador "+gamePlayer.getNickname()+" acaba de pasar por la salida y recibio $200");
                        turnService.nextTurn(gameRepository.findById(codeGame));
                        break;
                    case "carcel":
                        response.put("message","El jugador "+gamePlayer.getNickname()+" esta de visita en la carcel");
                        turnService.nextTurn(gameRepository.findById(codeGame));
                        break;
                }
                break;
            case "TAXES":
                System.out.println("Dentro de Taxes");
                response.put("message",verifyStateCardTaxes(codeGame,gamePlayer));
                turnService.nextTurn(gameRepository.findById(codeGame));
                break;
            case "SERVICE":
                System.out.println("Dentro de service");
                response.put("message",verifyStateCardService(codeGame,gamePlayer));
                break;
            case "PROPERTY":
                System.out.println("Dentro de property");
                response.put("message",verifyStateCardProperty(codeGame,gamePlayer));
                break;
            case "TRANSPORT":
                System.out.println("Dentro de Transport");
                response.put("message",verifyStateCardTransport(codeGame,gamePlayer));
                break;
        }
        response.putAll(gamePlayerService.turnGamePlayer(gamePlayer));
        return response;
    }

    private String verifyStateCardService(int codeGame,GamePlayer gamePlayer){
        String message = "";
        System.out.println("VerifyStateCardService: "+propertyServiceClient.getServiceCard(gamePropertyService.getIdCard(codeGame,gamePlayer.getPosition())));
        PropertyCard propertyCard = propertyServiceClient.getServiceCard(gamePropertyService.getIdCard(codeGame,gamePlayer.getPosition()));
        switch (gamePropertyService.getStateCard(codeGame,gamePlayer.getPosition())){
            case StateCard.DISPONIBLE:
                message = "Quieres comprar la "+propertyCard.getName()+ " por un precio de $"+propertyCard.getPrice();
                break;
            case StateCard.COMPRADA:
                ServiceCardDTORent serviceCardDTORent = new ServiceCardDTORent(gamePropertyService.getIdCard(codeGame,gamePlayer.getPosition()),false);
                message = "El jugador "+gamePlayer.getNickname()+" le pago a "+gamePropertyService.getNickNameCard(codeGame,gamePlayer.getPosition())+
                        " una renta de $"+((gamePlayer.getDice1()+gamePlayer.getDice2())*
                        propertyServiceClient.getRentServiceCard(serviceCardDTORent));
                break;
            case StateCard.HIPOTECADA:
                message = "Esta propiedad se encuentra hipotecada";
                break;
        }
        return message;
    }

    private String verifyStateCardProperty(int codeGame,GamePlayer gamePlayer){
        String message = "";
        System.out.println("VerifyStateCardService: "+propertyServiceClient.getPropertyCard(gamePropertyService.getIdCard(codeGame,gamePlayer.getPosition())));
        PropertyCard propertyCard = propertyServiceClient.getPropertyCard(gamePropertyService.getIdCard(codeGame,gamePlayer.getPosition()));
        switch (gamePropertyService.getStateCard(codeGame,gamePlayer.getPosition())){
            case StateCard.DISPONIBLE:
                message = "Quieres comprar la "+propertyCard.getName()+ " por un precio de $"+propertyCard.getPrice();
                break;
            case StateCard.COMPRADA:
                GameProperties gameProperties = gamePropertyService.getGameProperties(codeGame,gamePlayer.getPosition());
                PropertyCardDTORent propertyCardDTORent = new PropertyCardDTORent(gamePropertyService.getIdCard(codeGame,gamePlayer.getPosition()),gameProperties.getHouses(),gameProperties.getHotels());
                message = "El jugador "+gamePlayer.getNickname()+" le pago a "+gamePropertyService.getNickNameCard(codeGame,gamePlayer.getPosition())+
                        " una renta de $"+((gamePlayer.getDice1()+gamePlayer.getDice2())*
                        propertyServiceClient.getRentPropertyCard(propertyCardDTORent));
                break;
            case StateCard.HIPOTECADA:
                message = "Esta propiedad se encuentra hipotecada";
                break;
        }
        return message;
    }

    private String verifyStateCardTransport(int codeGame,GamePlayer gamePlayer){
        String message = "";
        System.out.println("VerifyStateCardService: "+propertyServiceClient.getTransportCard(gamePropertyService.getIdCard(codeGame,gamePlayer.getPosition())));
        PropertyCard propertyCard = propertyServiceClient.getTransportCard(gamePropertyService.getIdCard(codeGame,gamePlayer.getPosition()));
        switch (gamePropertyService.getStateCard(codeGame,gamePlayer.getPosition())){
            case StateCard.DISPONIBLE:
                message = "Quieres comprar la "+propertyCard.getName()+ " por un precio de $"+propertyCard.getPrice();
                break;
            case StateCard.COMPRADA:
                TransportCardDTORent transportCardDTORent = new TransportCardDTORent(gamePropertyService.getIdCard(codeGame,gamePlayer.getPosition()),5);
                message = "El jugador "+gamePlayer.getNickname()+" le pago a "+gamePropertyService.getNickNameCard(codeGame,gamePlayer.getPosition())+
                        " una renta de $"+((gamePlayer.getDice1()+gamePlayer.getDice2())*
                        propertyServiceClient.getRentTransportCard(transportCardDTORent));
                break;
            case StateCard.HIPOTECADA:
                message = "Esta propiedad se encuentra hipotecada";
                break;
        }
        return message;
    }

    private String verifyStateCardTaxes(int codeGame, GamePlayer gamePlayer) {
        String message = "";
        System.out.println("VerifyStateCardService: " + propertyServiceClient.getTaxesCard(gamePropertyService.getIdCard(codeGame, gamePlayer.getPosition())));
        message = "El jugador " + gamePlayer.getNickname() + " pago $" +
                propertyServiceClient.getRentTaxesCard(gamePropertyService.getIdCard(codeGame, gamePlayer.getPosition()))+ "de "+
                gamePropertyService.getNickNameCard(codeGame, gamePlayer.getPosition());
        gamePlayer.setCash(gamePlayer.getCash() - propertyServiceClient.getRentTaxesCard(gamePropertyService.getIdCard(codeGame, gamePlayer.getPosition())));
        return message;
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

    private List<GamePlayerDTOPlaying> getPlayerPlaying(int codeGame){
        List<GamePlayerDTOPlaying> gamePlayerDTOPlayings = new ArrayList<>();
        for (GamePlayerDTO gamePlayerDTO : gamePlayerService.getGamePlayersInGame(codeGame)){
            gamePlayerDTOPlayings.add(new GamePlayerDTOPlaying(gamePlayerDTO.getGame().getId(),gamePlayerDTO.getNickname(),
                    gamePlayerDTO.getDice1(),gamePlayerDTO.getDice2(),gamePlayerDTO.getPosition(),gamePlayerDTO.getCash(),
                    gamePlayerDTO.getPiece(),gamePlayerDTO.getTurn(),
                    getCardsPlayer(gamePlayerDTO.getGame().getId(),gamePlayerDTO.getNickname()),
                    gamePropertyService.getTypeCard(gamePlayerDTO.getGame().getId(),gamePlayerDTO.getPosition()),
                    gamePropertyService.getStateCard(gamePlayerDTO.getGame().getId(),gamePlayerDTO.getPosition())));
        }
        return gamePlayerDTOPlayings;
    }

    public HashMap<String,Object> buy(BuyPropertyDTO buyPropertyDTO){
        HashMap <String,Object> response = new HashMap<>();
        GamePlayer gamePlayer = gamePlayerService.getGamePlayerInGame(buyPropertyDTO.getCodeGame(),findTurnActive(buyPropertyDTO.getCodeGame()));
        GameProperties gameProperties = gamePropertyService.getGameProperties(buyPropertyDTO.getCodeGame(),gamePlayer.getPosition());
        PropertyCard propertyCard = new PropertyCard();
        switch (gameProperties.getType()){
            case "PROPERTY":
                propertyCard = propertyServiceClient.getPropertyCard(gamePropertyService.getIdCard(buyPropertyDTO.getCodeGame(),gamePlayer.getPosition()));
                break;
            case "TRANSPORT":
                propertyCard = propertyServiceClient.getTransportCard(gamePropertyService.getIdCard(buyPropertyDTO.getCodeGame(),gamePlayer.getPosition()));
                break;
            case "SERVICE":
                propertyCard = propertyServiceClient.getServiceCard(gamePropertyService.getIdCard(buyPropertyDTO.getCodeGame(),gamePlayer.getPosition()));
                break;
        }
        if (buyPropertyDTO.isBuy()) {
            if (propertyCard.getPrice() > gamePlayer.getCash()){
                response.put("success",false);
                response.put("error","No tienes suficientes dinero para comprar esta propiedad");
            }else {
                gameProperties.setNickname(buyPropertyDTO.getNickName());
                gamePropertyService.buyProperty(gameProperties,buyPropertyDTO.getNickName());
                response.put("codeGame", buyPropertyDTO.getCodeGame());
                response.put("stateGame",gameRepository.findById(buyPropertyDTO.getCodeGame()).getStateGame());
                response.put("message","El jugador "+gamePlayer.getNickname()+" compro "+ propertyCard.getName()+" por un precio de $"+propertyCard.getPrice());
                turnService.nextTurn(gameRepository.findById(buyPropertyDTO.getCodeGame()));
                response.putAll(gamePlayerService.turnGamePlayer(gamePlayer));
                response.put("gamePlayers",getPlayerPlaying(buyPropertyDTO.getCodeGame()));
            }
        }else {
            response.put("success",true);
            response.put("codeGame", buyPropertyDTO.getCodeGame());
            response.put("stateGame",gameRepository.findById(buyPropertyDTO.getCodeGame()).getStateGame());
            response.put("message","El jugador "+gamePlayer.getNickname()+" no compro "+ propertyCard.getName());
            turnService.nextTurn(gameRepository.findById(buyPropertyDTO.getCodeGame()));
            response.putAll(gamePlayerService.turnGamePlayer(gamePlayer));
            response.put("gamePlayers",getPlayerPlaying(buyPropertyDTO.getCodeGame()));
        }
        return response;
    }

}
