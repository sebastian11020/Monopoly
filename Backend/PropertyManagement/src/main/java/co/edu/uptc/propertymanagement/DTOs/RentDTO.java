package co.edu.uptc.propertymanagement.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RentDTO {
    private int id;
    private int rent;
    private int cant_houses;
    private int cant_hotels;
    private boolean state;
    private PropertyDTO property;
}
