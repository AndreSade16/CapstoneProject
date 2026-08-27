package andreasaderi.capstone.responseDTOs;

import java.util.List;

public record RecipeIngredientsToSlDTO(
        List<ShoppingListItemCreatedDTO> shoppingListItems
) {
}
