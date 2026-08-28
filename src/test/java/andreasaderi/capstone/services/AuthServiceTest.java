package andreasaderi.capstone.services;

import andreasaderi.capstone.entities.User;
import andreasaderi.capstone.exceptions.NotFoundException;
import andreasaderi.capstone.exceptions.UnauthorizedException;
import andreasaderi.capstone.requestDTOs.LoginDTO;
import andreasaderi.capstone.security.JWTTools;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
    @Mock
    private UserService userService;

    @Mock
    private PasswordEncoder bcrypt;

    @Mock
    private JWTTools jwtTools;

    @InjectMocks
    private AuthService authService;

    @Test
    void checkCredentialsAndGenerateTokenShouldReturnTokenWhenCredentialsAreCorrect() {

        User user = new User();

        LoginDTO loginDTO = new LoginDTO(
                "andrea@email.com",
                "Password123"
        );

        when(userService.findByEmail(loginDTO.email()))
                .thenReturn(user);

        when(bcrypt.matches(
                loginDTO.password(),
                user.getPassword()
        )).thenReturn(true);

        when(jwtTools.generateToken(user))
                .thenReturn("fake-jwt-token");

        String token = authService.checkCredentialsAndGenerateToken(loginDTO);

        assertEquals("fake-jwt-token", token);

        verify(jwtTools).generateToken(user);
    }

    @Test
    void checkCredentialsAndGenerateTokenShouldThrowUnauthorizedExceptionWhenPasswordIsWrong() {

        User user = new User();

        LoginDTO loginDTO = new LoginDTO(
                "andrea@email.com",
                "WrongPassword123"
        );

        when(userService.findByEmail(loginDTO.email()))
                .thenReturn(user);

        when(bcrypt.matches(
                loginDTO.password(),
                user.getPassword()
        )).thenReturn(false);

        assertThrows(
                UnauthorizedException.class,
                () -> authService.checkCredentialsAndGenerateToken(loginDTO)
        );

        verify(jwtTools, never()).generateToken(user);
    }

    @Test
    void checkCredentialsAndGenerateTokenShouldThrowExceptionWhenEmailIsNotFound() {

        LoginDTO loginDTO = new LoginDTO(
                "notfound@email.com",
                "Password123"
        );

        when(userService.findByEmail(loginDTO.email()))
                .thenThrow(new NotFoundException("User not found"));

        assertThrows(
                NotFoundException.class,
                () -> authService.checkCredentialsAndGenerateToken(loginDTO)
        );

        verify(bcrypt, never()).matches(anyString(), anyString());

        verify(jwtTools, never()).generateToken(any());
    }
}
