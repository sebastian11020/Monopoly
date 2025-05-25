package co.edu.uptc.gamemanagement.controllers;

import co.edu.uptc.gamemanagement.services.PieceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
@RequestMapping("/Pieces")
public class PieceController {

    @Autowired
    private PieceService pieceService;

    @PostMapping("/Create")
    public ResponseEntity<HashMap<String,Object>> createPieces(@RequestBody String name) {
        return ResponseEntity.ok(pieceService.createPieces(name));
    }

}
