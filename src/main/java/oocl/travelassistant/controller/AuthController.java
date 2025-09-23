package oocl.travelassistant.controller;

import lombok.RequiredArgsConstructor;
import oocl.travelassistant.dto.UserLoginDTO;
import oocl.travelassistant.dto.UserLoginResponseDTO;
import oocl.travelassistant.dto.UserRegisterDTO;
import oocl.travelassistant.dto.UserRegisterResponseDto;
import oocl.travelassistant.service.UserService;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    public UserRegisterResponseDto register(@RequestBody UserRegisterDTO userRegisterDTO) {
        return userService.register(userRegisterDTO);
    }

    @PostMapping("/login")
    public UserLoginResponseDTO login(@RequestBody UserLoginDTO userLoginDTO) {
        return userService.login(userLoginDTO);
    }
}