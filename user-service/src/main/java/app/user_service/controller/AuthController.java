package app.user_service.controller;

import app.user_service.dto.LoginRequest;
import app.user_service.dto.LoginResponse;
import app.user_service.dto.RegisterRequest;
import app.user_service.security.JwtUtil;
import app.user_service.service.TokenBlacklistService;
import app.user_service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final TokenBlacklistService blacklistService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        String result = userService.register(request);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        LoginResponse response = userService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            Date expirationDate = jwtUtil.extractExpiration(token);
            long ttl = expirationDate.getTime() - System.currentTimeMillis();
            blacklistService.blacklistToken(token, ttl);
            return ResponseEntity.ok("Logout successful. Token blacklisted.");
        }
        return ResponseEntity.badRequest().body("Invalid Authorization header.");
    }
}
