package andreasaderi.capstone.controllers;

import andreasaderi.capstone.entities.PantryItem;
import andreasaderi.capstone.entities.User;
import andreasaderi.capstone.exceptions.ValidationException;
import andreasaderi.capstone.requestDTOs.PantryItemDTO;
import andreasaderi.capstone.requestDTOs.PantryItemFiltersDTO;
import andreasaderi.capstone.requestDTOs.PantryItemUpdateDTO;
import andreasaderi.capstone.responseDTOs.PantryItemCreatedDTO;
import andreasaderi.capstone.services.PantryItemService;
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
@RequestMapping("/pantry-items")
public class PantryItemController {

    private final PantryItemService pantryItemService;

    public PantryItemController(PantryItemService pantryItemService) {
        this.pantryItemService = pantryItemService;
    }

    @PostMapping
    public PantryItemCreatedDTO createPantryItem(@AuthenticationPrincipal User user, @RequestBody @Validated PantryItemDTO body, BindingResult validationResult) {
        if (validationResult.hasErrors()) {
            List<String> errorsList = validationResult.getFieldErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toList();
            throw new ValidationException(errorsList);
        }

        PantryItem pantryItem = pantryItemService.save(body, user);

        return new PantryItemCreatedDTO(pantryItem.getPantryItemId());
    }

    @GetMapping("/me")
    public Page<PantryItem> findAll(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "expirationDate") String sortBy, @RequestParam(defaultValue = "DESC") Sort.Direction direction, @Valid @ModelAttribute PantryItemFiltersDTO filters) {
        return pantryItemService.findAll(page, size, sortBy, direction, filters);
    }

    @PatchMapping("/me/{pantryItemId}")
    public PantryItem updateOwnItem(@PathVariable UUID pantryItemId, @AuthenticationPrincipal User user, @RequestBody @Validated PantryItemUpdateDTO body, BindingResult validationResult) {
        if (validationResult.hasErrors()) {
            List<String> errorsList = validationResult.getFieldErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toList();
            throw new ValidationException(errorsList);
        }

        return pantryItemService.updateOwnPantryItem(pantryItemId, body, user);
    }

    @DeleteMapping("/me/{pantryItemId}")
    public void deleteOwnItem(@PathVariable UUID pantryItemId, @AuthenticationPrincipal User user) {
        pantryItemService.deleteOwnItem(pantryItemId, user);
    }
}
