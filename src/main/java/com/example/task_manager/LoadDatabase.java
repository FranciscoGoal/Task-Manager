package com.example.task_manager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class LoadDatabase {

    private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);

    @Bean
    CommandLineRunner hashPasswords(UserRepository userRepository) {
        return args -> {
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            for (User user : userRepository.findAll()) {
                if (!user.getPassword().startsWith("$2a$")) {
                    String raw = user.getPassword();
                    user.setPassword(encoder.encode(raw));
                    userRepository.save(user);
                    log.info("Hashed password for user: " + user.getUsername());
                }
            }
        };
    }
}
