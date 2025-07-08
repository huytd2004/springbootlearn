package com.example.springsecurity.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
//Lớp JwtService là một Spring Service dùng để xử lý các thao tác liên quan đến JWT (JSON Web Token) như tạo, xác thực và trích xuất thông tin từ token.
public class JwtService {

    @Value("${jwt.secret}") // Lấy giá trị secret key từ file cấu hình (application.properties hoặc application.yml)
    private String secretKey;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    } // Trích xuất tên người dùng từ token JWT

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    } // Trích xuất các claim(thông tin) từ token JWT và áp dụng hàm claimsResolver để lấy giá trị mong muốn

    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    } // Tạo token JWT bằng cách sử dụng thông tin người dùng từ UserDetails, với các claim bổ sung là một HashMap rỗng

    public String generateToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails
    ) {
        return buildToken(extraClaims, userDetails, jwtExpiration);
    } // Tạo token JWT

    private String buildToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails,
            long expiration
    ) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    } // Tạo token JWT bằng cách sử dụng các claim bổ sung, tên người dùng từ UserDetails, thời gian phát hành và thời gian hết hạn, sau đó ký token bằng khóa bí mật

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    } // Kiểm tra tính hợp lệ của token JWT bằng cách so sánh tên người dùng trong token với tên người dùng trong UserDetails và kiểm tra xem token đã hết hạn hay chưa

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    } // Kiểm tra xem token JWT đã hết hạn hay chưa bằng cách so sánh ngày hết hạn với ngày hiện tại

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    } // Trích xuất ngày hết hạn của token JWT

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder() // tạo một builder để phân tích token
                .setSigningKey(getSignInKey()) // đặt khóa bí mật để xác thực chữ ký của token
                .build() // xây dựng
                .parseClaimsJws(token) // phân tích token và trả về một đối tượng Jws<Claims>
                .getBody(); // lấy phần thân của token, chứa các claim (thông tin) đã được mã hóa
    } // Trích xuất tất cả các claim(thông tin) từ token JWT bằng cách sử dụng khóa bí mật để xác thực chữ ký của token

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    } // Chuyển đổi chuỗi secretKey từ định dạng Base64 thành một Key sử dụng thuật toán HMAC SHA-256 để ký token JWT
}
