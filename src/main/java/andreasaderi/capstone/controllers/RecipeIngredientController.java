package andreasaderi.capstone.controllers;

import andreasaderi.capstone.entities.RecipeIngredient;
import andreasaderi.capstone.exceptions.ValidationException;
import andreasaderi.capstone.requestDTOs.RecipeIngredientDTO;
import andreasaderi.capstone.responseDTOs.RecipeIngredientCreatedDTO;
import andreasaderi.capstone.services.RecipeIngredientService;
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

    private final RecipeIngredientService recipeIngredientService;


    public RecipeIngredientController(RecipeIngredientService recipeIngredientService) {
        this.recipeIngredientService = recipeIngredientService;
    }

    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @PostMapping("/{recipeId}/ingredients")
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

    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @GetMapping("/{recipeId}/ingredients")
    public List<RecipeIngredient> findRecipeIngredients(@PathVariable UUID recipeId) {
        return recipeIngredientService.findRecipeIngredients(recipeId);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @PutMapping("/{recipeId}/ingredients/{recipeIngredientId}")
    public RecipeIngredient updateRecipeIngredientById(@PathVariable UUID recipeId, @PathVariable UUID recipeIngredientId, @RequestBody RecipeIngredientDTO body) {
        return recipeIngredientService.updateRecipeIngredientById(recipeId, recipeIngredientId, body);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @DeleteMapping("/{recipeId}/ingredients/{recipeIngredientId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable UUID recipeId, @PathVariable UUID recipeIngredientId) {
        recipeIngredientService.delete(recipeId, recipeIngredientId);
    }
}
