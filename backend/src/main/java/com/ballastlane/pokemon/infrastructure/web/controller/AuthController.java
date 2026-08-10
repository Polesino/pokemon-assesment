package com.ballastlane.pokemon.infrastructure.web.controller;

import com.ballastlane.pokemon.domain.port.in.UserAuthUseCase;
import com.ballastlane.pokemon.infrastructure.web.dto.AuthRequestDto;
import com.ballastlane.pokemon.infrastructure.web.dto.AuthResponseDto;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final UserAuthUseCase userAuthUseCase;

    public AuthController(UserAuthUseCase userAuthUseCase) {
        this.userAuthUseCase = userAuthUseCase;
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@Valid @RequestBody AuthRequestDto request) {
        userAuthUseCase.register(request.username(), request.password());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody AuthRequestDto request) {
        String token = userAuthUseCase.login(request.username(), request.password());
        return ResponseEntity.ok(new AuthResponseDto(token));
    }
}
