package andreasaderi.capstone.repositories;

import andreasaderi.capstone.entities.IngredientDefinition;
import andreasaderi.capstone.entities.Recipe;
import andreasaderi.capstone.entities.RecipeIngredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RecipeIngredientRepository extends JpaRepository<RecipeIngredient, UUID> {
    boolean existsByRecipeAndIngredientDefinition(Recipe recipe, IngredientDefinition ingredientDefinition);
}
