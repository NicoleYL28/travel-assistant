package oocl.travelassistant.config;

import org.springframework.beans.factory.annotation.Autowired;import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
public class SecurityConfig {
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // ✅ Spring Security 配置
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 关掉 CSRF（方便 Postman 测试）
                .csrf(csrf -> csrf.disable())

                // 配置请求规则
                .authorizeHttpRequests(auth -> auth
                        // 放行 Swagger / 静态资源 / 注册登录接口
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/api/auth/**").permitAll()
                        // 其他接口全放行（开发阶段用）
                        .anyRequest().permitAll()
                );

        return http.build();
    }
}

