package andreasaderi.capstone.requestDTOs;

import andreasaderi.capstone.entities.Category;
import andreasaderi.capstone.entities.Season;
import andreasaderi.capstone.entities.StorageLocation;
import andreasaderi.capstone.entities.Unit;
import jakarta.validation.constraints.*;

import java.util.Set;

public record IngredientDefinitionDTO(
        @NotBlank(message = "Ingredient name can't be blank")
        @Size(min = 3, message = "Ingredient name must be at least 3 characters long")
        String name,
        @NotBlank(message = "Ingredient description can't be blank")
        @Size(min = 10, message = "Ingredient description must be at least 10 characters long")
        String description,
        @NotNull(message = "Category field must be filled.")
        Category category,
        @NotNull(message = "Unit type field must be filled.")
        Unit unit,
        @NotNull(message = "Default storage location field must be filled.")
        StorageLocation defaultStorageLocation,
        @Positive(message = "Shelf life days must be a positive number")
        int shelfLifeDays,
        String alternativeUsages,
        @NotNull(message = "Seasonality can't be null")
        @NotEmpty(message = "Seasonality set can't be empty")
        Set<Season> seasonality
) {
}
