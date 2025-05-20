package co.edu.uptc.propertymanagement.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CardDTORent {
    private Long idCard;
    private int houses;
    private int hotels;
    private boolean multiplicator;
    private int cantTransport;
}
