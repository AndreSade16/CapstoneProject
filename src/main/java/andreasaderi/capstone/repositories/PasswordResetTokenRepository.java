package andreasaderi.capstone.repositories;

import andreasaderi.capstone.entities.PasswordResetToken;
import andreasaderi.capstone.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, UUID> {
    @Modifying
    void deleteByUser(User user);

    Optional<PasswordResetToken> findByUser(User user);
}
