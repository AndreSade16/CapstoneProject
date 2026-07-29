package andreasaderi.capstone.services;

import andreasaderi.capstone.entities.ShoppingList;
import andreasaderi.capstone.entities.ShoppingListItem;
import andreasaderi.capstone.entities.Unit;
import andreasaderi.capstone.exceptions.ConflictException;
import andreasaderi.capstone.exceptions.NotFoundException;
import andreasaderi.capstone.repositories.ShoppingListItemRepository;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ShoppingListItemService {

    private final ShoppingListItemRepository shoppingListItemRepository;


    public ShoppingListItemService(ShoppingListItemRepository shoppingListItemRepository) {
        this.shoppingListItemRepository = shoppingListItemRepository;
    }

    public ShoppingListItem save() {
//        TODO: HERE! controllare che non ci sia già una item con questo nome dentro la lista. In tal caso, bisogna sommare le quantità, non aggiungere una nuova item.
    }

    public ShoppingListItem findById(UUID shoppingListItemId) {
        return shoppingListItemRepository.findById(shoppingListItemId).orElseThrow(() -> new NotFoundException("Shopping list item with ID '" + shoppingListItemId + "' not found"));
    }

    public void setAsBought(ShoppingListItem item, @NotNull(message = "Purchased unit can't be null") Unit unit, @NotNull @Positive(message = "Purchased quantity must be a positive number") Double quantity) {
        item.setPurchasedUnit(unit);
        item.setPurchasedQuantity(quantity);
        shoppingListItemRepository.save(item);
    }

    public ShoppingListItem findByIdAndShoppingList(UUID itemId, ShoppingList shoppingList) {
        ShoppingListItem item = findById(itemId);

        if (!item.getShoppingList().getShoppingListId()
                .equals(shoppingList.getShoppingListId())) {
            throw new ConflictException("This item does not belong to this shopping list");
        }

        return item;
    }
}
