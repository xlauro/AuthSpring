package com.laurosantos.authspring.auth;

import com.fasterxml.jackson.databind.JsonNode;
import com.laurosantos.authspring.user.Role;
import com.laurosantos.authspring.user.User;
import com.laurosantos.authspring.user.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    private final com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    void register_shouldReturnCreatedWithToken() throws Exception {
        String email = "alice" + System.nanoTime() + "@example.com";
        String payload = "{" +
                "\"username\":\"alice\"," +
                "\"email\":\"" + email + "\"," +
                "\"password\":\"password123\"" +
                "}";

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.user.email").value(email));
    }

    @Test
    void login_shouldReturnToken() throws Exception {
        User user = User.builder()
                .username("bob")
                .email("bob@example.com")
                .password(passwordEncoder.encode("secret123"))
                .role(Role.ROLE_USER)
                .build();
        userRepository.save(user);

        String payload = "{" +
                "\"email\":\"bob@example.com\"," +
                "\"password\":\"secret123\"" +
                "}";

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.user.username").value("bob"));
    }

    @Test
    void register_thenLogin_shouldReuseCreatedUser() throws Exception {
        String email = "carol" + System.nanoTime() + "@example.com";
        String registerPayload = "{" +
                "\"username\":\"carol\"," +
                "\"email\":\"" + email + "\"," +
                "\"password\":\"password123\"" +
                "}";

        MvcResult result = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerPayload))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
        assertThat(json.get("token").asText()).isNotBlank();

        String loginPayload = "{" +
                "\"email\":\"" + email + "\"," +
                "\"password\":\"password123\"" +
                "}";

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginPayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.email").value(email));
    }
}
