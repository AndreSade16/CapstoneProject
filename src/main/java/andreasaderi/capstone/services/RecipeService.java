package andreasaderi.capstone.services;

import andreasaderi.capstone.entities.*;
import andreasaderi.capstone.exceptions.ConflictException;
import andreasaderi.capstone.exceptions.NotFoundException;
import andreasaderi.capstone.exceptions.RecordAlreadyExistsException;
import andreasaderi.capstone.repositories.RecipeRepository;
import andreasaderi.capstone.requestDTOs.PantryItemUpdateDTO;
import andreasaderi.capstone.requestDTOs.RecipeDTO;
import andreasaderi.capstone.requestDTOs.RecipeFiltersDTO;
import andreasaderi.capstone.requestDTOs.ShoppingListItemDTO;
import andreasaderi.capstone.responseDTOs.ShoppingListItemCreatedDTO;
import andreasaderi.capstone.specifications.RecipeSpecification;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final RecipeSpecification recipeSpecification;
    private final CloudinaryService cloudinaryService;
    private final PantryItemService pantryItemService;
    private final ShoppingListItemService shoppingListItemService;

    public RecipeService(RecipeRepository recipeRepository, RecipeSpecification recipeSpecification, CloudinaryService cloudinaryService, PantryItemService pantryItemService, ShoppingListItemService shoppingListItemService) {
        this.recipeRepository = recipeRepository;
        this.recipeSpecification = recipeSpecification;
        this.cloudinaryService = cloudinaryService;
        this.pantryItemService = pantryItemService;
        this.shoppingListItemService = shoppingListItemService;
    }

    public Recipe save(RecipeDTO body, MultipartFile recipeImage) {
        if (recipeRepository.existsByName(body.name()))
            throw new RecordAlreadyExistsException("A recipe named '" + body.name() + "' already exists");
        String imageUrl = cloudinaryService.uploadValidatedImageAndGetUrl(recipeImage);


        return recipeRepository.save(new Recipe(body.name(), body.description(), imageUrl, body.preparationTime(), body.cookingTime(), body.difficulty(), body.cost(), body.procedure()));
    }

    public Recipe findById(UUID recipeId) {
        return recipeRepository.findById(recipeId).orElseThrow(() -> new NotFoundException("Recipe with id '" + recipeId + "' not found"));
    }

    public Recipe updateById(UUID recipeId, RecipeDTO body, MultipartFile recipeImage) {
        Recipe recipe = findById(recipeId);

        if (!recipe.getName().equalsIgnoreCase(body.name())
                && recipeRepository.existsByName(body.name())) {
            throw new RecordAlreadyExistsException("Recipe named '" + body.name() + "' already exists");
        }

        if (recipeImage != null && !recipeImage.isEmpty()) {

            String imageUrl = cloudinaryService.uploadValidatedImageAndGetUrl(recipeImage);

            recipe.setImageUrl(imageUrl);
        }

        recipe.setName(body.name());
        recipe.setDescription(body.description());
        recipe.setPreparationTime(body.preparationTime());
        recipe.setCookingTime(body.cookingTime());
        recipe.setDifficulty(body.difficulty());
        recipe.setCost(body.cost());
        recipe.setProcedure(body.procedure());
        recipe.getIngredients().clear();

        return recipeRepository.save(recipe);
    }

    public Recipe findByIdAndIncrementVisits(UUID id) {
        Recipe recipe = findById(id);

        recipe.setVisitsCount(recipe.getVisitsCount() + 1);

        return recipeRepository.save(recipe);
    }

    public Page<Recipe> findAll(int page, int size, String sortBy, Sort.Direction direction, @Valid RecipeFiltersDTO filters) {
        if (size <= 0) size = 10;
        if (size > 20) size = 20;
        if (page < 0) page = 0;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Specification<Recipe> spec = recipeSpecification.specificationRecipeBuilder(filters);


        return recipeRepository.findAll(spec, pageable);
    }

    public List<Recipe> findMostRelevantForUser(User user) {
        Set<UUID> pantryIngredientIds = pantryItemService.findListByUser(user).stream()
                .map(item -> item.getIngredientDefinition().getIngredientDefinitionId())
                .collect(Collectors.toSet());

        return recipeRepository.findRecipesSortedByMatchingIngredients(pantryIngredientIds);
    }

    public void delete(UUID recipeId) {
        Recipe recipe = findById(recipeId);
        recipeRepository.delete(recipe);
    }

    public List<ShoppingListItemCreatedDTO> putRecipeIngredientsInSl(UUID recipeId, ShoppingList shoppingList, int peopleCount) {
        Recipe recipe = findByIdAndIncrementVisits(recipeId);

        List<RecipeIngredient> ingredients = recipe.getIngredients();

        return ingredients.stream().map(ingredient -> new ShoppingListItemCreatedDTO(shoppingListItemService.save(shoppingList, new ShoppingListItemDTO(ingredient.getIngredientDefinition().getIngredientDefinitionId(), ingredient.getQuantityPerPerson() * peopleCount)).getShoppingListItemId())).toList();
    }

    public List<Recipe> findByIngredientsIngredientDefinition(IngredientDefinition ingredientDefinition) {
        return recipeRepository.findByIngredientsIngredientDefinition(ingredientDefinition);
    }

    public void prepareRecipe(User user, List<RecipeIngredient> recipeIngredients, int peopleCount) {

        List<PantryItem> pantryItems = pantryItemService.findListByUser(user);

        Map<UUID, Double> availableQuantities = pantryItems.stream()
                .collect(Collectors.groupingBy(
                        item -> item.getIngredientDefinition().getIngredientDefinitionId(),
                        Collectors.summingDouble(PantryItem::getQuantity)
                ));

        boolean canPrepare = recipeIngredients.stream().allMatch(ri -> {
            UUID ingredientId = ri.getIngredientDefinition().getIngredientDefinitionId();
            double requiredTotal = ri.getQuantityPerPerson() * peopleCount;
            double available = availableQuantities.getOrDefault(ingredientId, 0.0);
            return available >= requiredTotal;
        });

        if (!canPrepare) {
            throw new ConflictException("You don't have enough ingredients in your pantry to prepare this recipe.");
        }

        for (RecipeIngredient ri : recipeIngredients) {
            double amountNeeded = ri.getQuantityPerPerson() * peopleCount;
            UUID ingredientId = ri.getIngredientDefinition().getIngredientDefinitionId();

            List<PantryItem> matchingItems = pantryItems.stream()
                    .filter(item -> item.getIngredientDefinition().getIngredientDefinitionId().equals(ingredientId))
                    .sorted(Comparator.comparing(PantryItem::getExpirationDate))
                    .toList();

            for (PantryItem item : matchingItems) {
                if (amountNeeded <= 0) break;

                if (item.getQuantity() <= amountNeeded) {
                    amountNeeded -= item.getQuantity();
                    pantryItemService.deleteOwnItem(item.getPantryItemId(), user);
                } else {
                    pantryItemService.updateOwnPantryItem(item.getPantryItemId(), new PantryItemUpdateDTO(item.getQuantity() - amountNeeded, item.getPurchaseDate(), item.getExpirationDate(), item.getStorageLocation()), user);
                    amountNeeded = 0;
                }
            }
        }
    }


    public void deleteAll(List<Recipe> recipesWithIngredient) {
        recipeRepository.deleteAll(recipesWithIngredient);
    }
}

