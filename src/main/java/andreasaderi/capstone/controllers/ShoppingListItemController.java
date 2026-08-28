package andreasaderi.capstone.controllers;

import andreasaderi.capstone.entities.ShoppingList;
import andreasaderi.capstone.entities.ShoppingListItem;
import andreasaderi.capstone.entities.User;
import andreasaderi.capstone.exceptions.ValidationException;
import andreasaderi.capstone.requestDTOs.ShoppingListItemDTO;
import andreasaderi.capstone.responseDTOs.ShoppingListItemCreatedDTO;
import andreasaderi.capstone.services.ShoppingListItemService;
import andreasaderi.capstone.services.ShoppingListService;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/shopping-lists")
public class ShoppingListItemController {

    private final ShoppingListItemService shoppingListItemService;
    private final ShoppingListService shoppingListService;


    public ShoppingListItemController(ShoppingListItemService shoppingListItemService, ShoppingListService shoppingListService) {
        this.shoppingListItemService = shoppingListItemService;
        this.shoppingListService = shoppingListService;
    }


    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @PostMapping("me/{shoppingListId}/items")
    @ResponseStatus(HttpStatus.CREATED)
    public ShoppingListItemCreatedDTO createShoppingListItem(@PathVariable UUID shoppingListId, @RequestBody @Validated ShoppingListItemDTO body, @AuthenticationPrincipal User authenticatedUser, BindingResult validationResult) {
        if (validationResult.hasErrors()) {
            List<String> errorsList = validationResult.getFieldErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toList();
            throw new ValidationException(errorsList);
        }

        ShoppingList shoppingList = shoppingListService.findByIdAndUser(shoppingListId, authenticatedUser);
        shoppingList.setUpdatedAt(LocalDate.now());
        shoppingListService.saveShoppingListUpdates(shoppingList);
        ShoppingListItem saved = shoppingListItemService.save(shoppingList, body);

        return new ShoppingListItemCreatedDTO(saved.getShoppingListItemId());
    }

    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @GetMapping("me/{shoppingListId}/items")
    public List<ShoppingListItem> findItemsByShoppingList(@PathVariable UUID shoppingListId, @AuthenticationPrincipal User authenticatedUser) {
        ShoppingList shoppingList = shoppingListService.findByIdAndUser(shoppingListId, authenticatedUser);
        return shoppingListItemService.findByShoppingList(shoppingList);
    }

    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @PatchMapping("/{shoppingListId}/items/{shoppingListItemId}")
    public ShoppingListItem updateById(@AuthenticationPrincipal User authenticatedUser, @PathVariable UUID shoppingListId, @PathVariable UUID shoppingListItemId, @RequestBody @Validated ShoppingListItemDTO body, BindingResult validationResult) {
        if (validationResult.hasErrors()) {
            List<String> errorsList = validationResult.getFieldErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toList();
            throw new ValidationException(errorsList);
        }

        ShoppingList shoppingList = shoppingListService.findByIdAndUser(shoppingListId, authenticatedUser);
        shoppingList.setUpdatedAt(LocalDate.now());
        shoppingListService.saveShoppingListUpdates(shoppingList);

        return shoppingListItemService.updateById(authenticatedUser, shoppingList, shoppingListItemId, body);
    }

    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @DeleteMapping("me/{shoppingListId}/items/{shoppingListItemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOwnItemById(@PathVariable UUID shoppingListId, @PathVariable UUID shoppingListItemId, @AuthenticationPrincipal User user) {
        ShoppingList shoppingList = shoppingListService.findByIdAndUser(shoppingListId, user);
        shoppingListItemService.delete(shoppingList, shoppingListItemId);
    }
}
