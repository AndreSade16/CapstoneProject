package andreasaderi.capstone.repositories;

import andreasaderi.capstone.entities.IngredientDefinition;
import andreasaderi.capstone.entities.Recipe;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, UUID>, JpaSpecificationExecutor<Recipe> {

    boolean existsByName(@NotBlank(message = "Name field can't be blank") @Size(min = 3, message = "Name field must be at least 3 characters long") String name);

    @Query("""
            SELECT r FROM Recipe r
            JOIN r.ingredients ri
            WHERE ri.ingredientDefinition.ingredientDefinitionId IN :ingredientIds
            GROUP BY r
            ORDER BY COUNT(ri) DESC
            """)
    List<Recipe> findRecipesSortedByMatchingIngredients(@Param("ingredientIds") Set<UUID> ingredientIds);

    List<Recipe> findByIngredientsIngredientDefinition(IngredientDefinition ingredientDefinition);
}
