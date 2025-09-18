package mgt.result.sage.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import mgt.result.sage.dto.AuthRequest;
import mgt.result.sage.dto.RegisterRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String email;
    private String password;
    private AuthRequest authRequest;
    private RegisterRequest registerRequest;

    @BeforeEach
    void setUp() {
        email = "testuser@example.com";
        password = "password123";
        authRequest = new AuthRequest(email, password);
        registerRequest = new RegisterRequest("first", "last", email, password);
    }

    private void firstRegister() throws Exception {
        mockMvc.perform(post("/v1/auth/register")
                .content(objectMapper.writeValueAsString(registerRequest))
                .contentType(MediaType.APPLICATION_JSON));
    }

    private String firstRegisterAndGetResponse() throws Exception {
        return mockMvc.perform(post("/v1/auth/register")
                        .content(objectMapper.writeValueAsString(registerRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();
    }

    @Test
    void register_ShouldReturnTokens_WhenUserIsNew() throws Exception {
        mockMvc.perform(post("/v1/auth/register")
                        .content(objectMapper.writeValueAsString(registerRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.refreshToken").exists());
    }

    @Test
    void login_ShouldReturnTokens_WhenCredentialsAreValid() throws Exception {
        // First register
        firstRegister();

        // Then login
        mockMvc.perform(post("/v1/auth/login")
                        .content(objectMapper.writeValueAsString(authRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.refreshToken").exists());
    }



    @Test
    void login_ShouldFail_WhenPasswordIsInvalid() throws Exception {
        AuthRequest wrongRequest = new AuthRequest(email, "wrongPass");
        // Register first
       firstRegister();

        // Wrong password
        mockMvc.perform(post("/v1/auth/login")
                        .content(objectMapper.writeValueAsString(wrongRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void refresh_ShouldReturnNewTokens_WhenRefreshTokenIsValid() throws Exception {
        // Register
        String response = firstRegisterAndGetResponse();

        String refreshToken = objectMapper.readTree(response).get("refreshToken").asText();

        // Refresh
        mockMvc.perform(post("/v1/auth/refresh")
                        .param("email", email)
                        .param("refreshToken", refreshToken))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.refreshToken").exists());
    }



    @Test
    void refresh_ShouldFail_WhenRefreshTokenIsInvalid() throws Exception {
        mockMvc.perform(post("/auth/refresh")
                        .param("email", email)
                        .param("refreshToken", "invalid.token.here"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void me_ShouldReturnUserEmail_WhenAccessTokenIsValid() throws Exception {
        // Register
        String response = firstRegisterAndGetResponse();

        String accessToken = objectMapper.readTree(response).get("accessToken").asText();

        mockMvc.perform(get("/v1/auth/me")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(content().string("You are logged in as: " + email));
    }

    @Test
    void logout_ShouldInvalidateRefreshToken() throws Exception {
        // Register
        String response = firstRegisterAndGetResponse();

        String accessToken = objectMapper.readTree(response).get("accessToken").asText();

        // Logout
        mockMvc.perform(post("/v1/auth/logout")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(content().string("Logged out successfully"));
    }
}
