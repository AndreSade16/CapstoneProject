package andreasaderi.capstone.requestDTOs;

import andreasaderi.capstone.entities.StorageLocation;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;
import java.util.UUID;

public record PantryItemDTO(
        @NotNull(message = "Ingredient definition ID can't be blank")
        UUID ingredientDefinitionId,
        @NotNull(message = "Quantity can't be null")
        @Positive(message = "Quantity must be a positive number")
        double quantity,
        @PastOrPresent(message = "Purchase date must be in the present or in the past")
        @NotNull(message = "Purchase date can't be null")
        LocalDate purchaseDate,
        @FutureOrPresent(message = "Expiration date must be in the future")
        @NotNull(message = "Expiration date can't be null")
        LocalDate expirationDate,
        @NotNull(message = "Storage location can't be null")
        StorageLocation storageLocation
) {
}
