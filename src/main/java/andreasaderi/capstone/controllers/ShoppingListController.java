package andreasaderi.capstone.controllers;

import andreasaderi.capstone.entities.ShoppingList;
import andreasaderi.capstone.entities.User;
import andreasaderi.capstone.requestDTOs.CompleteShoppingListDTO;
import andreasaderi.capstone.responseDTOs.ShoppingListCompletedDTO;
import andreasaderi.capstone.responseDTOs.ShoppingListCreatedDTO;
import andreasaderi.capstone.services.ShoppingListService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
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

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ShoppingListCreatedDTO createShoppingList(@AuthenticationPrincipal User authenticatedUser) {
        ShoppingList saved = shoppingListService.save(authenticatedUser);
        return new ShoppingListCreatedDTO(saved.getShoppingListId());
    }

    @PostMapping("/{id}/complete")
    public ShoppingListCompletedDTO completeShoppingList(
            @PathVariable UUID id,
            @Valid @RequestBody CompleteShoppingListDTO body,
            @AuthenticationPrincipal User authenticatedUser
    ) {
        ShoppingList saved = shoppingListService.completeShoppingList(id, body, authenticatedUser);
        return new ShoppingListCompletedDTO(saved.getShoppingListId(), saved.getShoppingListStatus());
    }
}
