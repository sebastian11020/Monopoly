package co.edu.uptc.ServiceStadistics.model;

import lombok.*;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;


@Data
@Node("Game")
@NoArgsConstructor
public class GameNode {
    @Id
    private String id;
    @Property("playedAt")
    private java.time.LocalDateTime playedAt;
    @Property("nameWinner")
    private String nameWinner;

public GameNode(String id, java.time.LocalDateTime playedAt, String nameWinner) {
        this.id = id;
        this.playedAt = playedAt;
        this.nameWinner = nameWinner;
    }
}

