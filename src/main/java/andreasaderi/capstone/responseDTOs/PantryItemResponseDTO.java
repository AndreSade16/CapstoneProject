package andreasaderi.capstone.responseDTOs;

import andreasaderi.capstone.entities.StorageLocation;
import andreasaderi.capstone.entities.Unit;

import java.time.LocalDate;
import java.util.UUID;

public record PantryItemResponseDTO(
        UUID pantryItemId,

        UUID ingredientDefinitionId,

        String ingredientName,

        String imageUrl,

        Double quantity,

        Unit unit,

        StorageLocation storageLocation,

        LocalDate expirationDate,

        long daysUntilExpiration
) {
}
