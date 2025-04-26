package co.edu.uptc.gamemanagement.controllers;

import co.edu.uptc.gamemanagement.entities.Game;
import co.edu.uptc.gamemanagement.entities.Piece;
import co.edu.uptc.gamemanagement.services.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/Game")
public class GameController {

    @Autowired
    private GameService gameService;

    @PostMapping("/Create")
    public HashMap<String, Object> createGame(@RequestBody String nickname) {
        return gameService.createGame(nickname);
    }

    @GetMapping()
    public List<Piece> getPieceGame() {
        return null;
    }

}
