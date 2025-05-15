package co.edu.uptc.gamemanagement.controllers;

import co.edu.uptc.gamemanagement.DTOs.ExitGameDTO;
import co.edu.uptc.gamemanagement.services.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Objects;

@RestController
@RequestMapping("/Game")
public class GameController {

    @Autowired
    private GameService gameService;

    @GetMapping("/Check/{codeGame}")
    public ResponseEntity<Boolean> existGame(@PathVariable int codeGame){
        return ResponseEntity.ok(gameService.checkGame(codeGame));
    }

    @PostMapping("/Create")
    public ResponseEntity<HashMap<String,Object>> createGame(@RequestBody String nickname){
        return ResponseEntity.ok(gameService.createGame(nickname));
    }

}
