package andreasaderi.capstone.requestDTOs;

import andreasaderi.capstone.entities.StorageLocation;
import andreasaderi.capstone.entities.User;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.LocalDate;

public record PantryItemFiltersDTO(
        String name,
        @PositiveOrZero
        Double minQuantity,
        @PositiveOrZero
        Double maxQuantity,
        LocalDate minPurchaseDate,
        LocalDate maxPurchaseDate,
        LocalDate minExpirationDate,
        LocalDate maxExpirationDate,
        StorageLocation storageLocation,
        User user
) {
}
