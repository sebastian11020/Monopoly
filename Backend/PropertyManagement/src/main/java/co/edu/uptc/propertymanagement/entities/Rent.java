package co.edu.uptc.propertymanagement.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Rent {
    @Id
    private int id;
    private int rent;
    private int cant_houses;
    private int cant_hotels;
    private boolean state;
    @ManyToOne(cascade = CascadeType.ALL)
    private Property property;
}
