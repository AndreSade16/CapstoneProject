package andreasaderi.capstone.services;

import andreasaderi.capstone.entities.IngredientDefinition;
import andreasaderi.capstone.entities.Recipe;
import andreasaderi.capstone.entities.RecipeIngredient;
import andreasaderi.capstone.exceptions.ConflictException;
import andreasaderi.capstone.exceptions.NotFoundException;
import andreasaderi.capstone.exceptions.RecordAlreadyExistsException;
import andreasaderi.capstone.repositories.RecipeIngredientRepository;
import andreasaderi.capstone.requestDTOs.RecipeIngredientDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
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
        Optional<RecipeIngredient> optionalRecipeIngredient = recipeIngredientRepository.findByRecipeAndIngredientDefinition(recipe, ingredientDefinition);
        if (optionalRecipeIngredient.isPresent()) {
            RecipeIngredient recipeIngredient = optionalRecipeIngredient.get();
            recipeIngredient.setQuantityPerPerson(body.quantityPerPerson());
            return recipeIngredientRepository.save(recipeIngredient);
        }

        return recipeIngredientRepository.save(new RecipeIngredient(recipe, ingredientDefinition, body.quantityPerPerson()));
    }

    public List<RecipeIngredient> findRecipeIngredients(UUID recipeId) {
        Recipe recipe = recipeService.findById(recipeId);
        return recipeIngredientRepository.findByRecipe(recipe);
    }

    public RecipeIngredient updateRecipeIngredientById(UUID recipeId, UUID recipeIngredientId, RecipeIngredientDTO body) {
        Recipe recipe = recipeService.findById(recipeId);
        RecipeIngredient recipeIngredient = findById(recipeIngredientId);

        if (!recipeIngredient.getRecipe().getRecipeId().equals(recipe.getRecipeId()))
            throw new ConflictException("Recipe ingredient " + recipeIngredient.getIngredientDefinition().getName() + " doesn't belong to recipe " + recipe.getName());
        if (recipeIngredient.getIngredientDefinition().getIngredientDefinitionId().equals(body.ingredientDefinitionId()) && recipeIngredient.getQuantityPerPerson() == body.quantityPerPerson())
            throw new RecordAlreadyExistsException("This ingredient is already present in this exact quantity in this recipe");

        IngredientDefinition newIngredientDefinition = ingredientDefinitionService.findById(body.ingredientDefinitionId());

        if (!recipeIngredient.getIngredientDefinition().getIngredientDefinitionId().equals(newIngredientDefinition.getIngredientDefinitionId()) && recipeIngredientRepository.existsByRecipeAndIngredientDefinition(recipe, newIngredientDefinition))
            throw new RecordAlreadyExistsException("Recipe " + recipe.getName() + " already has " + newIngredientDefinition.getName() + " as it's ingredient");

        recipeIngredient.setIngredientDefinition(newIngredientDefinition);
        recipeIngredient.setQuantityPerPerson(body.quantityPerPerson());

        return recipeIngredientRepository.save(recipeIngredient);

    }

    private RecipeIngredient findById(UUID recipeIngredientId) {
        return recipeIngredientRepository.findById(recipeIngredientId).orElseThrow(() -> new NotFoundException("Recipe ingredient with id '" + recipeIngredientId + "' not found"));
    }

    public void delete(UUID recipeId, UUID recipeIngredientId) {
        RecipeIngredient recipeIngredient = findById(recipeIngredientId);
        Recipe recipe = recipeService.findById(recipeId);
        if (!recipeIngredient.getRecipe().getRecipeId().equals(recipe.getRecipeId()))
            throw new ConflictException("Ingredient " + recipeIngredient.getIngredientDefinition().getName() + " doesn't belong to recipe " + recipe.getName());

        recipeIngredientRepository.delete(recipeIngredient);
    }
}
