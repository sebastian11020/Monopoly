package co.edu.uptc.gamemanagement.controllers;

import co.edu.uptc.gamemanagement.DTOs.GamePieceDTOFront;
import co.edu.uptc.gamemanagement.DTOs.GamePlayerDTOFront;
import co.edu.uptc.gamemanagement.services.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@Controller
public class GameWebSocketController {

    @Autowired
    private GameService gameService;

    @MessageMapping("/Create") // Mapeo para recibir mensajes
    @SendTo("/topic/CreateGame")
    public HashMap<String, Object> createGame(@RequestBody String nickname) {
        return gameService.createGame(nickname);
    }

    @MessageMapping("/JoinGame")
    @SendTo("/topic/JoinGame")
    public HashMap<String, Object> joinGame(@RequestBody GamePlayerDTOFront gamePlayer) {
        return gameService.joinGame(gamePlayer);
    }

    @MessageMapping("/SelectPieceGame")
    @SendTo("/topic/SelectPieceGame")
    public HashMap<String, Object> getPieceGame(@RequestBody GamePieceDTOFront gamePiece) {
        return gameService.SelectPieceGame(gamePiece);
    }

}
