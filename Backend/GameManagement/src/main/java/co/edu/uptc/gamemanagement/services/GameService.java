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

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

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
        }else{
            response.put("success",false);
            response.put("error","Esta partida no esta en juego");
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
        if (gamePlayer.isInJail()){
            if (gamePlayer.getDice1()==gamePlayer.getDice2()){
                gamePlayer.setInJail(false);
                gamePlayer.setPosition(gamePlayer.getPosition()+(gamePlayer.getDice1()+gamePlayer.getDice2()));
            }else {
                gamePlayer.setTurnCounter(gamePlayer.getTurnCounter()+1);
                if (gamePlayer.getTurnCounter()==3){
                    gamePlayer.setInJail(false);
                    gamePlayer.setPosition(gamePlayer.getPosition()+(gamePlayer.getDice1()+gamePlayer.getDice2()));
                    gamePlayer.setTurnCounter(0);
                }
                nextTurn(gamePlayer.getGame().getId());
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

    private List<PropertiesDTO> getCardsPlayer(int idGame,String nickName){
        List<GenericCard> cards = propertyServiceClient.getNameCards(gamePropertyService.getIdCardsPlayer(idGame,nickName));
        List<PropertiesDTO> propertiesDTOS = new ArrayList<>();
        for (GenericCard card : cards){
            GameProperties gameProperties = gamePropertyService.getGamePropertyByIdGameAndIdProperty(idGame,card.getId());
            propertiesDTOS.add(new PropertiesDTO(card.getName(),gameProperties.getHouses(),gameProperties.getHotels()));
        }
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
        if (buyPropertyDTO.isBuy()) {
            if (gameProperties.getStateCard().equals(StateCard.DISPONIBLE)){
                if (propertyCard.getPrice() > gamePlayer.getCash()){
                    response.put("success",false);
                    response.put("message","No tienes suficientes dinero para comprar esta propiedad");
                    response.put("nickName",gamePlayer.getNickname());
                }else {
                    response.put("success",true);
                    gamePropertyService.buyProperty(gameProperties,buyPropertyDTO.getNickName());
                    response.put("message","El jugador "+gamePlayer.getNickname()+" compro "+ propertyCard.getName()+" por un precio de $"+propertyCard.getPrice());
                    gamePlayer.setCash(gamePlayer.getCash()-propertyCard.getPrice());
                    gamePlayerService.save(gamePlayer);
                    response.put("nickName",gamePlayer.getNickname());
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
        if (rent > gamePlayer.getCash()) {
            response.put("success", false);
            response.put("message", "El jugador " + gamePlayer.getNickname() +
                    " no tiene suficientes dinero para pagar la renta");
        }else{
            gamePlayer.setCash(gamePlayer.getCash() - rent);
            gamePlayerService.save(gamePlayer);
            gamePlayerOwner.setCash(gamePlayerOwner.getCash() + rent);
            gamePlayerService.save(gamePlayerOwner);
            response.put("success", true);
            response.put("message", "El jugador "+gamePlayer.getNickname()+" le pago $"+ rent+
                    " a el jugador "+ nickNameOwner);
        }
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
        }
        return response;
    }

    public HashMap<String,Object> sell(SellDTOFront sellDTOFront){
        HashMap<String,Object> response = new HashMap<>();
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
            }else if (gameProperties.getHotels()==0){
                response.put("success",true);
                response.put("message","Venta exitosa");
                game.setNumberHotels(game.getNumberHouses()+sellDTOFront.getNumberHouses());
                gameProperties.setHouses(gameProperties.getHouses()-sellDTOFront.getNumberHouses());
                gamePlayer.setCash(gamePlayer.getCash()+(sellDTOFront.getNumberHouses())*(cardToBuiltDTO.getPriceHouses()/2));
            }else {
                response.put("success",false);
                response.put("message","Para vender tus casas primero debes vender tus hoteles");
            }
            gameRepository.save(game);
            gamePropertyService.save(gameProperties);
            gamePlayerService.save(gamePlayer);
        }
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
        GenericCard genericCard = propertyServiceClient.getCard(mortgagePropertyDTO.getIdCard());
        GamePlayer gamePlayer = gamePlayerService.existPlayerInTheGame(mortgagePropertyDTO.getCodeGame(),mortgagePropertyDTO.getNickName());
        GameProperties gameProperties = gamePropertyService.getGamePropertyByIdGameAndIdProperty(mortgagePropertyDTO.getCodeGame(),mortgagePropertyDTO.getIdCard());
        if (gameProperties!=null){
            response.put("success",true);
            response.put("message","El jugador "+gamePlayer.getNickname()+"hipoteco "+genericCard.getName() +"y recibio $"+ genericCard.getPrice()/2);
            gameProperties.setStateCard(StateCard.HIPOTECADA);
            gamePlayer.setCash(gamePlayer.getCash()+(genericCard.getPrice()/2));
            gamePropertyService.save(gameProperties);
        }else {
            response.put("success",false);
            response.put("message","No se logro hipotecar la propiedad "+genericCard.getName());
        }
        gamePlayerService.save(gamePlayer);
        return response;
    }
}
