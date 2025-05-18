package co.edu.uptc.gamemanagement.services;

import co.edu.uptc.gamemanagement.DTOs.CardDTO;
import co.edu.uptc.gamemanagement.entities.Game;
import co.edu.uptc.gamemanagement.entities.GameProperties;
import co.edu.uptc.gamemanagement.enums.StateCard;
import co.edu.uptc.gamemanagement.repositories.GamePropertyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
public class GamePropertyService {

    @Autowired
    private GamePropertyRepository gamePropertyRepository;

    public HashMap<String, Object> createGameProperties(Game game, List<CardDTO> cards){
        HashMap<String, Object> response = new HashMap<>();
        if (cards.isEmpty()){
            response.put("success",false);
            response.put("error","No se encontraron tarjetas");
        }else{
            for (CardDTO cardDTO : cards) {
                GameProperties gameProperties = new GameProperties();
                gameProperties.setGame(game);
                gameProperties.setIdCard(cardDTO.getId());
                gameProperties.setPosition(cardDTO.getPosition());
                gameProperties.setStateCard(StateCard.DISPONIBLE);
                gameProperties.setType(cardDTO.getType());
                gamePropertyRepository.save(gameProperties);
            }
            response.put("success",true);
            response.put("confirm","Tarjetas creadas con exito");
            response.put("gameProperties",getGameProperties(game));
        }
        return response;
    }

    public List<GameProperties> getGameProperties(Game game){
        return gamePropertyRepository.findByGame(game);
    }

    public List<Long> getIdCardsPlayer(int idGame,String nickName){
        return gamePropertyRepository.findByGame_IdAndNickname(idGame,nickName).stream()
                .map(GameProperties::getIdCard).toList();
    }

    public long getIdCard(int idGame,int position){
        return gamePropertyRepository.findByGame_IdAndPosition(idGame,position).getIdCard();
    }

    public StateCard getStateCard(int idGame,int position){
        return gamePropertyRepository.findByGame_IdAndPosition(idGame,position).getStateCard();
    }

    public String getTypeCard(int idGame,int position){
        return gamePropertyRepository.findByGame_IdAndPosition(idGame,position).getType();
    }

    public String getNickNameCard(int idGame,int position){
        return gamePropertyRepository.findByGame_IdAndPosition(idGame,position).getNickname();
    }

    public GameProperties getGameProperties(int idGame){
        return gamePropertyRepository.findByGame_Id(idGame);
    }

}
