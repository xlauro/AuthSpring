package com.laurosantos.authspring.auth;

import com.laurosantos.authspring.auth.dto.AuthResponse;
import com.laurosantos.authspring.auth.dto.LoginRequest;
import com.laurosantos.authspring.auth.dto.RegisterRequest;
import com.laurosantos.authspring.exception.AlreadyExistsException;
import com.laurosantos.authspring.exception.InvalidCredentialsException;
import com.laurosantos.authspring.security.JwtService;
import com.laurosantos.authspring.user.Role;
import com.laurosantos.authspring.user.User;
import com.laurosantos.authspring.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.userdetails.UserDetails;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    private RegisterRequest registerRequest;
    private User savedUser;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setUsername("jane");
        registerRequest.setEmail("jane@example.com");
        registerRequest.setPassword("secret123");

        savedUser = User.builder()
                .id(1L)
                .username("jane")
                .email("jane@example.com")
                .password("encoded")
                .role(Role.ROLE_USER)
                .build();
    }

    @Test
    void register_shouldCreateUserAndReturnToken() {
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(false);
        when(userRepository.existsByUsername(registerRequest.getUsername())).thenReturn(false);
        when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(jwtService.generateToken(any(UserDetails.class))).thenReturn("jwt-token");

        AuthResponse response = authService.register(registerRequest);

        assertThat(response.getToken()).isEqualTo("jwt-token");
        assertThat(response.getUser().getEmail()).isEqualTo("jane@example.com");
    }

    @Test
    void register_shouldFailWhenEmailExists() {
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(true);

        assertThatThrownBy(() -> authService.register(registerRequest))
                .isInstanceOf(AlreadyExistsException.class)
                .hasMessageContaining("Email");
    }

    @Test
    void register_shouldFailWhenUsernameExists() {
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(false);
        when(userRepository.existsByUsername(registerRequest.getUsername())).thenReturn(true);

        assertThatThrownBy(() -> authService.register(registerRequest))
                .isInstanceOf(AlreadyExistsException.class)
                .hasMessageContaining("Username");
    }

    @Test
    void login_shouldReturnTokenOnSuccess() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("jane@example.com");
        loginRequest.setPassword("secret123");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(new UsernamePasswordAuthenticationToken("jane@example.com", "secret123"));
        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(java.util.Optional.of(savedUser));
        when(jwtService.generateToken(any(UserDetails.class))).thenReturn("jwt-token");

        AuthResponse response = authService.login(loginRequest);

        assertThat(response.getToken()).isEqualTo("jwt-token");
        assertThat(response.getUser().getUsername()).isEqualTo("jane");
    }

    @Test
    void login_shouldFailOnBadCredentials() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("jane@example.com");
        loginRequest.setPassword("wrong");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("bad"));

        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(InvalidCredentialsException.class);
    }
}
