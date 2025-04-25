package co.edu.uptc.propertymanagement.DTOs;

import co.edu.uptc.propertymanagement.enums.StateProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PropertyDTO {
    private int id;
    private String name;
    private int price;
    private int position;
    private int mortgagePrice;
    private StateProperty state;
    private PropertyGroupDTO group;
}
