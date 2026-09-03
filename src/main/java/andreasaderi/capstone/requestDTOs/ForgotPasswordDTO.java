package andreasaderi.capstone.requestDTOs;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ForgotPasswordDTO(@NotBlank @Email(message = "Insert a correct email") String email) {
}
