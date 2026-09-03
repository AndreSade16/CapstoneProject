package andreasaderi.capstone.controllers;

import andreasaderi.capstone.entities.User;
import andreasaderi.capstone.exceptions.ValidationException;
import andreasaderi.capstone.requestDTOs.ForgotPasswordDTO;
import andreasaderi.capstone.requestDTOs.LoginDTO;
import andreasaderi.capstone.requestDTOs.ResetPasswordDTO;
import andreasaderi.capstone.requestDTOs.UserDTO;
import andreasaderi.capstone.responseDTOs.LoginResponseDTO;
import andreasaderi.capstone.responseDTOs.UserRegistrationResponseDTO;
import andreasaderi.capstone.services.AuthService;
import andreasaderi.capstone.services.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final AuthService authService;

    public AuthController(UserService userService, AuthService authService) {
        this.userService = userService;
        this.authService = authService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserRegistrationResponseDTO registerUser(@ModelAttribute @Validated UserDTO body, @RequestPart(value = "avatar", required = false) MultipartFile profileImage, BindingResult validationResult) {
        if (validationResult.hasErrors()) {
            List<String> errorsList = validationResult.getFieldErrors().stream()
                    .map(fieldError -> fieldError.getDefaultMessage())
                    .toList();
            throw new ValidationException(errorsList);
        }

        User saved = userService.register(body, profileImage);

        return new UserRegistrationResponseDTO(saved.getUserId());
    }

    @PostMapping("/login")
    public LoginResponseDTO login(@RequestBody @Validated LoginDTO body, BindingResult validationResult) {
        if (validationResult.hasErrors()) {
            List<String> errorsList = validationResult.getFieldErrors().stream()
                    .map(fieldError -> fieldError.getDefaultMessage())
                    .toList();
            throw new ValidationException(errorsList);
        }
        return new LoginResponseDTO(authService.checkCredentialsAndGenerateToken(body));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Void> forgotPassword(@RequestBody @Valid ForgotPasswordDTO body) {
        userService.generateAndSendResetCode(body.email());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@RequestBody @Valid ResetPasswordDTO body) {
        userService.resetPassword(body.email(), body.code(), body.newPassword());
        return ResponseEntity.ok().build();
    }

}
