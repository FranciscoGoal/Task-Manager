package com.example.task_manager;

import java.util.Map;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public AuthService(UserRepository userRepository, JwtService jwtService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    public Map<String, Object> login(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!encoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Contraseña incorrecta");
        }

        String token = jwtService.generateToken(user.getUserId(), user.getUsername());
        return Map.of(
                "token", token,
                "userId", user.getUserId(),
                "username", user.getUsername()
        );
    }

    public Map<String, Object> register(String username, String password) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("El usuario ya existe");
        }
        User user = new User(username, encoder.encode(password));
        user = userRepository.save(user);
        String token = jwtService.generateToken(user.getUserId(), user.getUsername());
        return Map.of(
                "token", token,
                "userId", user.getUserId(),
                "username", user.getUsername()
        );
    }
}
