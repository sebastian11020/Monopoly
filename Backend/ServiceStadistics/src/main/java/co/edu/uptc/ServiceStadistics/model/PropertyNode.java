package co.edu.uptc.ServiceStadistics.model;

import lombok.*;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Node("Property")
public class PropertyNode {
    @Id
    private String id;
    @Property("name")
    private String name;
    @Property("acquiredCount")
    private int acquiredCount;
}

