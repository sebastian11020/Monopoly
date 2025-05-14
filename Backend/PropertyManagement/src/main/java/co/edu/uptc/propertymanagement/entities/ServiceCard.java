package co.edu.uptc.propertymanagement.entities;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@DiscriminatorValue(value = "SERVICE")
public class ServiceCard extends Card{
    private int price;
    private int mortgagePrice;
    @ElementCollection
    private List<Integer> multiplicator;
}
