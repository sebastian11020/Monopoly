package co.edu.uptc.gamemanagement.DTOs;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
public class LogDTO {
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
    private List<Map<String, Object>> gamePlayers;
    private List<Map<String, Object>> gameProperties;
    private Integer turnId;
    private Boolean turnActive;
    private Integer turnOrder;
    private String actionResult;
    private Map<String, Object> cardDrawn;
    private Map<String, Object> propertyInvolved;
}

