package co.edu.uptc.gamemanagement.services;

import co.edu.uptc.gamemanagement.entities.Game;
import co.edu.uptc.gamemanagement.entities.Turn;
import co.edu.uptc.gamemanagement.repositories.TurnRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.management.ObjectName;
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
        Object [] response = desactiveTurn(game);
        if ((boolean) response[0]){
            if (Integer.parseInt(String.valueOf(response[1]))<=turns.size()){
                turns.get((int) response[1]).setActive(true);
                turnRepository.save(turns.get((int) response[1]));
            }else {
                turns.getFirst().setActive(true);
                turnRepository.save(turns.getFirst());
            }
        }
    }

    public Object[] desactiveTurn(Game game){
        List<Turn> turns = findTurnInTheGame(game);
        Object[] response = new Object[2];
        for (int i = 0; i < turns.size(); i++) {
            if (turns.get(i).isActive()){
                turns.get(i).setActive(false);
                turnRepository.save(turns.get(i));
                response[0] = true;
                response[1] = i+1;
                return response;
            }
        }
        return response;
    }

}
