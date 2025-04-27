package co.edu.uptc.gamemanagement.controllers;

import co.edu.uptc.gamemanagement.services.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/Game")
public class GameController {

    @Autowired
    private GameService gameService;

    @PostMapping("/Check")
    public ResponseEntity<Boolean> existGame(@RequestBody String codeGame){
        return ResponseEntity.ok(gameService.checkGame(Integer.parseInt(codeGame)));
    }
}
