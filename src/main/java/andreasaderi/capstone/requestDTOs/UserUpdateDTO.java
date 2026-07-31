package andreasaderi.capstone.requestDTOs;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UserUpdateDTO(
        @Size(min = 3, message = "Username field must be at least 3 characters long")
        String username,
        @Email(message = "Email field must be a valid email")
        String email,
        @Size(min = 3, message = "First name field must be at least 3 characters long")
        String firstName,
        @Size(min = 3, message = "Last name field must be at least 3 characters long")
        String lastName
) {
}
