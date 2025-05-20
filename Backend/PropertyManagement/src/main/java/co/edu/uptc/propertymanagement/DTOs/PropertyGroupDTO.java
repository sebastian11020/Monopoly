package co.edu.uptc.propertymanagement.DTOs;

import co.edu.uptc.propertymanagement.entities.PropertyCard;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PropertyGroupDTO {
    private int id;
    private String color;
    private List<PropertyCardDTO> properties;
}
