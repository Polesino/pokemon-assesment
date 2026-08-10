package com.ballastlane.pokemon.application.usecase;

import com.ballastlane.pokemon.domain.model.User;
import com.ballastlane.pokemon.domain.port.in.UserAuthUseCase;
import com.ballastlane.pokemon.domain.port.out.UserRepositoryPort;
import com.ballastlane.pokemon.infrastructure.security.JwtTokenProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class UserAuthUseCaseImpl implements UserAuthUseCase {
    private static final String DEFAULT_ROLE = "ROLE_USER";

    private final UserRepositoryPort userRepositoryPort;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public UserAuthUseCaseImpl(
            UserRepositoryPort userRepositoryPort,
            PasswordEncoder passwordEncoder,
            JwtTokenProvider jwtTokenProvider
    ) {
        this.userRepositoryPort = userRepositoryPort;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public String login(String username, String password) {
        User user = userRepositoryPort.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Invalid username or password"));

        if (!passwordEncoder.matches(password, user.password())) {
            throw new IllegalArgumentException("Invalid username or password");
        }

        return jwtTokenProvider.generateToken(user);
    }

    @Override
    public User register(String username, String password) {
        if (userRepositoryPort.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already exists");
        }

        User user = new User(null, username, passwordEncoder.encode(password), DEFAULT_ROLE);
        return userRepositoryPort.save(user);
    }
}
