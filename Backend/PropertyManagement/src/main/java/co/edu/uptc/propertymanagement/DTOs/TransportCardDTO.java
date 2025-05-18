package co.edu.uptc.propertymanagement.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransportCardDTO {
    private int id;
    private String name;
    private int position;
    private int price;
    private int mortgagePrice;
    private List<Integer> rents;
}
