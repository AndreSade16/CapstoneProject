package andreasaderi.capstone.repositories;

import andreasaderi.capstone.entities.Recipe;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, UUID>, JpaSpecificationExecutor<Recipe> {

    boolean existsByName(@NotBlank(message = "Name field can't be blank") @Size(min = 3, message = "Name field must be at least 3 characters long") String name);
}
