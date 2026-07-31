package andreasaderi.capstone.controllers;

import andreasaderi.capstone.entities.User;
import andreasaderi.capstone.exceptions.ValidationException;
import andreasaderi.capstone.requestDTOs.UserFiltersDTO;
import andreasaderi.capstone.requestDTOs.UserUpdateDTO;
import andreasaderi.capstone.services.UserService;
import jakarta.validation.Valid;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;


    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public Page<User> findAll(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "username") String sortBy, @RequestParam(defaultValue = "DESC") Sort.Direction direction, @Valid @ModelAttribute UserFiltersDTO filters) {
        return userService.findAll(page, size, sortBy, direction, filters);
    }

    @GetMapping("/{UserId}")
    public User findById(@PathVariable UUID UserId) {
        return userService.findById(UserId);
    }

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
}
