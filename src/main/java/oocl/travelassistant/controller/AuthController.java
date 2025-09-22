package oocl.travelassistant.controller;

import oocl.travelassistant.dto.UserLoginDTO;
import oocl.travelassistant.dto.UserRegisterDTO;
import oocl.travelassistant.dto.UserLoginResponseDTO;
import oocl.travelassistant.dto.UserRegisterResponseDto;
import oocl.travelassistant.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")
public class AuthController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public UserRegisterResponseDto register(@RequestBody UserRegisterDTO dto) {
        return userService.register(dto);
    }

    @PostMapping("/login")
    public UserLoginResponseDTO login(@RequestBody UserLoginDTO dto) {
        return userService.login(dto);
    }
}