package com.ballastlane.pokemon.domain.port.out;

import com.ballastlane.pokemon.domain.model.User;
import java.util.Optional;

public interface UserRepositoryPort {
    User save(User user);

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);
}
