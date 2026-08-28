package andreasaderi.capstone.controllers;

import andreasaderi.capstone.entities.ShoppingList;
import andreasaderi.capstone.entities.User;
import andreasaderi.capstone.requestDTOs.CompleteShoppingListDTO;
import andreasaderi.capstone.responseDTOs.ShoppingListCompletedDTO;
import andreasaderi.capstone.responseDTOs.ShoppingListCreatedDTO;
import andreasaderi.capstone.responseDTOs.ShoppingListResponseDTO;
import andreasaderi.capstone.services.ShoppingListService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/shopping-lists")
public class ShoppingListController {

    private final ShoppingListService shoppingListService;


    public ShoppingListController(ShoppingListService shoppingListService) {
        this.shoppingListService = shoppingListService;
    }

    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ShoppingListCreatedDTO createShoppingList(@AuthenticationPrincipal User authenticatedUser) {
        ShoppingList saved = shoppingListService.save(authenticatedUser);
        return new ShoppingListCreatedDTO(saved.getShoppingListId());
    }

    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @PostMapping("/{id}/complete")
    public ShoppingListCompletedDTO completeShoppingList(
            @PathVariable UUID id,
            @Valid @RequestBody CompleteShoppingListDTO body,
            @AuthenticationPrincipal User authenticatedUser
    ) {
        ShoppingList saved = shoppingListService.completeShoppingList(id, body, authenticatedUser);
        return new ShoppingListCompletedDTO(saved.getShoppingListId(), saved.getShoppingListStatus());
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @GetMapping("/{ShoppingListId}")
    public ShoppingList findById(@PathVariable UUID ShoppingListId) {
        return shoppingListService.findById(ShoppingListId);
    }

    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @GetMapping("/me")
    public ShoppingListResponseDTO findActiveShoppingList(@AuthenticationPrincipal User user) {
        ShoppingList shoppingList = shoppingListService.findByUserAndActive(user);
        return new ShoppingListResponseDTO(shoppingList.getShoppingListId(), shoppingList.getCreatedAt(), shoppingList.getUpdatedAt(), shoppingList.getShoppingListStatus(), shoppingList.getItems().stream().toList());
    }

    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @DeleteMapping("/me/{shoppingListId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOwnShoppingList(@PathVariable UUID shoppingListId, @AuthenticationPrincipal User user) {
        shoppingListService.deleteOwnShoppingList(shoppingListId, user);
    }
}
