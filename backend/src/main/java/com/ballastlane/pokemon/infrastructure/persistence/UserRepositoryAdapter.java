package com.ballastlane.pokemon.infrastructure.persistence;

import com.ballastlane.pokemon.domain.model.User;
import com.ballastlane.pokemon.domain.port.out.UserRepositoryPort;
import com.ballastlane.pokemon.infrastructure.persistence.entity.UserEntity;
import com.ballastlane.pokemon.infrastructure.persistence.repository.JpaUserRepository;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class UserRepositoryAdapter implements UserRepositoryPort {
    private final JpaUserRepository jpaUserRepository;

    public UserRepositoryAdapter(JpaUserRepository jpaUserRepository) {
        this.jpaUserRepository = jpaUserRepository;
    }

    @Override
    public User save(User user) {
        UserEntity savedEntity = jpaUserRepository.save(toEntity(user));
        return toDomain(savedEntity);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return jpaUserRepository.findByUsername(username)
                .map(this::toDomain);
    }

    @Override
    public boolean existsByUsername(String username) {
        return jpaUserRepository.existsByUsername(username);
    }

    private UserEntity toEntity(User user) {
        UserEntity entity = new UserEntity();
        entity.setId(user.id());
        entity.setUsername(user.username());
        entity.setPassword(user.password());
        entity.setRole(user.role());
        return entity;
    }

    private User toDomain(UserEntity entity) {
        return new User(
                entity.getId(),
                entity.getUsername(),
                entity.getPassword(),
                entity.getRole()
        );
    }
}
