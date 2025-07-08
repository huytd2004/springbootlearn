package com.example.springsecurity.config;


import com.example.springsecurity.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
// Lớp JwtAuthenticationFilter là một bộ lọc Spring Security dùng để xác thực người dùng thông qua JWT (JSON Web Token),duoc goi o trong SecurityConfig
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    // UserDetailsService được sử dụng để tải thông tin người dùng từ cơ sở dữ liệu hoặc nguồn dữ liệu khác dựa trên tên người dùng
    @Override
    // Phương thức doFilterInternal được gọi mỗi khi có một yêu cầu HTTP đến ứng dụng, nó sẽ kiểm tra xem yêu cầu có chứa JWT hợp lệ hay không.
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userName;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        // Lấy JWT từ header Authorization
        jwt = authHeader.substring(7);
        userName = jwtService.extractUsername(jwt);

        if (userName != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userName); // Tải thông tin người dùng từ UserDetailsService
            if (jwtService.isTokenValid(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null, // Không cần mật khẩu trong token xác thực
                        userDetails.getAuthorities()
                ); // Tạo một đối tượng UsernamePasswordAuthenticationToken với thông tin người dùng và quyền hạn của họ
                authToken.setDetails( // gắn thêm thông tin phụ của request như ip,...
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                SecurityContextHolder.getContext().setAuthentication(authToken); //Đánh dấu request là đã được xác thực
            }
        }
        filterChain.doFilter(request, response);
    }
}
