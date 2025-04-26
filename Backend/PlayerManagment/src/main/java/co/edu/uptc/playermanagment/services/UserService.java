package co.edu.uptc.playermanagment.services;

import co.edu.uptc.playermanagment.DTOs.LoginDTO;
import co.edu.uptc.playermanagment.DTOs.UserDTO;
import co.edu.uptc.playermanagment.entities.User;
import co.edu.uptc.playermanagment.mappers.UserMapper;
import co.edu.uptc.playermanagment.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Objects;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public HashMap<String, Object> createUser(UserDTO user) {
        HashMap<String, Object> response = new HashMap<>();
        if (!userRepository.existsById(user.getNickname())){
            if (!userRepository.existsByEmail(user.getEmail())){
                userRepository.save(UserMapper.INSTANCE.DTOToUser(user));
                response.put("success", true);
                response.put("confirm", "Usuario creado con exito");
            }else{
                response.put("success", false);
                response.put("error", "Ya existe un usuario con ese email");
            }
        }else{
            response.put("success", false);
            response.put("error", "Ya existe un usuario con nickname");
        }
        return response;
    }

    public HashMap<String, Object> login(LoginDTO login) {
        HashMap<String,Object> response = new HashMap<>();
        User user = userRepository.findUserByEmail(login.getEmail());
        if (user!=null){
            if (user.getPassword().equals(login.getPassword())){
                response.put("success", true);
                response.put("nickname", user.getNickname());
            }else {
                response.put("success", false);
                response.put("error","Contraseña incorrecta");
            }
        }else {
            response.put("success", false);
            response.put("error","Usuario no encontrado");
        }
        return response;
    }

}
