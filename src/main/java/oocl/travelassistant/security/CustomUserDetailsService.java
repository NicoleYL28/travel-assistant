package oocl.travelassistant.security;

import oocl.travelassistant.entity.User;
import oocl.travelassistant.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    @Override
    public UserDetails loadUserByUsername(String userIdStr) throws UsernameNotFoundException {
        Long id = Long.valueOf(userIdStr);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("用户不存在"));
        return org.springframework.security.core.userdetails.User
                .withUsername(String.valueOf(user.getId()))
                .password(user.getPasswordHash())
                .authorities(Collections.emptyList())
                .accountLocked(false)
                .disabled(false)
                .build();
    }
}
