package andreasaderi.capstone.requestDTOs;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record LoginDTO(
        @NotBlank(message = "Email can't be blank")
        @Email
        String email,
        @NotBlank(message = "Password can't be blank")
        @Pattern(regexp = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[a-zA-Z]).{8,}$", message = "Password must contain at least 8 characters, one uppercase letter and a number")
        String password
) {
}
