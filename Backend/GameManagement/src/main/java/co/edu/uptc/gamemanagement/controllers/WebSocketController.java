package co.edu.uptc.gamemanagement.controllers;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {

    @MessageMapping("/sendMessage") // Mapeo para recibir mensajes
    @SendTo("/topic/messages") // Destino para enviar mensajes a los clientes
    public String handleMessage(String message) {
        return "Echo: " + message; // Respuesta que se enviará a los clientes suscritos
    }
}
