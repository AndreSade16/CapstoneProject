package andreasaderi.capstone.responseDTOs;

import andreasaderi.capstone.entities.ShoppingListItem;
import andreasaderi.capstone.entities.ShoppingListStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record ShoppingListResponseDTO(
        UUID shoppingListId,
        LocalDate createdAt,
        LocalDate updatedAt,
        ShoppingListStatus shoppingListStatus,
        List<ShoppingListItem> items
) {
}
