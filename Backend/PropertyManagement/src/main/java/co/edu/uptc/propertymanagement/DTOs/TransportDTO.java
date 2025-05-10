package co.edu.uptc.propertymanagement.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransportDTO {
    private int id;
    private String name;
    private int position;
    private int price;
    private int mortgagePrice;
}
