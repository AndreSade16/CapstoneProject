package andreasaderi.capstone.requestDTOs;

import andreasaderi.capstone.entities.RecipeCost;
import andreasaderi.capstone.entities.RecipeDifficulty;
import jakarta.validation.constraints.*;

public record RecipeDTO(
        @NotBlank(message = "Name field can't be blank")
        @Size(min = 3, message = "Name field must be at least 3 characters long")
        String name,
        @NotBlank(message = "Description field can't be blank")
        @Size(min = 3, message = "Description field must be at least 3 characters long")
        String description,
        @Positive(message = "Preparation time must be a positive number")
        @NotNull(message = "Preparation time field can't be null")
        double preparationTime,
        @PositiveOrZero(message = "Cooking time must be a positive number or zero")
        @NotNull(message = "Cooking time field can't be null")
        double cookingTime,
        @NotNull(message = "Difficulty field can't be null")
        RecipeDifficulty difficulty,
        @NotNull(message = "Cost field can't be null")
        RecipeCost cost,
        @NotBlank(message = "Procedure field can't be blank")
        @Size(message = "Procedure field must be at least 10 characters long")
        String procedure
) {
}
