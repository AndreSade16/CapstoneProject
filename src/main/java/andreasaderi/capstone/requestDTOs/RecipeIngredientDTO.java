package andreasaderi.capstone.requestDTOs;

import java.util.UUID;

public record RecipeIngredientDTO(
        UUID ingredientDefinitionId,
        double quantityPerPerson
) {
}
