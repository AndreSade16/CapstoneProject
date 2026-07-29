package andreasaderi.capstone.services;

import andreasaderi.capstone.entities.IngredientDefinition;
import andreasaderi.capstone.entities.PantryItem;
import andreasaderi.capstone.entities.ShoppingListItem;
import andreasaderi.capstone.entities.User;
import andreasaderi.capstone.repositories.PantryItemRepository;
import andreasaderi.capstone.requestDTOs.PantryItemDTO;
import andreasaderi.capstone.requestDTOs.PurchasedItemDTO;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class PantryItemService {

    private final PantryItemRepository pantryItemRepository;
    private final IngredientDefinitionService ingredientDefinitionService;
    private final ShoppingListItemService shoppingListItemService;

    public PantryItemService(PantryItemRepository pantryItemRepository, IngredientDefinitionService ingredientDefinitionService, ShoppingListItemService shoppingListItemService) {
        this.pantryItemRepository = pantryItemRepository;
        this.ingredientDefinitionService = ingredientDefinitionService;
        this.shoppingListItemService = shoppingListItemService;
    }

    public PantryItem save(PantryItemDTO body, User user) {

        IngredientDefinition ingredientDefinition = ingredientDefinitionService.findById(body.ingredientDefinitionId());

        return pantryItemRepository.save(new PantryItem(user, ingredientDefinition, body.quantity(), body.purchaseDate(), body.expirationDate(), body.storageLocation()));
    }


    public void createFromPurchasedItem(ShoppingListItem item, PurchasedItemDTO purchasedItem, User authenticatedUser) {
        PantryItemDTO body = new PantryItemDTO(item.getIngredientDefinition().getIngredientDefinitionId(), purchasedItem.purchasedQuantity(), LocalDate.now(), purchasedItem.expirationDate(), purchasedItem.storageLocation());
        this.save(body, authenticatedUser);
    }
}
