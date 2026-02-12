package logiflow.authservice.dto.mapper;

import logiflow.authservice.dto.response.AuthResponse;
import logiflow.authservice.model.Role;
import logiflow.authservice.model.User;

import java.util.stream.Collectors;

public class UserMapper {
    public static AuthResponse toAuthResponse(User user, String accessToken, String refreshToken) {
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .username(user.getUsername())
                .email(user.getEmail())
                .roles(user.getRoles().stream().map(role -> role.getName().name()).collect(Collectors.toSet()))
                .build();
    }

    public static logiflow.authservice.dto.response.UserResponse toUserResponse(User user) {
        return logiflow.authservice.dto.response.UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .roles(user.getRoles().stream().map(role -> role.getName().name()).collect(Collectors.toSet()))
                .build();
    }
}

