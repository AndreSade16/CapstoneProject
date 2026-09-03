package andreasaderi.capstone.requestDTOs;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ResetPasswordDTO(@NotBlank @Email String email, @NotBlank @Size(max = 6, min = 0) String code,
                               @Pattern(regexp = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[a-zA-Z]).{8,}$", message = "Password must contain at least 8 characters, one uppercase letter and a number")
                               String newPassword) {
}
