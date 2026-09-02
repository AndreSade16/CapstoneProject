package andreasaderi.capstone.controllers;

import andreasaderi.capstone.entities.*;
import andreasaderi.capstone.exceptions.ValidationException;
import andreasaderi.capstone.requestDTOs.RecipeDTO;
import andreasaderi.capstone.requestDTOs.RecipeFiltersDTO;
import andreasaderi.capstone.responseDTOs.RecipeCreatedDTO;
import andreasaderi.capstone.responseDTOs.RecipeIngredientsToSlDTO;
import andreasaderi.capstone.services.PantryItemService;
import andreasaderi.capstone.services.RecipeIngredientService;
import andreasaderi.capstone.services.RecipeService;
import andreasaderi.capstone.services.ShoppingListService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/recipes")
public class RecipeController {

    private final RecipeService recipeService;
    private final ShoppingListService shoppingListService;
    private final RecipeIngredientService recipeIngredientService;
    private final PantryItemService pantryItemService;


    public RecipeController(RecipeService recipeService, ShoppingListService shoppingListService, RecipeIngredientService recipeIngredientService, PantryItemService pantryItemService) {
        this.recipeService = recipeService;
        this.shoppingListService = shoppingListService;
        this.recipeIngredientService = recipeIngredientService;
        this.pantryItemService = pantryItemService;
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RecipeCreatedDTO createRecipe(@RequestPart(value = "recipeImage") MultipartFile recipeImage, @ModelAttribute @Validated RecipeDTO body, BindingResult validationResult) {
        if (validationResult.hasErrors()) {
            List<String> errorsList = validationResult.getFieldErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toList();
            throw new ValidationException(errorsList);
        }
        Recipe saved = recipeService.save(body, recipeImage);
        return new RecipeCreatedDTO(saved.getRecipeId());
    }

    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @PostMapping("/{recipeId}/{peopleCount}")
    public RecipeIngredientsToSlDTO putRecipeIngredientsInSl(@AuthenticationPrincipal User user, @PathVariable UUID recipeId, @PathVariable int peopleCount) {

        if (peopleCount > 20) throw new ValidationException("Servings can't be more than 20");

        ShoppingList shoppingList = shoppingListService.findByUserAndActive(user);

        return new RecipeIngredientsToSlDTO(recipeService.putRecipeIngredientsInSl(recipeId, shoppingList, peopleCount));

    }

    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @PostMapping("/{recipeId}/remaining/{peopleCount}")
    public RecipeIngredientsToSlDTO putRemainingRecipeIngredientsInSl(@AuthenticationPrincipal User user, @PathVariable UUID recipeId, @PathVariable int peopleCount) {

        if (peopleCount > 20) throw new ValidationException("Servings can't be more than 20");

        ShoppingList shoppingList = shoppingListService.findByUserAndActive(user);

        List<PantryItem> pantryItems = pantryItemService.findListByUser(user);

        return new RecipeIngredientsToSlDTO(recipeService.putRemainingRecipeIngredientsInSl(pantryItems, recipeId, shoppingList, peopleCount));

    }


    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @PostMapping("/{recipeId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void prepareRecipe(@AuthenticationPrincipal User user, @PathVariable UUID recipeId, @RequestParam @NotNull @Max(20) int peopleCount) {
        Recipe recipe = recipeService.findById(recipeId);
        List<RecipeIngredient> recipeIngredients = recipeIngredientService.findRecipeIngredients(recipeId);
        recipeService.prepareRecipe(user, recipeIngredients, peopleCount);
    }

    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @GetMapping
    public Page<Recipe> findAll(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "visitsCount") String sortBy, @RequestParam(defaultValue = "DESC") Sort.Direction direction, @Valid @ModelAttribute RecipeFiltersDTO filters) {
        return recipeService.findAll(page, size, sortBy, direction, filters);
    }

    @GetMapping("/{recipeId}")
    public Recipe findById(@PathVariable UUID recipeId) {
        return recipeService.findById(recipeId);
    }

    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @GetMapping("/{recipeId}/visit")
    public Recipe visitRecipeById(@PathVariable UUID recipeId) {
        return recipeService.findByIdAndIncrementVisits(recipeId);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @PutMapping("/{recipeId}")
    public Recipe updateById(@PathVariable UUID recipeId, @ModelAttribute @Validated RecipeDTO body, @RequestPart(value = "recipeImage", required = false) MultipartFile recipeImage) {
        return recipeService.updateById(recipeId, body, recipeImage);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @DeleteMapping("/{recipeId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable UUID recipeId) {
        recipeService.delete(recipeId);
    }
}
