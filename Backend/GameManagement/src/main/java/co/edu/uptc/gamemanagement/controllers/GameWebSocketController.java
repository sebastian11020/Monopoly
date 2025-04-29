package co.edu.uptc.gamemanagement.controllers;

import co.edu.uptc.gamemanagement.DTOs.ExitGameDTO;
import co.edu.uptc.gamemanagement.DTOs.GamePieceDTOFront;
import co.edu.uptc.gamemanagement.DTOs.GamePlayerDTOFront;
import co.edu.uptc.gamemanagement.services.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@Controller
public class GameWebSocketController {

    @Autowired
    private GameService gameService;
    @Autowired
    private SimpMessageSendingOperations simpMessageSendingOperations;
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @MessageMapping("/Create")
    @SendTo("/topic/CreateGame")
    public HashMap<String, Object> createGame(@RequestBody String nickname){
        return gameService.createGame(nickname);
    }

    @MessageMapping("/JoinGame")
    public void joinGame(GamePlayerDTOFront gamePlayer) {
        simpMessagingTemplate.convertAndSend("/topic/JoinGame"+gamePlayer.getIdGame(), gameService.joinGame(gamePlayer));
    }

    @MessageMapping("/SelectPieceGame")
    public void getPieceGame(GamePieceDTOFront gamePiece) {
        simpMessagingTemplate.convertAndSend("/topic/SelectPieceGame/"+gamePiece.getIdGame(), gameService.SelectPieceGame(gamePiece));
    }

    @MessageMapping("/Exit")
    public void exitGamePlayer(ExitGameDTO exitGame){
        simpMessagingTemplate.convertAndSend("/topic/Exit/"+exitGame.getCodeGame(), gameService.exitGame(exitGame));
    }

}
