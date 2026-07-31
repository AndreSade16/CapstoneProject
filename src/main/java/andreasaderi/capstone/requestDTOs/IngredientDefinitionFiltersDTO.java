package andreasaderi.capstone.requestDTOs;

import andreasaderi.capstone.entities.Category;
import andreasaderi.capstone.entities.Season;
import jakarta.validation.constraints.Positive;

import java.util.Set;

public record IngredientDefinitionFiltersDTO(
        String name,
        Category category,
        @Positive
        Integer minShelfLifeDays,
        @Positive
        Integer maxShelfLifeDays,
        Set<Season> seasonality
) {
}

