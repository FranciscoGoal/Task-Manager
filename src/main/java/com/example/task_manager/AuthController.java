package com.example.task_manager;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        try {
            return ResponseEntity.ok(authService.login(
                    body.get("username"), body.get("password")));
        } catch (RuntimeException e) {
            return ResponseEntity.status(401)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> body) {
        try {
            String username = body.get("username");
            String password = body.get("password");
            if (username == null || username.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "El usuario no puede estar vacío"));
            }
            if (password == null || password.length() < 4) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "La contraseña debe tener al menos 4 caracteres"));
            }
            return ResponseEntity.ok(authService.register(username.trim(), password));
        } catch (RuntimeException e) {
            return ResponseEntity.status(409)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
