package co.edu.uptc.ServiceStadistics.service;

import co.edu.uptc.ServiceStadistics.DTOs.PlayerStats;
import co.edu.uptc.ServiceStadistics.model.GameNode;
import co.edu.uptc.ServiceStadistics.model.PlayerNode;
import co.edu.uptc.ServiceStadistics.model.PropertyNode;
import co.edu.uptc.ServiceStadistics.repository.PlayerRepository;
import co.edu.uptc.ServiceStadistics.repository.GameRepository;
import co.edu.uptc.ServiceStadistics.repository.PropertyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
public class StatsService {

    private final PlayerRepository playerRepository;
    private final GameRepository gameRepository;
    private final PropertyRepository propertyRepository;

    public StatsService(PlayerRepository playerRepository, GameRepository gameRepository, PropertyRepository propertyRepository) {
        this.playerRepository = playerRepository;
        this.gameRepository = gameRepository;
        this.propertyRepository = propertyRepository;
    }

    @Transactional
    public PlayerNode savePlayerStats(PlayerStats dto) {
        // Buscar o crear nodos
        PlayerNode player = playerRepository.findById(dto.getPlayerId())
                .orElseGet(() -> new PlayerNode(dto.getPlayerId(), dto.getPlayerName(), dto.getMoneyWon()));
        GameNode game = gameRepository.findById(dto.getGameId())
                .orElseGet(() -> new GameNode(dto.getGameId(), dto.getDateTime(), dto.getNameWinner()));
        PropertyNode property = null;
        if (dto.getPropertyId() != null && dto.getPropertyName() != null) {
            property = propertyRepository.findById(dto.getPropertyId())
                    .orElseGet(() -> new PropertyNode(dto.getPropertyId(), dto.getPropertyName(), dto.getAcquiredCount()));
            property.setAcquiredCount(dto.getAcquiredCount());
        }
        // Relacionar jugador con partida (evitar duplicados)
        boolean foundGame = player.getGames().stream().anyMatch(g -> g.getId().equals(game.getId()));
        if (!foundGame) {
            player.getGames().add(game);
        }
        // Relacionar jugador con propiedad si existe (evitar duplicados)
        if (property != null && property.getId() != null) {
            final String propertyIdFinal = property.getId();
            boolean foundProperty = player.getProperties().stream()
                .anyMatch(p -> p.getId() != null && p.getId().equals(propertyIdFinal));
            if (!foundProperty) {
                player.getProperties().add(property);
            }
        }
        // Actualizar datos básicos
        player.setName(dto.getPlayerName());
        player.setMoneyWon(dto.getMoneyWon());
        // Guardar nodos relacionados
        if (property != null) propertyRepository.save(property);
        gameRepository.save(game);
        return playerRepository.save(player);
    }

    public java.util.List<PlayerNode> getAllPlayers() {
        return playerRepository.findAll();
    }

    public Map<String, Object> getPlayerStatsSummary(String playerId) {
        // Buscar todos los jugadores con ese ID (puede haber varios nodos)
        var players = playerRepository.findAll().stream()
            .filter(p -> p.getId() != null && p.getId().equals(playerId))
            .toList();
        if (players.isEmpty()) return null;
        int gamesPlayed = 0;
        int totalMoney = 0;
        int victories = 0;
        int totalProperties = 0;
        for (PlayerNode player : players) {
            gamesPlayed += player.getGames() != null ? player.getGames().size() : 0;
            totalMoney += player.getMoneyWon();
            totalProperties += player.getProperties() != null ? player.getProperties().size() : 0;
            if (player.getGames() != null) {
                for (var game : player.getGames()) {
                    if (game.getNameWinner() != null && game.getNameWinner().equals(player.getName())) {
                        victories++;
                    }
                }
            }
        }
        Map<String, Object> stats = new HashMap<>();
        stats.put("gamesPlayed", gamesPlayed);
        stats.put("totalMoney", totalMoney);
        stats.put("victories", victories);
        stats.put("totalProperties", totalProperties);
        return stats;
    }

    public Map<String, Object> getPlayerStatsSummaryByNickname(String nickname) {
        // Buscar todos los jugadores con ese nickname (puede haber varios nodos)
        var players = playerRepository.findAll().stream()
            .filter(p -> p.getName() != null && p.getName().equals(nickname))
            .toList();
        if (players.isEmpty()) return null;
        int gamesPlayed = 0;
        int totalMoney = 0;
        int victories = 0;
        int totalProperties = 0;
        for (PlayerNode player : players) {
            gamesPlayed += player.getGames() != null ? player.getGames().size() : 0;
            totalMoney += player.getMoneyWon();
            totalProperties += player.getProperties() != null ? player.getProperties().size() : 0;
            if (player.getGames() != null) {
                for (var game : player.getGames()) {
                    if (game.getNameWinner() != null && game.getNameWinner().equals(player.getName())) {
                        victories++;
                    }
                }
            }
        }
        Map<String, Object> stats = new HashMap<>();
        stats.put("gamesPlayed", gamesPlayed);
        stats.put("totalMoney", totalMoney);
        stats.put("victories", victories);
        stats.put("totalProperties", totalProperties);
        return stats;
    }
}
