package andreasaderi.capstone.responseDTOs;

import java.util.List;

public record DashboardDTO(
        List<PantryItemResponseDTO> expiringItems,

        ShoppingListResponseDTO activeShoppingList,

        List<RecipeCardDTO> suggestedRecipes
) {
}
