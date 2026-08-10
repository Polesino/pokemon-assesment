package com.ballastlane.pokemon.domain.port.in;

import com.ballastlane.pokemon.domain.model.User;

public interface UserAuthUseCase {
    String login(String username, String password);

    User register(String username, String password);
}
