package andreasaderi.capstone.requestDTOs;

import andreasaderi.capstone.entities.StorageLocation;
import andreasaderi.capstone.entities.Unit;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;
import java.util.UUID;

public record PurchasedItemDTO(
        @NotNull(message = "Shopping list item ID can't be null")
        UUID shoppingListItemId,
        @NotNull
        @Positive(message = "Purchased quantity must be a positive number")
        Double purchasedQuantity,
        @NotNull(message = "Purchased unit can't be null")
        Unit purchasedUnit,
        @NotNull
        @Future(message = "Expiration date must be yet to come")
        LocalDate expirationDate,
        @NotNull(message = "Storage location can't be null")
        StorageLocation storageLocation
) {
}
