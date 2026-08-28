package andreasaderi.capstone.controllers;

import andreasaderi.capstone.entities.User;
import andreasaderi.capstone.exceptions.ValidationException;
import andreasaderi.capstone.requestDTOs.*;
import andreasaderi.capstone.services.AuthService;
import andreasaderi.capstone.services.UserService;
import jakarta.validation.Valid;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final AuthService authService;


    public UserController(UserService userService, AuthService authService) {
        this.userService = userService;
        this.authService = authService;
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @GetMapping
    public Page<User> findAll(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "username") String sortBy, @RequestParam(defaultValue = "DESC") Sort.Direction direction, @Valid @ModelAttribute UserFiltersDTO filters) {
        return userService.findAll(page, size, sortBy, direction, filters);
    }

    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @GetMapping("/me")
    public User findOwnProfile(@AuthenticationPrincipal User user) {
        return userService.findById(user.getUserId());
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @GetMapping("/{UserId}")
    public User findById(@PathVariable UUID UserId) {
        return userService.findById(UserId);
    }

    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @PatchMapping("/me")
    public User updateOwnData(@AuthenticationPrincipal User authenticatedUser, @RequestBody @Validated UserUpdateDTO body, BindingResult validationResult) {
        if (validationResult.hasErrors()) {
            List<String> errorsList = validationResult.getFieldErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toList();
            throw new ValidationException(errorsList);
        }
        return userService.updateOwnData(authenticatedUser, body);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @PatchMapping("/{userId}")
    public User adminUpdateUser(@PathVariable UUID userId, @ModelAttribute @Validated UserUpdateByAdminDTO body, @RequestPart(value = "avatar", required = false) MultipartFile avatar, BindingResult validationResult) {
        if (validationResult.hasErrors()) {
            List<String> errorsList = validationResult.getFieldErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toList();
            throw new ValidationException(errorsList);
        }

        return userService.adminUpdateUser(userId, body, avatar);
    }

    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @PatchMapping("/me/password")
    public User updateOwnPassword(@AuthenticationPrincipal User user, @RequestBody @Validated UserPasswordUpdateDTO body) {
        return authService.updatePassword(user, body);
    }

    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @PatchMapping("/me/avatar")
    public User updateOwnAvatar(@AuthenticationPrincipal User user, @RequestParam("avatar") MultipartFile profileImage) {
        return userService.updateOwnAvatar(user, profileImage);
    }

    @DeleteMapping("/me")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOwnProfile(@AuthenticationPrincipal User user, @RequestBody LoginDTO body) {
        userService.deleteOwnProfile(user, body);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProfileById(@PathVariable UUID userId, @AuthenticationPrincipal User activeUser) {
        userService.deleteProfileById(userId, activeUser);
    }
}
