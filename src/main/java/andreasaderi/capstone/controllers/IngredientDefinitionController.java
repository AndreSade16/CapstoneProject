package andreasaderi.capstone.controllers;

import andreasaderi.capstone.entities.IngredientDefinition;
import andreasaderi.capstone.exceptions.ValidationException;
import andreasaderi.capstone.requestDTOs.IngredientDefinitionDTO;
import andreasaderi.capstone.requestDTOs.IngredientDefinitionFiltersDTO;
import andreasaderi.capstone.responseDTOs.IngredientDefinitionCreatedDTO;
import andreasaderi.capstone.services.IngredientDefinitionService;
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
@RequestMapping("/ingredients")
public class IngredientDefinitionController {

    private final IngredientDefinitionService ingredientDefinitionService;

    public IngredientDefinitionController(IngredientDefinitionService ingredientDefinitionService) {
        this.ingredientDefinitionService = ingredientDefinitionService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public IngredientDefinitionCreatedDTO createIngredient(@ModelAttribute @Validated IngredientDefinitionDTO body, @RequestPart(value = "ingredientImage") MultipartFile ingredientImage, BindingResult validationResult) {
        if (validationResult.hasErrors()) {
            List<String> errorsList = validationResult.getFieldErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toList();
            throw new ValidationException(errorsList);
        }

        IngredientDefinition saved = ingredientDefinitionService.save(body, ingredientImage);

        return new IngredientDefinitionCreatedDTO(saved.getIngredientDefinitionId());
    }

    @GetMapping
    public Page<IngredientDefinition> findAll(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "category") String sortBy, @RequestParam(defaultValue = "DESC") Sort.Direction direction, @Valid @ModelAttribute IngredientDefinitionFiltersDTO filters) {
        return ingredientDefinitionService.findAll(page, size, sortBy, direction, filters);
    }

    @GetMapping("/{ingredientDefinitionId}")
    public IngredientDefinition findById(@PathVariable UUID ingredientDefinitionId) {
        return ingredientDefinitionService.findById(ingredientDefinitionId);
    }

    @PutMapping("/{ingredientDefinitionId}")
    public IngredientDefinition updateById(@PathVariable UUID ingredientDefinitionId, @ModelAttribute @Validated IngredientDefinitionDTO body, @RequestPart(value = "ingredientImage", required = false) MultipartFile ingredientImage, BindingResult validationResult) {
        if (validationResult.hasErrors()) {
            List<String> errorsList = validationResult.getFieldErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toList();
            throw new ValidationException(errorsList);
        }
        return ingredientDefinitionService.updateById(ingredientDefinitionId, body, ingredientImage);

    }

    @DeleteMapping("/{ingredientDefinitionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable UUID ingredientDefinitionId) {
        ingredientDefinitionService.delete(ingredientDefinitionId);
    }
}
