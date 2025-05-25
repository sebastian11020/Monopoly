package co.edu.uptc.logmanagement.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Document(collection = "Logs")
public class GameLog {
    @Id
    private String id;
    private String gameId;
    private String player;
    private String action;
    private String details;
    private LocalDateTime timestamp;
    private String stateGame;
    private Integer dice1;
    private Integer dice2;
    private Integer position;
    private Integer cash;
    private List<String> properties;
    private Integer turn;
    private Boolean inJail;
    private Integer numberOfPairs;
    private List<String> affectedPlayers;
    private Map<String, Object> extraData;
    private String creator;
    private Integer numberHouses;
    private Integer numberHotels;
    private List<Map<String, Object>> gamePlayers; // Estado resumido de todos los jugadores
    private List<Map<String, Object>> gameProperties; // Estado de todas las propiedades
    private Integer turnId;
    private Boolean turnActive;
    private Integer turnOrder;
    private String actionResult;
    private Map<String, Object> cardDrawn;
    private Map<String, Object> propertyInvolved;
    private Integer previousPosition;
    private Integer previousCash;
    private Map<String, Object> finalState;
    private String eventType;
    private String eventId;

    // Constructor completo para poblar todos los campos
    public GameLog(String gameId, String player, String action, String details, LocalDateTime timestamp, String stateGame,
                   Integer dice1, Integer dice2, Integer position, Integer cash, List<String> properties, Integer turn,
                   Boolean inJail, Integer numberOfPairs, List<String> affectedPlayers, Map<String, Object> extraData,
                   String creator, Integer numberHouses, Integer numberHotels, List<Map<String, Object>> gamePlayers,
                   List<Map<String, Object>> gameProperties, Integer turnId, Boolean turnActive, Integer turnOrder,
                   String actionResult, Map<String, Object> cardDrawn, Map<String, Object> propertyInvolved,
                   Integer previousPosition, Integer previousCash, Map<String, Object> finalState, String eventType, String eventId) {
        this.gameId = gameId;
        this.player = player;
        this.action = action;
        this.details = details;
        this.timestamp = timestamp;
        this.stateGame = stateGame;
        this.dice1 = dice1;
        this.dice2 = dice2;
        this.position = position;
        this.cash = cash;
        this.properties = properties;
        this.turn = turn;
        this.inJail = inJail;
        this.numberOfPairs = numberOfPairs;
        this.affectedPlayers = affectedPlayers;
        this.extraData = extraData;
        this.creator = creator;
        this.numberHouses = numberHouses;
        this.numberHotels = numberHotels;
        this.gamePlayers = gamePlayers;
        this.gameProperties = gameProperties;
        this.turnId = turnId;
        this.turnActive = turnActive;
        this.turnOrder = turnOrder;
        this.actionResult = actionResult;
        this.cardDrawn = cardDrawn;
        this.propertyInvolved = propertyInvolved;
        this.previousPosition = previousPosition;
        this.previousCash = previousCash;
        this.finalState = finalState;
        this.eventType = eventType;
        this.eventId = eventId;
    }
}
