package andreasaderi.capstone.services;

import andreasaderi.capstone.entities.User;
import andreasaderi.capstone.exceptions.FileNotAllowedException;
import andreasaderi.capstone.exceptions.NotFoundException;
import andreasaderi.capstone.exceptions.RecordAlreadyExistsException;
import andreasaderi.capstone.repositories.UserRepository;
import andreasaderi.capstone.requestDTOs.UserDTO;
import andreasaderi.capstone.requestDTOs.UserFiltersDTO;
import andreasaderi.capstone.specifications.UserSpecification;
import andreasaderi.capstone.tools.EmailSender;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
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

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final Cloudinary fileUploader;
    private final PasswordEncoder bcrypt;
    private final EmailSender mailgun;
    private final UserSpecification userSpecification;

    public UserService(UserRepository userRepository, Cloudinary fileUploader, PasswordEncoder bcrypt, EmailSender mailgun, UserSpecification userSpecification) {
        this.userRepository = userRepository;
        this.fileUploader = fileUploader;
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

        if (profileImage.isEmpty()) {

            imageUrl = "https://ui-avatars.com/api/?name=" + body.firstName() + "+" + body.lastName();

        } else {

            if (profileImage.getSize() >= 10485760)
                throw new FileNotAllowedException("File size can't be more than 10MB");
            if (!(Objects.equals(profileImage.getContentType(), "image/jpeg") || Objects.equals(profileImage.getContentType(), "image/gif") || Objects.equals(profileImage.getContentType(), "image/png") || Objects.equals(profileImage.getContentType(), "image/webp")))
                throw new FileNotAllowedException("File must be an img");


            try {
                Map result = fileUploader.uploader().upload(profileImage.getBytes(), ObjectUtils.emptyMap());
                imageUrl = (String) result.get("secure_url");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

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
}
