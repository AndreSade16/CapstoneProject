package andreasaderi.capstone.services;

import andreasaderi.capstone.entities.PasswordResetToken;
import andreasaderi.capstone.entities.User;
import andreasaderi.capstone.exceptions.NotFoundException;
import andreasaderi.capstone.repositories.PasswordResetTokenRepository;
import org.springframework.stereotype.Service;

@Service
public class PasswordResetTokenService {

    public final PasswordResetTokenRepository passwordResetTokenRepository;

    public PasswordResetTokenService(PasswordResetTokenRepository passwordResetTokenRepository) {
        this.passwordResetTokenRepository = passwordResetTokenRepository;
    }


    public void deleteByUser(User user) {
        passwordResetTokenRepository.deleteByUser(user);
        passwordResetTokenRepository.flush();
    }

    public void save(PasswordResetToken token) {
        passwordResetTokenRepository.save(token);
    }

    public PasswordResetToken findByUser(User user) {
        return passwordResetTokenRepository.findByUser(user).orElseThrow(() -> new NotFoundException("Invalid Code"));
    }
}
