package com.laurosantos.authspring.user;

import com.laurosantos.authspring.security.JwtService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtService jwtService;

    @AfterEach
    void clean() {
        userRepository.deleteAll();
    }

    @Test
    void me_shouldReturnAuthenticatedUser() throws Exception {
        UserEntityPair pair = createUserWithToken();

        mockMvc.perform(get("/api/users/me")
                        .header("Authorization", "Bearer " + pair.token())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(pair.entity().getEmail()))
                .andExpect(jsonPath("$.username").value(pair.entity().getUsername()));
    }

    private UserEntityPair createUserWithToken() {
        com.laurosantos.authspring.user.User entity = com.laurosantos.authspring.user.User.builder()
                .username("dave")
                .email("dave@example.com")
                .password(passwordEncoder.encode("password123"))
                .role(Role.ROLE_USER)
                .build();
        com.laurosantos.authspring.user.User saved = userRepository.save(entity);

        UserDetails userDetails = new User(saved.getEmail(), saved.getPassword(), List.of(new SimpleGrantedAuthority(saved.getRole().name())));
        String token = jwtService.generateToken(userDetails);
        return new UserEntityPair(saved, token);
    }

    private record UserEntityPair(com.laurosantos.authspring.user.User entity, String token) {
    }
}
