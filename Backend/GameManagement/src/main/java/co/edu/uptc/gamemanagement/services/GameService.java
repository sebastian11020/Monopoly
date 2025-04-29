package co.edu.uptc.gamemanagement.services;

import co.edu.uptc.gamemanagement.DTOs.ExitGameDTO;
import co.edu.uptc.gamemanagement.DTOs.GamePieceDTOFront;
import co.edu.uptc.gamemanagement.DTOs.GamePlayerDTOFront;
import co.edu.uptc.gamemanagement.entities.Game;
import co.edu.uptc.gamemanagement.entities.GamePlayer;
import co.edu.uptc.gamemanagement.enums.StateGame;
import co.edu.uptc.gamemanagement.repositories.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
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

    public boolean checkGame(int idGame) {
        return gameRepository.existsById(idGame);
    }

    public HashMap<String, Object> createGame(String nickname) {
        HashMap<String, Object> response = new HashMap<>();
        if (serviceConsumer.validateExistenceNickNameUser(nickname)){
            GamePlayer gamePlayer = gamePlayerService.existGamePlayerInGame(nickname);
            if (gamePlayer!=null) {
                response.put("success", false);
                response.put("codeGame", gamePlayer.getGame().getId());
                response.put("error", "El jugador ya encuentra registrado en una partida con el siguiente codigo: "+ gamePlayer.getGame().getId());
            }else {
                Game game = gameRepository.save(new Game(StateGame.EN_ESPERA));
                response.put("success", true);
                response.put("confirm","Partida creada con exito");
                response.put("codeGame", game.getId());
                response.put("gamePlayers", gamePlayerService.createGamePlayers(game,nickname).get("gamePlayers"));
            }
        }else{
            response.put("success", false);
            response.put("error", "No se encontro un jugador con el siguiente nickName: "+ nickname);
        }
        return response;
    }

    public HashMap<String, Object> joinGame(GamePlayerDTOFront gamePlayerDTOFront){
        HashMap<String, Object> response = new HashMap<>();
        Game game = gameRepository.findById(gamePlayerDTOFront.getIdGame());
        if (game != null) {
            if (serviceConsumer.validateExistenceNickNameUser(gamePlayerDTOFront.getNickName())){
                GamePlayer gamePlayer = gamePlayerService.existGamePlayerInGame(gamePlayerDTOFront.getNickName());
                if (gamePlayer!=null) {
                    if (gamePlayer.getGame().getId()==gamePlayerDTOFront.getIdGame()){
                        response.clear();
                        response.put("success", true);
                        response.put("confirm", "Te reconectaste exitosamente");
                        response.put("codeGame", game.getId());
                        response.put("gamePlayers", gamePlayerService.getGamePlayers(game.getId()));
                    }else {
                        response.put("success", false);
                        response.put("codeGame", gamePlayer.getGame().getId());
                        response.put("error", "El jugador ya encuentra registrado en una partida con el siguiente codigo: "+ gamePlayer.getGame().getId());
                    }
                }else {
                    response = gamePlayerService.createGamePlayers(game,gamePlayerDTOFront.getNickName());
                    if ((Boolean) response.get("success")) {
                        response.clear();
                        response.put("success", true);
                        response.put("confirm", "Te uniste exitosamente");
                        response.put("codeGame", game.getId());
                        response.put("gamePlayers", gamePlayerService.getGamePlayers(game.getId()));
                    }
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
        HashMap<String, Object> response = new HashMap<>();
        gamePlayerService.exitGamePlayerInGame(exitGameDTO);
        response.put("success", true);
        response.put("confirm", "Jugador salio de la partida con exito");
        return response;
    }

    public HashMap<String, Object> startGame() {
        HashMap<String, Object> response = new HashMap<>();
        return response;
    }

    public HashMap<String, Object> orderTurn() {
        HashMap<String, Object> response = new HashMap<>();
        return response;
    }

    public int[] rollDice() {
        int [] dice = new int[2];
        dice[0]= ThreadLocalRandom.current().nextInt(1, 7);
        dice[1]= ThreadLocalRandom.current().nextInt(1, 7);;
        return dice;
    }

    public HashMap<String, Object> nextTurn() {
        HashMap<String, Object> response = new HashMap<>();
        response.put("dice", rollDice());
        return response;
    }


}
