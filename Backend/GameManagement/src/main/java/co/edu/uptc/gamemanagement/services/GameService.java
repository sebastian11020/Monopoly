package co.edu.uptc.gamemanagement.services;

import co.edu.uptc.gamemanagement.DTOs.GamePieceDTOFront;
import co.edu.uptc.gamemanagement.DTOs.GamePlayerDTOFront;
import co.edu.uptc.gamemanagement.entities.Game;
import co.edu.uptc.gamemanagement.entities.GamePlayer;
import co.edu.uptc.gamemanagement.enums.StateGame;
import co.edu.uptc.gamemanagement.repositories.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public HashMap<String, Object> createGame(String nickname) {
        HashMap<String, Object> response = new HashMap<>();
        Game game = gameRepository.save(new Game(StateGame.EN_ESPERA));
        response.put("success", true);
        response.put("confirm","Partida creada con exito");
        response.put("codeGame", game.getId());
        gamePlayerService.createGamePlayers(game,nickname);
        return response;
    }

    public HashMap<String, Object> joinGame(GamePlayerDTOFront gamePlayerDTOFront) {
        HashMap<String, Object> response = new HashMap<>();
        Game game = gameRepository.findById(gamePlayerDTOFront.getIdGame());
        if (game != null) {
            response = gamePlayerService.createGamePlayers(game,gamePlayerDTOFront.getNickName());
            if (Boolean.parseBoolean((String) response.get("success"))) {
                response.clear();
                response.put("success", true);
                response.put("confirm", "Te uniste exitosamente");
            }
        }else{
            response.put("success", false);
            response.put("error", "La partida no existe no existe");
        }
        return response;
    }

    public HashMap<String, Object> SelectPieceGame(GamePieceDTOFront gamePieceDTOFront) {
         HashMap<String, Object> response = new HashMap<>();
         if (gamePlayerService.checkPieceGame(gamePieceDTOFront.getIdGame(), gamePieceDTOFront.getIdPiece())) {
             response.put("success", false);
             response.put("error", "Esta ficha ya fue seleccionada por otro jugador");
         }else{
             GamePlayer gamePlayer = gamePlayerService.searchGamePlayer(gamePieceDTOFront.getNickName(),gamePieceDTOFront.getIdGame());
             if (gamePlayer != null) {
                 gamePlayer.setPiece(pieceService.getPiece(gamePieceDTOFront.getIdPiece()));
                 response.put("success", true);
                 response.put("confirm", "Ficha seleccionada con exito");
             }else {
                 response.put("success", false);
                 response.put("error", "No se encontro el jugador en la partida");
             }
         }
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
