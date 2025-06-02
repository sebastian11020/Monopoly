package co.edu.uptc.ServiceStadistics.controller;

import co.edu.uptc.ServiceStadistics.DTOs.PlayerStats;
import co.edu.uptc.ServiceStadistics.model.PlayerNode;
import co.edu.uptc.ServiceStadistics.service.StatsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/Stats")
public class StatsController {

    private final StatsService statsService;

    public StatsController(StatsService statsService) {
        this.statsService = statsService;
    }

    // Endpoint para recibir info y guardar
    @PostMapping("/player")
    public ResponseEntity<PlayerNode> savePlayerStats(@RequestBody PlayerStats dto) {
        PlayerNode savedPlayer = statsService.savePlayerStats(dto);
        return ResponseEntity.ok(savedPlayer);
    }

    // Endpoint para consultar todos los jugadores
    @GetMapping("/player")
    public List<PlayerNode> getAllPlayers( ) {
        return statsService.getAllPlayers();
    }

    // Endpoint para estadísticas individuales de un jugador
    @GetMapping("/player/{playerId}/stats")
    public ResponseEntity<?> getPlayerStats(@PathVariable String playerId) {
        return ResponseEntity.ok(statsService.getPlayerStatsSummary(playerId));
    }

    // Endpoint para estadísticas individuales de un jugador por nickname
    @GetMapping("/player/nickname/{nickname}/stats")
    public ResponseEntity<?> getPlayerStatsByNickname(@PathVariable String nickname) {
        return ResponseEntity.ok(statsService.getPlayerStatsSummaryByNickname(nickname));
    }
}
