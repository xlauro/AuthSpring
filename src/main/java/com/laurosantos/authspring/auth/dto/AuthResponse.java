package com.laurosantos.authspring.auth.dto;

import com.laurosantos.authspring.user.dto.UserResponse;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class AuthResponse {
    String token;
    UserResponse user;
}
