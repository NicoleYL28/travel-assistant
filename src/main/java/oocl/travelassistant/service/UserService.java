package oocl.travelassistant.service;

import oocl.travelassistant.dto.UserLoginDTO;
import oocl.travelassistant.dto.UserRegisterDTO;
import oocl.travelassistant.dto.UserResponseDTO;
import oocl.travelassistant.entity.User;
import oocl.travelassistant.exception.*;
import oocl.travelassistant.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponseDTO register(UserRegisterDTO dto) {
        String username = dto.getUsername();
        String email = dto.getEmail();

        // 至少提供一个
        if ((username == null || username.trim().isEmpty()) &&
                (email == null || email.trim().isEmpty())) {
            throw new IllegalArgumentException("注册必须提供用户名或邮箱之一");
        }

        if (username != null && !username.trim().isEmpty()) {
            if (userRepository.findByUsername(username).isPresent()) {
                throw new UsernameExistsException("用户名已存在");
            }
        }
        if (email != null && !email.trim().isEmpty()) {
            if (userRepository.findByEmail(email).isPresent()) {
                throw new EmailExistsException("邮箱已存在");
            }
        }

        if (dto.getPassword() == null || dto.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("密码不能为空");
        }

        User user = new User();
        user.setUsername(username); // 可能是 null
        user.setEmail(email);       // 可能是 null
        user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));

        userRepository.save(user);
        return toResponseDTO(user);
    }

    public UserResponseDTO login(UserLoginDTO dto) {
        String loginKey = dto.getUsernameOrEmail();

        Optional<User> userOpt = userRepository.findByUsernameOrEmail(loginKey, loginKey);
        if (userOpt.isEmpty()) {
            throw new UserNotFoundException("用户不存在");
        }
        User user = userOpt.get();

        if (!passwordEncoder.matches(dto.getPassword(), user.getPasswordHash())) {
            throw new PasswordErrorException("账号或密码错误");
        }

        return toResponseDTO(user);
    }

    private UserResponseDTO toResponseDTO(User user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        return dto;
    }
}
