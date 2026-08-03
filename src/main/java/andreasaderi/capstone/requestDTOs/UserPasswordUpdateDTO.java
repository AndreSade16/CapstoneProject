package andreasaderi.capstone.requestDTOs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserPasswordUpdateDTO(
        @NotBlank(message = "New password can't be blank")
        @Size(message = "New password must be at least 8 characters long")
        @Pattern(regexp = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[a-zA-Z]).{8,}$", message = "Password must contain at least 8 characters, one uppercase letter and a number")
        String newPassword,
        @NotBlank(message = "Repeat new password can't be blank")
        @Size(message = "Repeat new password must be at least 8 characters long")
        @Pattern(regexp = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[a-zA-Z]).{8,}$", message = "Repeat new password must contain at least 8 characters, one uppercase letter and a number")
        String repeatNewPassword,
        @NotBlank(message = "Old password can't be blank")
        @Size(message = "Old password must be at least 8 characters long")
        @Pattern(regexp = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[a-zA-Z]).{8,}$", message = "Old password must contain at least 8 characters, one uppercase letter and a number")
        String oldPassword
) {
}
