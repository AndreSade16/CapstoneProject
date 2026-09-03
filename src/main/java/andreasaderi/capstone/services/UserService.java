package andreasaderi.capstone.services;

import andreasaderi.capstone.entities.PasswordResetToken;
import andreasaderi.capstone.entities.Role;
import andreasaderi.capstone.entities.User;
import andreasaderi.capstone.exceptions.*;
import andreasaderi.capstone.repositories.UserRepository;
import andreasaderi.capstone.requestDTOs.*;
import andreasaderi.capstone.specifications.UserSpecification;
import andreasaderi.capstone.tools.EmailSender;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder bcrypt;
    private final EmailSender emailSender;
    private final UserSpecification userSpecification;
    private final CloudinaryService cloudinaryService;
    private final PasswordResetTokenService passwordResetTokenService;

    public UserService(UserRepository userRepository, PasswordEncoder bcrypt, EmailSender emailSender, UserSpecification userSpecification, CloudinaryService cloudinaryService, PasswordResetTokenService passwordResetTokenService) {
        this.userRepository = userRepository;
        this.cloudinaryService = cloudinaryService;
        this.bcrypt = bcrypt;
        this.emailSender = emailSender;
        this.userSpecification = userSpecification;
        this.passwordResetTokenService = passwordResetTokenService;
    }

    public User register(UserDTO body, MultipartFile profileImage) {
        if (userRepository.existsByEmail(body.email()))
            throw new RecordAlreadyExistsException("Email '" + body.email() + "' is already in use");
        if (userRepository.existsByUsername(body.username()))
            throw new RecordAlreadyExistsException("Username '" + body.username() + "' is already in use");

        String imageUrl;

        if (profileImage == null || profileImage.isEmpty()) {

            imageUrl = "https://ui-avatars.com/api/?name=" + body.firstName() + "+" + body.lastName();

        } else {

            imageUrl = cloudinaryService.uploadValidatedImageAndGetUrl(profileImage);
        }

        User newUser = userRepository.save(new User(body.username(), body.email(), bcrypt.encode(body.password()), body.firstName(), body.lastName(), imageUrl));

        emailSender.sendCustomRegistrationEmail(newUser);

        return newUser;

    }

    public User findByEmail(@NotBlank(message = "Email can't be blank") @Email String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("User with email '" + email + "' not found"));
    }

    public User findById(UUID userId) {
        return userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User with id '" + userId + "' not found"));
    }

    public Page<User> findAll(int page, int size, String sortBy, Sort.Direction direction, @Valid UserFiltersDTO filters) {
        if (size <= 0) size = 10;
        if (size > 20) size = 20;
        if (page < 0) page = 0;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Specification<User> spec = userSpecification.specificationUserBuilder(filters);


        return userRepository.findAll(spec, pageable);
    }

    public User updateOwnData(User authenticatedUser, UserUpdateDTO body) {
        if (body.email() != null && !body.email().equalsIgnoreCase(authenticatedUser.getEmail()) && userRepository.existsByEmail(body.email())) {
            throw new RecordAlreadyExistsException("Email '" + body.email() + "' is already in use");
        }
        if (body.username() != null && !body.username().equalsIgnoreCase(authenticatedUser.getUsername()) && userRepository.existsByUsername(body.username())) {
            throw new RecordAlreadyExistsException("Username '" + body.username() + "' is already in use");
        }
        if (body.username() != null) authenticatedUser.setUsername(body.username());
        if (body.email() != null) authenticatedUser.setEmail(body.email());
        if (body.firstName() != null) authenticatedUser.setFirstName(body.firstName());
        if (body.lastName() != null) authenticatedUser.setLastName(body.lastName());

        return userRepository.save(authenticatedUser);
    }

    public User adminUpdateUser(UUID userId, UserUpdateByAdminDTO body, MultipartFile avatar) {

        User user = findById(userId);

        if (body.email() != null && !body.email().equalsIgnoreCase(user.getEmail()) && userRepository.existsByEmail(body.email())) {
            throw new RecordAlreadyExistsException("Email '" + body.email() + "' is already in use");
        }
        if (body.username() != null && !body.username().equalsIgnoreCase(user.getUsername()) && userRepository.existsByUsername(body.username())) {
            throw new RecordAlreadyExistsException("Username '" + body.username() + "' is already in use");
        }

        if (body.username() != null) user.setUsername(body.username());
        if (body.email() != null) user.setEmail(body.email());
        if (body.password() != null) user.setPassword(bcrypt.encode(body.password()));
        if (body.firstName() != null) user.setFirstName(body.firstName());
        if (body.lastName() != null) user.setLastName(body.lastName());
        if (avatar != null && !avatar.isEmpty())
            user.setAvatar(cloudinaryService.uploadValidatedImageAndGetUrl(avatar));

        return userRepository.save(user);
    }

    public User update(User user) {
        return userRepository.save(user);
    }

    public User updateOwnAvatar(User user, MultipartFile profileImage) {

        if (profileImage.isEmpty()) throw new FileNotAllowedException("New profile image can't be empty");

        user.setAvatar(cloudinaryService.uploadValidatedImageAndGetUrl(profileImage));

        return userRepository.save(user);
    }

    public void deleteOwnProfile(User user, LoginDTO body) {
        if (!bcrypt.matches(body.password(), user.getPassword()) || !user.getEmail().equals(body.email()))
            throw new UnauthorizedException("Wrong credentials");
        userRepository.delete(user);
    }

    public void deleteProfileById(UUID userId, User activeUser) {
        User user = findById(userId);
        if (activeUser.getUserId().equals(userId))
            throw new ConflictException("To delete your own profile, visit your profile page");
        if (user.getRole().equals(Role.ADMIN))
            throw new UnauthorizedException("You can't delete another ADMIN profile");

        userRepository.delete(user);
    }

    public void generateAndSendResetCode(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isEmpty()) {
            return;
        }

        User user = userOpt.get();

        String code = generateNumericCode(6);

        PasswordResetToken token = new PasswordResetToken();
        token.setUser(user);
        token.setCodeHash(bcrypt.encode(code));
        token.setExpiryDate(LocalDateTime.now().plusMinutes(15));
        token.setUsed(false);

        passwordResetTokenService.deleteByUser(user);
        passwordResetTokenService.save(token);

        emailSender.sendPasswordResetEmail(user, code);
    }

    private String generateNumericCode(int length) {
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    public void resetPassword(String email, String code, String newPassword) {
        User user = findByEmail(email);

        PasswordResetToken token = passwordResetTokenService.findByUser(user);

        if (token.isUsed() || token.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new UnauthorizedException("Codice expired or already utilized");
        }

        if (!bcrypt.matches(code, token.getCodeHash())) {
            throw new UnauthorizedException("Invalid code");
        }

        user.setPassword(bcrypt.encode(newPassword));
        userRepository.save(user);

        token.setUsed(true);
        passwordResetTokenService.save(token);
    }
}
