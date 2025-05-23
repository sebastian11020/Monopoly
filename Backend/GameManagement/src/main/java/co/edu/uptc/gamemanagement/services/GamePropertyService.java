package co.edu.uptc.gamemanagement.services;

import co.edu.uptc.gamemanagement.DTOs.CardDTO;
import co.edu.uptc.gamemanagement.entities.Game;
import co.edu.uptc.gamemanagement.entities.GameProperties;
import co.edu.uptc.gamemanagement.enums.StateCard;
import co.edu.uptc.gamemanagement.repositories.GamePropertyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
                gamePropertyRepository.save(getGameProperties(game, cardDTO));
            }
            response.put("success",true);
            response.put("confirm","Tarjetas creadas con exito");
            response.put("gameProperties",getGameProperties(game));
        }
        return response;
    }

    private GameProperties getGameProperties(Game game, CardDTO cardDTO) {
        GameProperties gameProperties = new GameProperties();
        gameProperties.setGame(game);
        gameProperties.setIdCard(cardDTO.getId());
        gameProperties.setPosition(cardDTO.getPosition());
        gameProperties.setType(cardDTO.getType());
        System.out.println("Imprimiendo el tipo de la tarjeta: "+ cardDTO.getType());
        if (cardDTO.getType().equals("Card") || cardDTO.getType().equals("TAXES")){
            gameProperties.setStateCard(StateCard.ESPECIAL);
        }else{
            gameProperties.setStateCard(StateCard.DISPONIBLE);
        }
        return gameProperties;
    }

    public List<GameProperties> getGameProperties(Game game){
        return gamePropertyRepository.findByGame(game);
    }

    public GameProperties getGamePropertyByIdGameAndIdProperty(int idGame,Long idCard){
        return gamePropertyRepository.findByGame_IdAndIdCard(idGame,idCard);
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

    public String getNickNameOwnerCard(int idGame, int position){
        return gamePropertyRepository.findByGame_IdAndPosition(idGame,position).getNickname();
    }

    public GameProperties getGameProperties(int idGame,int position){
        return gamePropertyRepository.findByGame_IdAndPosition(idGame,position);
    }

    public void buyProperty(GameProperties gameProperties,String nickname){
        if (gameProperties!=null){
            gameProperties.setNickname(nickname);
            gameProperties.setStateCard(StateCard.COMPRADA);
            gamePropertyRepository.save(gameProperties);
        }
    }

    public void save(GameProperties gameProperties){
        gamePropertyRepository.save(gameProperties);
    }

    public boolean isOwnerOfAllService(int codeGame,String nickname){
        List<GameProperties> gameProperties = gamePropertyRepository.findByGame_IdAndNicknameAndType(codeGame,nickname,"SERVICE");
        return gameProperties.size() == 2;
    }

    public int numberOfTransport(int codeGame,String nickname){
        System.out.println("Nombre del dueño del transporte: "+nickname);
        return gamePropertyRepository.findByGame_IdAndNicknameAndType(codeGame,nickname,"TRANSPORT").size();
    }

}
