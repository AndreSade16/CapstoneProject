package andreasaderi.capstone.requestDTOs;

import andreasaderi.capstone.entities.RecipeCost;
import andreasaderi.capstone.entities.RecipeDifficulty;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.util.Set;

public record RecipeFiltersDTO(
        String name,
        @PositiveOrZero
        Double minTime,
        @Positive
        Double maxTime,
        Set<RecipeDifficulty> difficulty,
        Set<RecipeCost> cost
) {
}
