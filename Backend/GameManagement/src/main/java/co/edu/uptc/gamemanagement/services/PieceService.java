package co.edu.uptc.gamemanagement.services;

import co.edu.uptc.gamemanagement.entities.Piece;
import co.edu.uptc.gamemanagement.repositories.PieceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class PieceService {

    @Autowired
    private PieceRepository pieceRepository;

    public HashMap<String, Object> createPieces(String name) {
        HashMap<String, Object> response = new HashMap<>();
        if (pieceRepository.existsByName(name)) {
            response.put("success", false);
            response.put("error", "Ya existe una ficha con el siguiente nombre: "+ name);
        }else {
            pieceRepository.save(new Piece(name));
            response.put("success", true);
            response.put("confirm", "Ficha creada con exito");
        }
        return response;
    }

    public Piece getPiece(String namePiece) {
        return pieceRepository.findByName(namePiece);
    }
}
