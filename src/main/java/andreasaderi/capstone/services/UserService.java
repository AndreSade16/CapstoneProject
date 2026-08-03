package andreasaderi.capstone.services;

import andreasaderi.capstone.entities.User;
import andreasaderi.capstone.exceptions.NotFoundException;
import andreasaderi.capstone.exceptions.RecordAlreadyExistsException;
import andreasaderi.capstone.repositories.UserRepository;
import andreasaderi.capstone.requestDTOs.UserDTO;
import andreasaderi.capstone.requestDTOs.UserFiltersDTO;
import andreasaderi.capstone.requestDTOs.UserUpdateByAdminDTO;
import andreasaderi.capstone.requestDTOs.UserUpdateDTO;
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

import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder bcrypt;
    private final EmailSender mailgun;
    private final UserSpecification userSpecification;
    private final CloudinaryService cloudinaryService;

    public UserService(UserRepository userRepository, PasswordEncoder bcrypt, EmailSender mailgun, UserSpecification userSpecification, CloudinaryService cloudinaryService) {
        this.userRepository = userRepository;
        this.cloudinaryService = cloudinaryService;
        this.bcrypt = bcrypt;
        this.mailgun = mailgun;
        this.userSpecification = userSpecification;
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

        mailgun.sendCustomRegistrationEmail(newUser);

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

    public User adminUpdateUser(UUID userId, UserUpdateByAdminDTO body) {

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

        return userRepository.save(user);
    }

    public User update(User user) {
        return userRepository.save(user);
    }
}
