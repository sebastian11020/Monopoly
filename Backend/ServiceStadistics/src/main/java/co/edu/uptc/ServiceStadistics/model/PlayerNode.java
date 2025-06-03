package co.edu.uptc.ServiceStadistics.model;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;
import org.springframework.data.neo4j.core.schema.Property;
import lombok.*;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Node("Player")
public class PlayerNode {
    @Id
    private String id;
    @Property("name")
    private String name;
    @Property("moneyWon")
    private int moneyWon;

    @Relationship(type = "PLAYED_IN", direction = Relationship.Direction.OUTGOING)
    private Set<GameNode> games = new HashSet<>();

    @Relationship(type = "OWNS", direction = Relationship.Direction.OUTGOING)
    private Set<PropertyNode> properties = new HashSet<>();

    // Constructor personalizado para id, name, moneyWon
    public PlayerNode(String id, String name, int moneyWon) {
        this.id = id;
        this.name = name;
        this.moneyWon += moneyWon;
        this.games = new HashSet<>();
        this.properties = new HashSet<>();
    }
}
