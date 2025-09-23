package oocl.travelassistant.service;

import oocl.travelassistant.dto.UserLoginDTO;
import oocl.travelassistant.dto.UserResponseDTO;
import oocl.travelassistant.dto.UserRegisterDTO;
import oocl.travelassistant.entity.User;
import oocl.travelassistant.exception.EmailExistsException;
import oocl.travelassistant.exception.PasswordErrorException;
import oocl.travelassistant.exception.UserNotFoundException;
import oocl.travelassistant.exception.UsernameExistsException;
import oocl.travelassistant.repository.UserRepository;
import oocl.travelassistant.security.JwtTokenProvider;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public UserService(UserRepository userRepository,
                       BCryptPasswordEncoder passwordEncoder,
                       JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    // 修改：注册也返回统一的 UserResponseDTO（含 token）
    public UserResponseDTO register(UserRegisterDTO dto) {
        String username = dto.getUsername();
        String email = dto.getEmail();

        if ((username == null || username.trim().isEmpty()) &&
                (email == null || email.trim().isEmpty())) {
            throw new IllegalArgumentException("注册必须提供用户名或邮箱之一");
        }
        if (username != null && !username.trim().isEmpty() && userRepository.findByUsername(username).isPresent()) {
            throw new UsernameExistsException("用户名已存在");
        }
        if (email != null && !email.trim().isEmpty() && userRepository.findByEmail(email).isPresent()) {
            throw new EmailExistsException("邮箱已存在");
        }
        if (dto.getPassword() == null || dto.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("密码不能为空");
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        userRepository.save(user);

        return generateResponse(user);
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
//        String token = jwtTokenProvider.generateToken(
//                user.getId(),
//                user.getUsername(),
//                user.getEmail()
//        );
//        UserResponseDTO userResponseDTO = new UserResponseDTO();
//        userResponseDTO.setId(user.getId());
//        userResponseDTO.setEmail(user.getEmail());
//        userResponseDTO.setUsername(user.getUsername());
//        userResponseDTO.setToken(token);
        return generateResponse(user);
    }

    UserResponseDTO generateResponse(User user){
        UserResponseDTO userResponseDTO = new UserResponseDTO();
        String token = jwtTokenProvider.generateToken(
                user.getId(),
                user.getUsername(),
                user.getEmail()
        );
        userResponseDTO.setId(user.getId());
        userResponseDTO.setEmail(user.getEmail());
        userResponseDTO.setUsername(user.getUsername());
        userResponseDTO.setToken(token);
        return userResponseDTO;
    }
}
