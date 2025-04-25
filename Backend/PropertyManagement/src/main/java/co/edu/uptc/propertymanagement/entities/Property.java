package co.edu.uptc.propertymanagement.entities;

import co.edu.uptc.propertymanagement.enums.StateProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Property {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    private int price;
    private int position;
    private int mortgagePrice;
    @Enumerated(EnumType.STRING)
    private StateProperty state;
    @ManyToOne(cascade = CascadeType.ALL)
    private PropertyGroup group;
    @OneToMany(mappedBy = "property")
    private List<Rent> rent;
}
