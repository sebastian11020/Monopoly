package co.edu.uptc.gamemanagement.services;

import co.edu.uptc.gamemanagement.entities.Game;
import co.edu.uptc.gamemanagement.entities.Turn;
import co.edu.uptc.gamemanagement.repositories.TurnRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TurnService {

    @Autowired
    private TurnRepository turnRepository;

    public Turn createTurn(Game game,int turn){
        return turnRepository.save(new Turn(game,turn,false));
    }

}
