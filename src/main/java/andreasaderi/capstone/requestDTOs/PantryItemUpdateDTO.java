package andreasaderi.capstone.requestDTOs;

import andreasaderi.capstone.entities.StorageLocation;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public record PantryItemUpdateDTO(
        @NotNull
        @Positive
        Double quantity,
        @NotNull
        @PastOrPresent
        LocalDate purchaseDate,
        @NotNull
        LocalDate expirationDate,
        @NotNull
        StorageLocation storageLocation
) {
}
