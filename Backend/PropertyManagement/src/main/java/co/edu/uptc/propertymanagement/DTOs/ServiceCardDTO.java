package co.edu.uptc.propertymanagement.DTOs;

import lombok.Data;

import java.util.List;

@Data
public class ServiceCardDTO {
    private int price;
    private int mortgagePrice;
    private int id;
    private String name;
    private int position;
    private List<Integer> multiplicator;
}
