package andreasaderi.capstone.requestDTOs;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserUpdateByAdminDTO(
        @Size(message = "Username must be at least 3 characters long")
        String username,
        @Email
        String email,
        @Size(message = "Password must be at least 8 characters long")
        @Pattern(regexp = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[a-zA-Z]).{8,}$", message = "Password must contain at least 8 characters, one uppercase letter and a number")
        String password,
        @Size(message = "First name must be at least 3 characters long")
        String firstName,
        @Size(message = "Last name must be at least 3 characters long")
        String lastName
) {
}
