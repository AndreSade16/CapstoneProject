package andreasaderi.capstone.controllers;

import andreasaderi.capstone.entities.RecipeIngredient;
import andreasaderi.capstone.exceptions.ValidationException;
import andreasaderi.capstone.requestDTOs.RecipeIngredientDTO;
import andreasaderi.capstone.responseDTOs.RecipeIngredientCreatedDTO;
import andreasaderi.capstone.services.RecipeIngredientService;
import andreasaderi.capstone.services.RecipeService;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/recipes")
public class RecipeIngredientController {

    private final RecipeService recipeService;
    private final RecipeIngredientService recipeIngredientService;


    public RecipeIngredientController(RecipeService recipeService, RecipeIngredientService recipeIngredientService) {
        this.recipeService = recipeService;
        this.recipeIngredientService = recipeIngredientService;
    }

    @PostMapping("/{recipeId}/ingredients")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public RecipeIngredientCreatedDTO createRecipeIngredient(@RequestBody @Validated RecipeIngredientDTO body, @PathVariable UUID recipeId, BindingResult validationResult) {
        if (validationResult.hasErrors()) {
            List<String> errorsList = validationResult.getFieldErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toList();
            throw new ValidationException(errorsList);
        }

        RecipeIngredient saved = recipeIngredientService.save(body, recipeId);

        return new RecipeIngredientCreatedDTO(saved.getRecipeIngredientId());
    }

    @GetMapping("/{recipeId}/ingredients")
    public List<RecipeIngredient> findRecipeIngrediens(@PathVariable UUID recipeId) {
        return recipeIngredientService.findRecipeIngredients(recipeId);
    }

    @PutMapping("/{recipeId}/ingredients/{recipeIngredientId}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public RecipeIngredient updateRecipeIngredientById(@PathVariable UUID recipeId, @PathVariable UUID recipeIngredientId, @RequestBody RecipeIngredientDTO body) {
        return recipeIngredientService.updateRecipeIngredientById(recipeId, recipeIngredientId, body);
    }

    @DeleteMapping("/{recipeId}/ingredients/{recipeIngredientId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public void deleteById(@PathVariable UUID recipeId, @PathVariable UUID recipeIngredientId) {
        recipeIngredientService.delete(recipeId, recipeIngredientId);
    }
}
