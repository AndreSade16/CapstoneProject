package andreasaderi.capstone.services;

import andreasaderi.capstone.entities.IngredientDefinition;
import andreasaderi.capstone.entities.Recipe;
import andreasaderi.capstone.entities.RecipeIngredient;
import andreasaderi.capstone.exceptions.RecordAlreadyExistsException;
import andreasaderi.capstone.repositories.RecipeIngredientRepository;
import andreasaderi.capstone.requestDTOs.RecipeIngredientDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class RecipeIngredientService {

    private final RecipeIngredientRepository recipeIngredientRepository;
    private final IngredientDefinitionService ingredientDefinitionService;
    private final RecipeService recipeService;


    public RecipeIngredientService(RecipeIngredientRepository recipeIngredientRepository, IngredientDefinitionService ingredientDefinitionService, RecipeService recipeService) {
        this.recipeIngredientRepository = recipeIngredientRepository;
        this.ingredientDefinitionService = ingredientDefinitionService;
        this.recipeService = recipeService;
    }

    public RecipeIngredient save(RecipeIngredientDTO body, UUID recipeId) {
        IngredientDefinition ingredientDefinition = ingredientDefinitionService.findById(body.ingredientDefinitionId());
        Recipe recipe = recipeService.findById(recipeId);
        if (recipeIngredientRepository.existsByRecipeAndIngredientDefinition(recipe, ingredientDefinition))
            throw new RecordAlreadyExistsException("Recipe ingredient " + ingredientDefinition.getName() + " already exists in recipe named '" + recipe.getName() + "'. Edit it if you want to change it's quantity per person.");

        return recipeIngredientRepository.save(new RecipeIngredient(recipe, ingredientDefinition, body.quantityPerPerson()));
    }

    public List<RecipeIngredient> findRecipeIngredients(UUID recipeId) {
        Recipe recipe = recipeService.findById(recipeId);
        return recipeIngredientRepository.findByRecipe(recipe);
    }
}
