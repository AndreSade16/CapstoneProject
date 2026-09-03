package andreasaderi.capstone.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "password_reset_tokens")
@Getter
@Setter
@NoArgsConstructor
public class PasswordResetToken {
    @Id
    @GeneratedValue
    private UUID passwordResetTokenId;

    @OneToOne
    private User user;

    private String codeHash;

    private LocalDateTime expiryDate;

    private boolean used = false;
}
