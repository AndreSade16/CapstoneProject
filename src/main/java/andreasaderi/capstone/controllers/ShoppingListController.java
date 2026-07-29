package andreasaderi.capstone.controllers;

import andreasaderi.capstone.entities.ShoppingList;
import andreasaderi.capstone.entities.User;
import andreasaderi.capstone.responseDTOs.ShoppingListCreatedDTO;
import andreasaderi.capstone.services.ShoppingListService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

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

    
}
