package co.edu.uptc.gamemanagement.controllers;

import co.edu.uptc.gamemanagement.DTOs.*;
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
    private SimpMessagingTemplate simpMessagingTemplate;

    @MessageMapping("/RollDice")
    public void rollDice(RollDiceDTO rollDice) {
        simpMessagingTemplate.convertAndSend("/topic/RollDice/"+rollDice.getCodeGame(), gameService.rollDiceGamePlayer(rollDice));
    }

    @MessageMapping("/StartGame")
    public void startGame(int codeGame){
        simpMessagingTemplate.convertAndSend("/topic/StartGame/"+codeGame,gameService.startGame(codeGame));
    }

    @MessageMapping("/Create")
    public void createGame(@RequestBody String nickname){
        System.out.println("nickname "+nickname);
        simpMessagingTemplate.convertAndSend("/topic/CreateGame/"+nickname,gameService.createGame(nickname));
    }

    @MessageMapping("/JoinGame")
    public void joinGame(GamePlayerDTOFront gamePlayer) {
        simpMessagingTemplate.convertAndSend("/topic/JoinGame/"+gamePlayer.getIdGame(), gameService.joinGame(gamePlayer));
    }

    @MessageMapping("/SelectPieceGame")
    public void getPieceGame(GamePieceDTOFront gamePiece) {
        simpMessagingTemplate.convertAndSend("/topic/SelectPieceGame/"+gamePiece.getIdGame(), gameService.SelectPieceGame(gamePiece));
    }

    @MessageMapping("/Exit")
    public void exitGamePlayer(ExitGameDTO exitGame){
        simpMessagingTemplate.convertAndSend("/topic/Exit/"+exitGame.getCodeGame(), gameService.exitGame(exitGame));
    }

    @MessageMapping("/ChangeState")
    public void changeStatePlayer(ChangeStateDTO changeState){
        System.out.println("Esatdo que llega "+ changeState.getCodeGame());
        simpMessagingTemplate.convertAndSend("/topic/ChangeStatePlayer/"+changeState.getCodeGame(), gameService.changeStateGame(changeState));
    }

    @MessageMapping("/Buy")
    public void getGamePlayers(BuyPropertyDTO buyPropertyDTO){
        simpMessagingTemplate.convertAndSend("/topic/GetGamePlayers/"+buyPropertyDTO.getCodeGame(), gameService.buy(buyPropertyDTO));
    }

}
