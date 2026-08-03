package andreasaderi.capstone.services;

import andreasaderi.capstone.entities.ShoppingList;
import andreasaderi.capstone.entities.ShoppingListItem;
import andreasaderi.capstone.entities.ShoppingListStatus;
import andreasaderi.capstone.entities.User;
import andreasaderi.capstone.exceptions.ConflictException;
import andreasaderi.capstone.exceptions.NotFoundException;
import andreasaderi.capstone.exceptions.UnauthorizedException;
import andreasaderi.capstone.repositories.ShoppingListRepository;
import andreasaderi.capstone.requestDTOs.CompleteShoppingListDTO;
import andreasaderi.capstone.requestDTOs.PurchasedItemDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ShoppingListService {

    private final ShoppingListRepository shoppingListRepository;
    private final ShoppingListItemService shoppingListItemService;
    private final PantryItemService pantryItemService;


    public ShoppingListService(ShoppingListRepository shoppingListRepository, ShoppingListItemService shoppingListItemService, PantryItemService pantryItemService) {
        this.shoppingListRepository = shoppingListRepository;
        this.shoppingListItemService = shoppingListItemService;
        this.pantryItemService = pantryItemService;
    }


    public ShoppingList save(User authenticatedUser) {
        if (shoppingListRepository.existsByUserAndShoppingListStatus(authenticatedUser, ShoppingListStatus.ACTIVE))
            throw new ConflictException("You already have an active Shopping List. Complete it or edit that one");
        return shoppingListRepository.save(new ShoppingList(authenticatedUser));
    }

    public void saveShoppingListUpdates(ShoppingList shoppingList) {
        shoppingListRepository.save(shoppingList);
    }

    public ShoppingList findById(UUID shoppingListId) {
        return shoppingListRepository.findById(shoppingListId).orElseThrow(() -> new NotFoundException("Shopping list with ID '" + shoppingListId + "' not found"));
    }

    public ShoppingList findByIdAndUser(UUID shoppingListId, User user) {
        ShoppingList found = findById(shoppingListId);
        if (!found.getUser().getUserId().equals(user.getUserId()))
            throw new ConflictException("This shopping list doesn't belong to this user");

        return found;
    }

    //    After some research, I decided to add this "rollbackFor", so that even if in the future I'd add some code that throws an Exception that's not a Runtime one, I'd be covered anyway.
    @Transactional(rollbackFor = Exception.class)
    public ShoppingList completeShoppingList(UUID id, CompleteShoppingListDTO body, User authenticatedUser) {
        ShoppingList currentShoppingList = findById(id);
        if (!currentShoppingList.getUser().getUserId().equals(authenticatedUser.getUserId()))
            throw new UnauthorizedException("You don't have permissions to edit this shopping list");
        if (currentShoppingList.getShoppingListStatus() != ShoppingListStatus.ACTIVE)
            throw new ConflictException("This shopping list has already been completed");
        for (PurchasedItemDTO purchasedItem : body.items()) {
            ShoppingListItem item = shoppingListItemService.findByIdAndShoppingList(purchasedItem.shoppingListItemId(), currentShoppingList);
            pantryItemService.createFromPurchasedItem(
                    item,
                    purchasedItem,
                    authenticatedUser
            );
            shoppingListItemService.setAsBought(item, purchasedItem.purchasedQuantity());
        }
        currentShoppingList.setShoppingListStatus(ShoppingListStatus.COMPLETED);
        return shoppingListRepository.save(currentShoppingList);
    }

    public ShoppingList findByUserAndActive(User user) {
        return shoppingListRepository.findByUserAndShoppingListStatus(user, ShoppingListStatus.ACTIVE).orElseThrow(() -> new NotFoundException("No active shopping list found for user " + user.getUsername()));
    }

    public void deleteOwnShoppingList(UUID shoppingListId, User user) {
        ShoppingList shoppingList = findByIdAndUser(shoppingListId, user);

        shoppingListRepository.delete(shoppingList);
    }
}
