package com.quizbotv2.dto.userdtos;

import java.time.Instant;

public record UserDTO(
        Long id,
        String email,
        String pictureUrl,
        Instant firstLogin,
        Instant lastLogin
) {
}

