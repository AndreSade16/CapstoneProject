package andreasaderi.capstone.requestDTOs;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserDTO(
        @NotBlank(message = "Username can't be blank")
        @Size(message = "Username must be at least 3 characters long")
        String username,
        @NotBlank(message = "Email can't be blank")
        @Email
        String email,
        @NotBlank(message = "Password can't be blank")
        @Size(message = "Password must be at least 8 characters long")
        @Pattern(regexp = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[a-zA-Z]).{8,}$", message = "Password must contain at least 8 characters, one uppercase letter and a number")
        String password,
        @NotBlank(message = "First name can't be blank")
        @Size(message = "First name must be at least 3 characters long")
        String firstName,
        @NotBlank(message = "Last name can't be blank")
        @Size(message = "Last name must be at least 3 characters long")
        String lastName
) {
}
