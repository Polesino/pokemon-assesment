package com.ballastlane.pokemon.infrastructure.security;

import com.ballastlane.pokemon.domain.model.User;

public interface JwtTokenProvider {
    String generateToken(User user);
}
