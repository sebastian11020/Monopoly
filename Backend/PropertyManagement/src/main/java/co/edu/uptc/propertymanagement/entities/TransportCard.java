package co.edu.uptc.propertymanagement.entities;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@DiscriminatorValue(value = "TRANSPORT")
public class TransportCard extends  Card {
    private int price;
    private int mortgagePrice;
    @ElementCollection
    private List<Integer> rents = new ArrayList<>();
}
