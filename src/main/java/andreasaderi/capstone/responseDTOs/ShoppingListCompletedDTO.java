package andreasaderi.capstone.responseDTOs;

import andreasaderi.capstone.entities.ShoppingListStatus;

import java.util.UUID;

public record ShoppingListCompletedDTO(
        UUID shoppingListId,
        ShoppingListStatus shoppingListStatus
) {
}