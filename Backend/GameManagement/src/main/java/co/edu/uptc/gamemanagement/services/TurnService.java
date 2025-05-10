package co.edu.uptc.gamemanagement.services;

import co.edu.uptc.gamemanagement.entities.Game;
import co.edu.uptc.gamemanagement.entities.Turn;
import co.edu.uptc.gamemanagement.repositories.TurnRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TurnService {

    @Autowired
    private TurnRepository turnRepository;

    public Turn createTurn(Game game,int turn){
        return turnRepository.save(new Turn(game,turn,false));
    }

    public List<Turn> findTurnInTheGame(Game game){
        return turnRepository.findByGame(game);
    }

    public void reOrderTurn(Game game){
        List<Turn> turns = findTurnInTheGame(game);
        for (int i = 0; i < turns.size(); i++) {
            if (turns.get(i).getTurn()!=i+1){
                turns.get(i).setTurn(i+1);
                turnRepository.save(turns.get(i));
            }
        }
    }

    public void activeTurnInitial(Game game){
        List<Turn> turns = findTurnInTheGame(game);
        turns.getFirst().setActive(true);
        turnRepository.save(turns.getFirst());
    }

    public void nextTurn(Game game){
        List<Turn> turns = findTurnInTheGame(game);
        for (int i = 0; i < turns.size(); i++) {
            if (turns.get(i).isActive()){
                turns.get(i).setActive(false);
                turnRepository.save(turns.get(i));
                if (i+1<turns.size()){
                    turns.get(i+1).setActive(true);
                    turnRepository.save(turns.get(i+1));
                }else{
                    turns.getFirst().setActive(true);
                    turnRepository.save(turns.getFirst());
                }
            }
        }
    }

}
