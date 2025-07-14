package com.example.springsecurity.config;

import com.example.springsecurity.service.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity // Kích hoạt @PreAuthorize và các annotation liên quan
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        //Dùng để cấu hình bảo mật cho ứng dụng Spring Security, được gọi trước mỗi yêu cầu HTTP.
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/auth/**", "/h2-console/**").permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)// Thêm bộ lọc JwtAuthenticationFilter trước bộ lọc UsernamePasswordAuthenticationFilter để xử lý xác thực JWT
                .oauth2Login(oauth2 -> oauth2
                        .defaultSuccessUrl("/api/v1/auth/userinfo", true)
                        .userInfoEndpoint(userInfo -> userInfo // Cấu hình điểm cuối thông tin người dùng OAuth2
                                .userService(customOAuth2UserService()) // Sử dụng CustomOAuth2UserService để xử lý thông tin người dùng OAuth2
                        )
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("https://accounts.google.com/Logout") // Redirect to Google logout
                        .deleteCookies("JSESSIONID")
                        .invalidateHttpSession(true)
                );

        return http.build();
    }
    @Bean // Dùng để cung cấp UserDetailsService cho Spring Security
    public CustomOAuth2UserService customOAuth2UserService() {
        return new CustomOAuth2UserService();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        //Dùng để mã hóa mật khẩu người dùng
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        //Dùng để cung cấp AuthenticationManager cho Spring Security
        return config.getAuthenticationManager();
    }

}