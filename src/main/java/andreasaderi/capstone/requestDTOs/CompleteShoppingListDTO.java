package andreasaderi.capstone.requestDTOs;

import java.util.List;

public record CompleteShoppingListDTO(
        List<PurchasedItemDTO> items
) {
}
