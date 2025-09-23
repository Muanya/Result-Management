package mgt.result.sage.services;

import mgt.result.sage.dto.AuthToken;
import mgt.result.sage.dto.RegisterRequest;
import mgt.result.sage.entity.User;
import mgt.result.sage.repository.UserRepository;
import mgt.result.sage.service.AuthService;
import mgt.result.sage.utils.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    private String firstName = "FirstName";
    private String lastName = "LastName";
    private String email = "unit@test.com";
    private String password = "password123";
    private User user;
    private RegisterRequest req;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setId(1L);
        user.setEmail(email);
        user.setPassword("encodedPass");

        req = new RegisterRequest(firstName, lastName, email, password, "student");

    }

    @Test
    void register_ShouldCreateUser_WhenEmailNotExists() {
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(password)).thenReturn("encodedPass");
        when(jwtUtil.generateAccessToken(email)).thenReturn("access123");
        when(jwtUtil.generateRefreshToken(email)).thenReturn("refresh123");

        AuthToken response = authService.register(req);

        assertThat(response.getAccessToken()).isEqualTo("access123");
        assertThat(response.getRefreshToken()).isEqualTo("refresh123");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_ShouldFail_WhenEmailExists() {
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        assertThrows(RuntimeException.class,
                () -> authService.register(req));
    }

    @Test
    void login_ShouldReturnTokens_WhenCredentialsAreValid() {
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, user.getPassword())).thenReturn(true);
        when(jwtUtil.generateAccessToken(email)).thenReturn("access123");
        when(jwtUtil.generateRefreshToken(email)).thenReturn("refresh123");

        AuthToken response = authService.login(email, password);

        assertThat(response.getAccessToken()).isEqualTo("access123");
        assertThat(response.getRefreshToken()).isEqualTo("refresh123");
    }

    @Test
    void login_ShouldFail_WhenPasswordIsWrong() {
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, user.getPassword())).thenReturn(false);

        assertThrows(RuntimeException.class,
                () -> authService.login(email, password));
    }

    @Test
    void refreshToken_ShouldReturnNewTokens_WhenValid() {
        String refreshToken = "refreshOld";
        when(jwtUtil.validateToken(refreshToken, email)).thenReturn(true);
        when(jwtUtil.extractEmail(refreshToken)).thenReturn(email);
        user.setRefreshToken(refreshToken);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(jwtUtil.generateAccessToken(email)).thenReturn("accessNew");
        when(jwtUtil.generateRefreshToken(email)).thenReturn("refreshNew");

        AuthToken response = authService.refreshUserToken(email, refreshToken);

        assertThat(response.getAccessToken()).isEqualTo("accessNew");
        assertThat(response.getRefreshToken()).isEqualTo("refreshNew");
        verify(userRepository).save(user);
    }

    @Test
    void refreshToken_ShouldFail_WhenInvalid() {
        when(jwtUtil.validateToken("badToken", email)).thenReturn(false);

        assertThrows(RuntimeException.class,
                () -> authService.refreshUserToken(email,"badToken"));
    }

    @Test
    void logout_ShouldClearRefreshToken() {
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        authService.logout(email);

        assertThat(user.getRefreshToken()).isNull();
        verify(userRepository).save(user);
    }
}
