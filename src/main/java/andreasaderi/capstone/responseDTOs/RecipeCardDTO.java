package andreasaderi.capstone.responseDTOs;

import andreasaderi.capstone.entities.RecipeCost;
import andreasaderi.capstone.entities.RecipeDifficulty;

import java.util.UUID;

public record RecipeCardDTO(

        UUID id,
        String name,
        String imageUrl,
        double totalTime,
        RecipeDifficulty difficulty,
        RecipeCost cost

) {
}
