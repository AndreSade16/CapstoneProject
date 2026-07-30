package andreasaderi.capstone.requestDTOs;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

public record ShoppingListItemDTO(
        @NotNull(message = "Ingredient definition Id can't be null")
        UUID ingredientDefinitionId,
        @Positive(message = "Suggested quantity should be a positive number")
        Double suggestedQuantity
) {
}
