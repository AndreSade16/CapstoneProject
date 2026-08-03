package andreasaderi.capstone.repositories;

import andreasaderi.capstone.entities.IngredientDefinition;
import andreasaderi.capstone.entities.ShoppingList;
import andreasaderi.capstone.entities.ShoppingListItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ShoppingListItemRepository extends JpaRepository<ShoppingListItem, UUID> {
    Optional<ShoppingListItem> findByShoppingListAndIngredientDefinition(ShoppingList shoppingList, IngredientDefinition ingredientDefinition);

    List<ShoppingListItem> findByShoppingList(ShoppingList shoppingList);
}
