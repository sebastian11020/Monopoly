package co.edu.uptc.gamemanagement.controllers;

import co.edu.uptc.gamemanagement.DTOs.*;
import co.edu.uptc.gamemanagement.services.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
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

    @PostMapping("/CardsBuilt")
    public ResponseEntity<List<CardToBuiltDTO>> cardsBuilt(@RequestBody PayRentDTO payRentDTO){
        return ResponseEntity.ok(gameService.cardToBuiltDTOS(payRentDTO));
    }

    @PostMapping("/BuiltProperty")
    public ResponseEntity<HashMap<String,Object>> exitGame(@RequestBody BuiltPropertyDTO builtPropertyDTO){
        return ResponseEntity.ok(gameService.builtProperty(builtPropertyDTO));
    }

    @PostMapping("/CardsSell")
    public ResponseEntity<List<SellDTO>> sellProperty(@RequestBody PayRentDTO payRentDTO){
        return ResponseEntity.ok(gameService.getCardsSellBuilt(payRentDTO));
    }

    @PostMapping("/Sell")
    public ResponseEntity<HashMap<String,Object>> sellProperty(@RequestBody SellDTOFront sellDTOFront){
        return ResponseEntity.ok(gameService.sell(sellDTOFront));
    }

    @PostMapping("/Mortgage")
    public ResponseEntity<HashMap<String,Object>> mortgageProperty(@RequestBody MortgagePropertyDTO mortgagePropertyDTO){
        return ResponseEntity.ok(gameService.mortgageProperties(mortgagePropertyDTO));
    }

    @PostMapping("/MortgageCards")
    public ResponseEntity<List<GenericCard>> getGamePlayers(@RequestBody PayRentDTO payRentDTO){
        return ResponseEntity.ok(gameService.getMortgageProperties(payRentDTO));
    }

}
