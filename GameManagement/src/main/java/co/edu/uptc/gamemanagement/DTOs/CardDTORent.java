package co.edu.uptc.gamemanagement.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CardDTORent{
    private Long idCard;
    private int houses;
    private int hotels;
    private boolean multiplicator;
    private int cantTransport;

    public CardDTORent(Long idCard) {
        this.idCard = idCard;
    }

    public CardDTORent(Long idCard, int houses, int hotels) {
        this.idCard = idCard;
        this.houses = houses;
        this.hotels = hotels;
    }

    public CardDTORent(Long idCard, boolean multiplicator) {
        this.idCard = idCard;
        this.multiplicator = multiplicator;
    }

    public CardDTORent(Long idCard, int cantTransport) {
        this.idCard = idCard;
        this.cantTransport = cantTransport;
    }
}
