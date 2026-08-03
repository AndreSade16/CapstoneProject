package andreasaderi.capstone.services;

import andreasaderi.capstone.entities.*;
import andreasaderi.capstone.exceptions.ConflictException;
import andreasaderi.capstone.exceptions.NotFoundException;
import andreasaderi.capstone.exceptions.UnauthorizedException;
import andreasaderi.capstone.repositories.ShoppingListItemRepository;
import andreasaderi.capstone.requestDTOs.ShoppingListItemDTO;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ShoppingListItemService {

    private final ShoppingListItemRepository shoppingListItemRepository;
    private final IngredientDefinitionService ingredientDefinitionService;


    public ShoppingListItemService(ShoppingListItemRepository shoppingListItemRepository, IngredientDefinitionService ingredientDefinitionService) {
        this.shoppingListItemRepository = shoppingListItemRepository;
        this.ingredientDefinitionService = ingredientDefinitionService;
    }

    private static ShoppingListItem mergeSuggestedQuantity(
            ShoppingListItem existing,
            Double newQuantity
    ) {

        if (newQuantity == null) {
            return existing;
        }

        if (existing.getSuggestedQuantity() == null) {
            existing.setSuggestedQuantity(newQuantity);
        } else {
            existing.setSuggestedQuantity(
                    existing.getSuggestedQuantity() + newQuantity
            );
        }

        return existing;
    }

    public ShoppingListItem save(ShoppingList shoppingList, ShoppingListItemDTO body) {

        if (shoppingList.getShoppingListStatus().equals(ShoppingListStatus.COMPLETED))
            throw new ConflictException("Shopping list already marked as completed");
        IngredientDefinition ingredientDefinition = ingredientDefinitionService.findById(body.ingredientDefinitionId());

        Optional<ShoppingListItem> shoppingListItem = shoppingListItemRepository.findByShoppingListAndIngredientDefinition(shoppingList, ingredientDefinition);

        if (shoppingListItem.isPresent()) {
            ShoppingListItem alreadyPresentItem = mergeSuggestedQuantity(shoppingListItem.get(), body.suggestedQuantity());

            return shoppingListItemRepository.save(alreadyPresentItem);
        } else {
            return shoppingListItemRepository.save(new ShoppingListItem(shoppingList, ingredientDefinition, body.suggestedQuantity(), ingredientDefinition.getUnit()));
        }

    }

    public ShoppingListItem findById(UUID shoppingListItemId) {
        return shoppingListItemRepository.findById(shoppingListItemId).orElseThrow(() -> new NotFoundException("Shopping list item with ID '" + shoppingListItemId + "' not found"));
    }

    public void setAsBought(ShoppingListItem item, @NotNull @Positive(message = "Purchased quantity must be a positive number") Double quantity) {
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

    public ShoppingListItem updateById(User authenticatedUser, ShoppingList shoppingList, UUID shoppingListItemId, ShoppingListItemDTO body) {
        if (!shoppingList.getUser().getUserId().equals(authenticatedUser.getUserId()))
            throw new UnauthorizedException("You don't have the permissions to edit this item");
        ShoppingListItem shoppingListItem = findByIdAndShoppingList(shoppingListItemId, shoppingList);
        IngredientDefinition newIngredient = ingredientDefinitionService.findById(body.ingredientDefinitionId());
        Optional<ShoppingListItem> sameNameItem = shoppingListItemRepository.findByShoppingListAndIngredientDefinition(shoppingList, newIngredient);

        if (sameNameItem.isPresent() && !sameNameItem.get().getShoppingListItemId().equals(shoppingListItem.getShoppingListItemId())) {
            ShoppingListItem itemAlreadyThere = sameNameItem.get();
            mergeSuggestedQuantity(itemAlreadyThere, body.suggestedQuantity());
            shoppingListItemRepository.delete(shoppingListItem);
            return shoppingListItemRepository.save(itemAlreadyThere);
        } else {
            shoppingListItem.setIngredientDefinition(newIngredient);
            shoppingListItem.setSuggestedQuantity(body.suggestedQuantity());
            return shoppingListItemRepository.save(shoppingListItem);
        }

    }

    public List<ShoppingListItem> findByShoppingList(ShoppingList shoppingList) {
        return shoppingListItemRepository.findByShoppingList(shoppingList);
    }

    public void delete(ShoppingList shoppingList, UUID shoppingListItemId) {
        ShoppingListItem shoppingListItem = findByIdAndShoppingList(shoppingListItemId, shoppingList);

        shoppingListItemRepository.delete(shoppingListItem);
    }
}
