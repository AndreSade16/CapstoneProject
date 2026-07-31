package andreasaderi.capstone.controllers;

import andreasaderi.capstone.entities.Recipe;
import andreasaderi.capstone.exceptions.ValidationException;
import andreasaderi.capstone.requestDTOs.RecipeDTO;
import andreasaderi.capstone.requestDTOs.RecipeFiltersDTO;
import andreasaderi.capstone.responseDTOs.RecipeCreatedDTO;
import andreasaderi.capstone.services.RecipeService;
import jakarta.validation.Valid;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
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


    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

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

    @GetMapping
    public Page<Recipe> findAll(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "visitsCount") String sortBy, @RequestParam(defaultValue = "DESC") Sort.Direction direction, @Valid @ModelAttribute RecipeFiltersDTO filters) {
        return recipeService.findAll(page, size, sortBy, direction, filters);
    }

    @GetMapping("/{recipeId}")
    public Recipe findById(@PathVariable UUID recipeId) {
        return recipeService.findById(recipeId);
    }

    @GetMapping("/{recipeId}/visit")
    public Recipe visitRecipeById(@PathVariable UUID recipeId) {
        return recipeService.findByIdAndIncrementVisits(recipeId);
    }

    @PutMapping("/{recipeId}")
    public Recipe updateById(@PathVariable UUID recipeId, @ModelAttribute @Validated RecipeDTO body, @RequestPart(value = "recipeImage", required = false) MultipartFile recipeImage) {
        return recipeService.updateById(recipeId, body, recipeImage);
    }
}
