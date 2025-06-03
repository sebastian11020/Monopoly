package co.edu.uptc.logmanagement.controller;

import co.edu.uptc.logmanagement.model.GameLog;
import co.edu.uptc.logmanagement.repository.GameLogRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/Logs")
public class GameLogController {

    @Autowired
    private GameLogRepository logRepository;

    @PostMapping
    public ResponseEntity<GameLog> saveLog(@Valid @RequestBody GameLog log) {
        if (log.getTimestamp() == null) {
            log.setTimestamp(LocalDateTime.now());
        }
        GameLog saved = logRepository.save(log);
        return ResponseEntity.ok(saved);
    }

    @GetMapping
    public List<GameLog> getAllLogs() {
        return logRepository.findAll();
    }

    @GetMapping("/game/{gameId}")
    public List<GameLog> getLogsByGame(@PathVariable String gameId) {
        return logRepository.findByGameId(gameId);
    }

    @GetMapping("/player/{player}")
    public List<GameLog> getLogsByPlayer(@PathVariable String player) {
        return logRepository.findByPlayer(player);
    }
}
