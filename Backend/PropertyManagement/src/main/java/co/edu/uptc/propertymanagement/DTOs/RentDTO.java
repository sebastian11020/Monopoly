package co.edu.uptc.propertymanagement.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RentDTO {
    private Long idCard;
    private String name;
    private int rent;
}
