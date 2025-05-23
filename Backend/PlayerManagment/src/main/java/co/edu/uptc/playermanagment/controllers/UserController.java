package co.edu.uptc.playermanagment.controllers;

import co.edu.uptc.playermanagment.DTOs.LoginDTO;
import co.edu.uptc.playermanagment.DTOs.UserDTO;
import co.edu.uptc.playermanagment.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController()
@RequestMapping("/User")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/Create")
    public ResponseEntity<HashMap<String, Object>> createUser(@RequestBody UserDTO user) {
        return ResponseEntity.ok(userService.createUser(user));
    }

    @PostMapping("/Login")
    public ResponseEntity<HashMap<String, Object>> login(@RequestBody LoginDTO login) {
        return ResponseEntity.ok(userService.login(login));
    }

    @GetMapping("/Validate/{nickName}")
    public ResponseEntity<Boolean> validateNickName(@PathVariable String nickName) {
        return ResponseEntity.ok(userService.validateNickName(nickName));
    }
}
