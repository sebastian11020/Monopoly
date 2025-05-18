package co.edu.uptc.propertymanagement.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.sql.results.graph.Fetch;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@DiscriminatorValue(value = "PROPERTY")
public class PropertyCard extends Card {
    private int price;
    private int mortgagePrice;
    private int priceHouse;
    private int priceHotel;
    @ManyToOne(cascade = CascadeType.ALL,fetch =  FetchType.EAGER)
    private PropertyGroup group;
    @ElementCollection(fetch =  FetchType.EAGER)
    private List<Integer> rents = new ArrayList<>();
}