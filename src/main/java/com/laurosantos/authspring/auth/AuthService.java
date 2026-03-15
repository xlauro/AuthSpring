package com.laurosantos.authspring.auth;

import com.laurosantos.authspring.auth.dto.AuthResponse;
import com.laurosantos.authspring.auth.dto.LoginRequest;
import com.laurosantos.authspring.auth.dto.RegisterRequest;
import com.laurosantos.authspring.exception.AlreadyExistsException;
import com.laurosantos.authspring.exception.InvalidCredentialsException;
import com.laurosantos.authspring.security.JwtService;
import com.laurosantos.authspring.user.Role;
import com.laurosantos.authspring.user.User;
import com.laurosantos.authspring.user.UserMapper;
import com.laurosantos.authspring.user.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AlreadyExistsException("Email already registered");
        }
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new AlreadyExistsException("Username already registered");
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.ROLE_USER)
                .build();
        User saved = userRepository.save(user);

        UserDetails userDetails = toUserDetails(saved);
        String token = jwtService.generateToken(userDetails);
        return AuthResponse.builder()
                .token(token)
                .user(UserMapper.toResponse(saved))
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
        } catch (Exception ex) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));
        UserDetails userDetails = toUserDetails(user);
        String token = jwtService.generateToken(userDetails);
        return AuthResponse.builder()
                .token(token)
                .user(UserMapper.toResponse(user))
                .build();
    }

    private UserDetails toUserDetails(User user) {
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                List.of(new SimpleGrantedAuthority(user.getRole().name()))
        );
    }
}
