package co.edu.uptc.propertymanagement.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransportCardDTORent {
    private Long idCard;
    private int cantTransport;
}
