package com.ballastlane.pokemon.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ballastlane.pokemon.domain.model.User;
import com.ballastlane.pokemon.domain.port.out.UserRepositoryPort;
import com.ballastlane.pokemon.infrastructure.security.JwtTokenProvider;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserAuthUseCaseTest {
    @Mock
    private UserRepositoryPort userRepositoryPort;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private UserAuthUseCaseImpl useCase;

    @Test
    void shouldRegisterNewUserSuccessfully() {
        when(userRepositoryPort.existsByUsername("ash")).thenReturn(false);
        when(passwordEncoder.encode("pikachu")).thenReturn("encoded-password");
        when(userRepositoryPort.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0, User.class));

        User registeredUser = useCase.register("ash", "pikachu");

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepositoryPort).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertThat(savedUser.username()).isEqualTo("ash");
        assertThat(savedUser.password()).isEqualTo("encoded-password");
        assertThat(savedUser.role()).isEqualTo("ROLE_USER");
        assertThat(registeredUser).isEqualTo(savedUser);
        verify(passwordEncoder).encode("pikachu");
    }

    @Test
    void shouldThrowExceptionWhenUsernameAlreadyExists() {
        when(userRepositoryPort.existsByUsername("ash")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> useCase.register("ash", "pikachu"));
    }

    @Test
    void shouldAuthenticateAndReturnJwtToken() {
        User existingUser = new User(1L, "ash", "encoded-password", "ROLE_USER");
        when(userRepositoryPort.findByUsername("ash")).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches("pikachu", "encoded-password")).thenReturn(true);
        when(jwtTokenProvider.generateToken(existingUser)).thenReturn("jwt-token");

        String token = useCase.login("ash", "pikachu");

        assertThat(token).isNotNull();
        assertThat(token).isEqualTo("jwt-token");
        verify(userRepositoryPort).findByUsername("ash");
        verify(passwordEncoder).matches("pikachu", "encoded-password");
        verify(jwtTokenProvider).generateToken(existingUser);
    }
}
