package co.edu.uptc.propertymanagement.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PropertyCardDTO {
    private int id;
    private String name;
    private int position;
    private int price;
    private int mortgagePrice;
    private int priceHouse;
    private int priceHotel;
    private PropertyGroupDTO group;
    private List<Integer> rents;
}
