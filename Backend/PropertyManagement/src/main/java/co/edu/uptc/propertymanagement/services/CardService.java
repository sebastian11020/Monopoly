package co.edu.uptc.propertymanagement.services;

import co.edu.uptc.propertymanagement.DTOs.*;
import co.edu.uptc.propertymanagement.entities.*;
import co.edu.uptc.propertymanagement.mappers.CardMapper;
import co.edu.uptc.propertymanagement.repositories.CardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CardService {

    @Autowired
    private PropertyGroupService propertyGroupService;
    @Autowired
    private CardRepository cardRepository;

    public HashMap<String,Object> createTransportCard(TransportCardDTO transportDTO) {
        HashMap<String,Object> response = new HashMap<>();
        if (!cardRepository.existsByName(transportDTO.getName())){
            TransportCard transportCard = new TransportCard();
            transportCard.setName(transportDTO.getName());
            transportCard.setPosition(transportDTO.getPosition());
            transportCard.setPrice(transportDTO.getPrice());
            transportCard.setMortgagePrice(transportDTO.getMortgagePrice());
            transportCard.setRents(transportDTO.getRents());
            cardRepository.save(transportCard);
            response.put("success",true);
            response.put("confirm","Transporte creado correctamente");
        }else{
            response.put("success",false);
            response.put("error","Ya existe un transporte con el nombre " + transportDTO.getName());
        }
        return response;
    }

    @Transactional
    public HashMap<String,Object> createPropertyCard(PropertyCardDTO propertyCardDTO) {
        HashMap<String,Object> response = new HashMap<>();
        if (!cardRepository.existsByName(propertyCardDTO.getName())){
            PropertyCard propertyCard = new PropertyCard();
            propertyCard.setName(propertyCardDTO.getName());
            propertyCard.setPosition(propertyCardDTO.getPosition());
            propertyCard.setPrice(propertyCardDTO.getPrice());
            propertyCard.setMortgagePrice(propertyCardDTO.getMortgagePrice());
            propertyCard.setRents(propertyCardDTO.getRents());
            propertyCard.setPriceHouse(propertyCardDTO.getPriceHouse());
            propertyCard.setPriceHotel(propertyCardDTO.getPriceHotel());
            propertyCard.setGroup(propertyGroupService.findById(propertyCardDTO.getGroup().getId()));
            cardRepository.save(propertyCard);
            response.put("success",true);
            response.put("confirm","Propiedad creada correctamente");
        }else {
            response.put("success",false);
            response.put("error","Ya existe una propiedad con el nombre " + propertyCardDTO.getName());
        }
        return response;
    }

    public HashMap<String,Object> createServiceCard(ServiceCardDTO serviceCardDTO) {
        HashMap<String,Object> response = new HashMap<>();
        if (!cardRepository.existsByName(serviceCardDTO.getName())){
            ServiceCard serviceCard = new ServiceCard();
            serviceCard.setName(serviceCardDTO.getName());
            serviceCard.setPosition(serviceCardDTO.getPosition());
            serviceCard.setPrice(serviceCardDTO.getPrice());
            serviceCard.setMortgagePrice(serviceCardDTO.getMortgagePrice());
            serviceCard.setMultiplicator(serviceCardDTO.getMultiplicator());
            cardRepository.save(serviceCard);
            response.put("success",true);
            response.put("confirm","Servicio creado correctamente");
        }else {
            response.put("success",false);
            response.put("error","Ya existe un servicio con el nombre " + serviceCardDTO.getName());
        }
        return response;
    }

    public HashMap<String,Object> createTaxesCard(TaxesCardDTO taxesCardDTO){
        HashMap<String,Object> response = new HashMap<>();
        if(!cardRepository.existsByName(taxesCardDTO.getName())){
            TaxesCard taxesCard = new TaxesCard();
            taxesCard.setName(taxesCardDTO.getName());
            taxesCard.setPosition(taxesCardDTO.getPosition());
            taxesCard.setRent(taxesCardDTO.getRent());
            cardRepository.save(taxesCard);
            response.put("success",true);
            response.put("confirm","Impuesto creado correctamente");
        }else{
            response.put("success",false);
            response.put("error","Ya existe un impuesto con el nombre " + taxesCardDTO.getName());
        }
        return response;
    }

    public HashMap<String, Object> createCard(CardDTO cardDTO) {
        HashMap<String, Object> response = new HashMap<>();
        Card card = new Card();
        card.setName(cardDTO.getName());
        card.setPosition(cardDTO.getPosition());
        cardRepository.save(card);
        response.put("success", true);
        response.put("confirm", "Carta creada correctamente");
        return response;
    }

    public List<CardDTO> findAll() {
        List<Card> cards = cardRepository.findAll();
        return cards.stream()
                .map(CardMapper.INSTANCE::cardToDTO)
                .collect(Collectors.toList());
    }

    public List<GenericCardDTO> getNamesCards(List<Long> idsCards) {
        return cardRepository.findAllByIdIn(idsCards).stream()
                .map(CardMapper.INSTANCE::cardToGenericCardDTO)
                .collect(Collectors.toList());
    }

    public List<GenericCardDTO> getCardsByIds(List<Long> idsCards) {
        List<GenericCardDTO> cards = new ArrayList<>();
        for (Long idCard : idsCards) {
            cards.add(findCardById(idCard));
        }
        return cards;
    }

    public GenericCardDTO findCardById(Long idCard) {
        var typeCard = cardRepository.findCardById(idCard).getType();
        System.out.println("Entre al servicio de la carta con un id: "+ idCard);
         return switch (typeCard) {
            case "PROPERTY" -> CardMapper.INSTANCE.propertyCardToGenericCardDTO(cardRepository.findPropertyCardById(idCard));
            case "TRANSPORT" -> CardMapper.INSTANCE.transportCardToGenericCardDTO(cardRepository.findTransportCardById(idCard));
            case "SERVICE" -> CardMapper.INSTANCE.serviceCardToGenericCardDTO(cardRepository.findServiceCardById(idCard));
            case "TAXES" -> CardMapper.INSTANCE.taxesCardToGenericCardDTO(cardRepository.findTaxesCardById(idCard));
            default -> CardMapper.INSTANCE.cardToGenericCardDTO(cardRepository.findCardById(idCard));
        };
    }

    public int findByRent(CardDTORent  cardDTORent) {
        String typeCard = cardRepository.findCardById(cardDTORent.getIdCard()).getType();
        System.out.println("Entre al servicio de la renta con una tarjeta tipo: "+ typeCard);
        return switch (typeCard) {
            case "PROPERTY" -> findByRentProperties(cardDTORent);
            case "TRANSPORT" -> findByRentTransport(cardDTORent);
            case "SERVICE" -> findByMultiplicatorService(cardDTORent);
            case "TAXES" -> findByRentTaxes(cardDTORent.getIdCard());
            default -> 0;
        };
    }

    public int findByMultiplicatorService(CardDTORent  cardDTORent) {
        ServiceCard serviceCard = cardRepository.findServiceCardById(cardDTORent.getIdCard());
        if (cardDTORent.isMultiplicator()) {
            return serviceCard.getMultiplicator().get(1);
        }else{
            return  serviceCard.getMultiplicator().get(0);
        }
    }

    private int findByRentProperties(CardDTORent cardDTORent) {
        PropertyCard propertyCard = cardRepository.findPropertyCardById(cardDTORent.getIdCard());
        if (cardDTORent.getHotels() != 0) {
            return propertyCard.getRents().get(5);
        } else {
            return switch (cardDTORent.getHouses()) {
                case 1 -> propertyCard.getRents().get(1);
                case 2 -> propertyCard.getRents().get(2);
                case 3 -> propertyCard.getRents().get(3);
                case 4 -> propertyCard.getRents().get(4);
                default -> propertyCard.getRents().get(0);
            };
        }
    }

    public int findByRentTransport(CardDTORent  cardDTORent) {
        TransportCard transportCard = cardRepository.findTransportCardById(cardDTORent.getIdCard());
        System.out.println("Cantidad de transportes: " + cardDTORent.getCantTransport());
        return switch (cardDTORent.getCantTransport()) {
            case 2 -> transportCard.getRents().get(1);
            case 3 -> transportCard.getRents().get(2);
            case 4 -> transportCard.getRents().get(3);
            default -> transportCard.getRents().get(0);
        };
    }

    public int findByRentTaxes(Long idCard) {
        TaxesCard taxesCard = cardRepository.findTaxesCardById(idCard);
        return taxesCard.getRent();
    }

    public List<PropertyCard> findPropertyCard(List<Long> idCards){
        List<PropertyCard> propertyCards = new ArrayList<>();
        for (Long idCard : idCards) {
            String typeCard = cardRepository.findCardById(idCard).getType();
            System.out.println("Validnado id de cards: " + idCard);
            System.out.println("Validnado names cards: " + cardRepository.findCardById(idCard).getName());
            System.out.println("Validnado tipo de cards: " + cardRepository.findCardById(idCard).getType());
            if (typeCard.equals("PROPERTY")){
                propertyCards.add(cardRepository.findPropertyCardById(idCard));
            }
        }
        return propertyCards;
    }

    @Transactional
    public List<CardToBuiltDTO> findPropertyCardBuilt(List<Long> idCards){
        List<CardToBuiltDTO> cardToBuiltDTOS = new ArrayList<>();
        List<PropertyCard> propertyCards = findPropertyCard(idCards);
        for (PropertyCard propertyCard : propertyCards) {
            if (propertyCard.getGroup() != null){
                if (propertyCards.containsAll(propertyCard.getGroup().getProperties())) {
                    cardToBuiltDTOS.add(new CardToBuiltDTO(propertyCard.getId(), propertyCard.getName(),
                            propertyCard.getPriceHouse(),propertyCard.getPriceHotel()));
                }
            }
        }
        return cardToBuiltDTOS;
    }

    public CardToBuiltDTO findCardBuilt(Long idCard) {
        PropertyCard propertyCard = cardRepository.findPropertyCardById(idCard);
        return new CardToBuiltDTO(propertyCard.getId(), propertyCard.getName(),
                propertyCard.getPriceHouse(),propertyCard.getPriceHotel());
    }
}
